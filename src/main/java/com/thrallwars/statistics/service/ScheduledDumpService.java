package com.thrallwars.statistics.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledDumpService {

    private final WalletStatisticsService walletStatisticsService;

    public ScheduledDumpService(WalletStatisticsService walletStatisticsService) {
        this.walletStatisticsService = walletStatisticsService;
    }

    @Scheduled(cron = "0 0 6,22 * * *")
    public void createDump() {
        walletStatisticsService.createDataDump();
    }
}
