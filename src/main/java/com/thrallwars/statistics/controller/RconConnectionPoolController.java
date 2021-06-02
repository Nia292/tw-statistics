package com.thrallwars.statistics.controller;

import com.thrallwars.statistics.util.rcon.RconConnectionPool;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pooling")
public class RconConnectionPoolController {

    private final RconConnectionPool rconConnectionPool;

    public RconConnectionPoolController(RconConnectionPool rconConnectionPool) {
        this.rconConnectionPool = rconConnectionPool;
    }

    @GetMapping(path = "size")
    public int getCurrentConnections() {
        return rconConnectionPool.getPoolSize();
    }

    @DeleteMapping()
    public void triggerCleanUp() {
        rconConnectionPool.cleanup();
    }
}
