package com.thrallwars.statistics.util.rcon;

import org.springframework.stereotype.Service;

@Service
public class RconFactory {

    public RconSocket getSocket(String server, int port, String password) {
        return new RconSocket(server, port, password);
    }
}
