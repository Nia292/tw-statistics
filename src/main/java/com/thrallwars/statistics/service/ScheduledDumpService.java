package com.thrallwars.statistics.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ScheduledDumpService {

    private final StatisticsService statisticsService;
    private final DiscordWebhookService discordWebhookService;

    public ScheduledDumpService(StatisticsService statisticsService, DiscordWebhookService discordWebhookService) {
        this.statisticsService = statisticsService;
        this.discordWebhookService = discordWebhookService;
    }

    /**
     * 01:00 and 13:00, times when the server is usually not so busy
     */
    @Scheduled(cron = "0 0 13,1 * * *")
    public void createDump() {
        this.discordWebhookService.publishInfo("Running scheduled data dump of all 3 servers @ "  + Instant.now().toString());
        statisticsService.createDataDump();
        this.discordWebhookService.publishInfo("Data dump completed @ " + Instant.now().toString());
    }

    @Scheduled(cron = "0 15,30,45 * * * *")
    public void createOnlinePlayersDump() {
        statisticsService.createOnlinePlayersDump();
        this.discordWebhookService.publishInfo("Collected online players @ " + Instant.now().toString());
    }

    @Scheduled(cron = "0 15 13,1 * * *")
    public void uploadDataDump() {
        statisticsService.uploadZippedDumpToDiscord();
    }
}
