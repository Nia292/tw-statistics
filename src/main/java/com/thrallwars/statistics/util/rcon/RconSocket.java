package com.thrallwars.statistics.util.rcon;

import com.thrallwars.statistics.config.RconTarget;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class RconSocket {

    // Each packet needs a unique incrementing ID
    private final AtomicInteger idGenerator = new AtomicInteger();

    private final String server;
    private final int port;
    private final String password;
    @Getter
    private final String serverName;

    private Socket socket;
    private DataOutputStream tcpOut;
    private DataInputStream tcpIn;

    public RconSocket(String server, int port, String password, String serverName) {
        this.server = server;
        this.port = port;
        this.password = password;
        this.serverName = serverName;
    }

    public RconSocket(RconTarget rconTarget) {
        this.server = rconTarget.getHost();
        this.port = rconTarget.getPort();
        this.password = rconTarget.getPassword();
        this.serverName = rconTarget.getName();
    }


    @SneakyThrows
    private void connect() {
        socket = new Socket(server, port);
        tcpOut = new DataOutputStream(socket.getOutputStream());
        tcpIn = new DataInputStream(socket.getInputStream());
        log.debug("Sending login RCON");
        List<RConPacket> responses = executeRcon(password, 3);
        // Expect exactly one response for auth request
        RConPacket response = responses.iterator().next();
        log.debug("Login RCON: {}", response.message);
        log.debug("Login RCON: id {} and type {}", response.id, response.type);
        if (response.id == -1) {
            throw new InvalidRconPackageException("Wrong password");
        }
        log.debug("Established connection to {}:{}", server, port);
    }

    @SneakyThrows
    private void disconnect() {
        tcpIn.close();
        tcpOut.close();
        socket.close();
    }

    @SneakyThrows
    private RConPacket readSinglePacket() {
        // Read the header
        byte[] size = new byte[4];
        tcpIn.readFully(size, 0, 4);
        log.debug("Size header bytes {}", size);
        int packageSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN).getInt();
        log.debug("Read package of size {}", packageSize);
        byte [] remainingBytes = new byte[packageSize];
        tcpIn.readFully(remainingBytes, 0, packageSize);
        log.debug("RCon payload size {}", remainingBytes.length);
        log.debug("Total package size {}", (remainingBytes.length + 4));
        byte[] fullPackage = ByteBuffer.allocate((remainingBytes.length + 4))
                .put(size)
                .put(remainingBytes)
                .array();
        return RConPacket.fromBytes(fullPackage);
    }

    @SneakyThrows
    private List<RConPacket> executeRcon(String message, int type) {
        int packageId = idGenerator.getAndIncrement();
        var packet = new RConPacket(message, type, packageId);
        var data = packet.toBytes();
        tcpOut.write(data);
        tcpOut.flush();
        List<RConPacket> responsePackages = new ArrayList<>();
        // First we want to read the initial packet
        RConPacket firstPacket = readSinglePacket();
        responsePackages.add(firstPacket);
        // We might have more packets pending, check for more
        while (tcpIn.available() > 0) {
           responsePackages.add(readSinglePacket());
        }
        return responsePackages;
    }

    public String executeInConnection(String request) {
        connect();
        String response = executeRcon(request, 2)
                .stream()
                .map(rConPacket -> rConPacket.message)
                .collect(Collectors.joining());
        disconnect();
        return response;
    }
}
