package com.thrallwars.statistics;

import com.thrallwars.statistics.service.DiscordWebhookService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Instant;

@SpringBootApplication
@EnableConfigurationProperties
@Log4j2
@EnableScheduling
@EnableAsync
public class StatisticsApplication implements InitializingBean {

    private final DiscordWebhookService discordWebhookService;

    public StatisticsApplication(DiscordWebhookService discordWebhookService) {
        this.discordWebhookService = discordWebhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        discordWebhookService.publishInfo("Application started @ " + Instant.now().toString() + "(UTC)");
    }
}
