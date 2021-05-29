package com.thrallwars.statistics.config;

import lombok.Data;

@Data
public class RconTarget {
    private String name;
    private String host;
    private Integer port;
    private String password;
    private boolean gather;
}
