package com.thrallwars.statistics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "thrallwars")
public class ServiceConfig {
    private List<RconTarget> targets = new ArrayList<>();

    public RconTarget findTarget(String name) {
        return targets.stream()
                .filter(rconTarget -> rconTarget.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Target " + name + " not configured!"));
    }
}
