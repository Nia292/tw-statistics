package com.thrallwars.statistics.util.rcon;

import com.thrallwars.statistics.config.RconTarget;
import org.springframework.stereotype.Service;

@Service
public class RconFactory {

    public RconSocket getSocket(RconTarget rconTarget) {
        return new RconSocket(rconTarget);
    }
}
