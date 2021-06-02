package com.thrallwars.statistics.util.rcon;

import com.thrallwars.statistics.config.RconTarget;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Semaphore;


@Service
@Log4j2
public class RconConnectionPool {

    private static class SocketPoolEntry {
        private final String serverName;
        private final RconSocket socket;
        private final Semaphore lock;
        private Date lastUse;

        private SocketPoolEntry(String serverName, RconSocket socket) {
            this.serverName = serverName;
            this.socket = socket;
            this.lock = new Semaphore(1);
            lastUse = new Date();
        }

        private boolean acquiredSocket(String serverName) {
            return serverName.equals(this.serverName) && lock.tryAcquire();
        }

        private void renewStaleTimer() {
            this.lastUse = new Date();
        }
    }

    private final List<SocketPoolEntry> sockets = Collections.synchronizedList(new ArrayList<>());

    public int getPoolSize() {
        return sockets.size();
    }

    public void cleanup() {
        sockets.removeIf(socket -> {
            long ageInSeconds = ChronoUnit.SECONDS.between(socket.lastUse.toInstant(), new Date().toInstant());
            if (ageInSeconds >= 180) {
                // Consider socket as stale -> kick out
                socket.socket.disconnect(); // make sure to disconnect it before kicking it out!
                log.info("Socket {} has been stale for {} seconds, removing from pool", socket.socket.getServerName(), ageInSeconds);
                return true;
            }
            return false;
        });
    }

    @Scheduled(fixedDelay = 30 * 1000) // Clean our pool every 30 seconds
    public void automatedCleanup() {
        log.debug("Running scheduled pool cleanup");
        cleanup();
        log.info("Sockets after cleanup: {}", getPoolSize());
    }


    /**
     * Executes an rcon in a connectin drawn from a common connection pool to allow re-using of connections
     */
    public String executePooled(RconTarget rconTarget, String msg) {
        // Find a socket available
        SocketPoolEntry entry = sockets.stream()
                .filter(e -> e.acquiredSocket(rconTarget.getName()))
                .findAny()
                .orElseGet(() -> {
                    log.info("No socket availabe for target {}, creating new one", rconTarget.getName());
                    RconSocket rconSocket = new RconSocket(rconTarget);
                    log.info("Connecting to {} for authentication of new socket", rconSocket.getServerName());
                    rconSocket.connect();
                    log.info("Authentication successful.");
                    SocketPoolEntry newEntry = new SocketPoolEntry(rconSocket.getServerName(), rconSocket);
                    newEntry.acquiredSocket(rconTarget.getName());
                    sockets.add(newEntry);
                    return newEntry;
                });
        // Now we are guranteed to have an rcon socket for ourselves.
        RconSocket socket = entry.socket;
        // Execute request without closing the connection or authenticating
        String response = socket.executeInConnection(msg);
        // Make sure the connection is no longer stale
        entry.renewStaleTimer();
        // Release the lock
        entry.lock.release();
        // And return our response
        return response;
    }

    public RconSocket getSocket(RconTarget rconTarget) {
        return new RconSocket(rconTarget);
    }
}
