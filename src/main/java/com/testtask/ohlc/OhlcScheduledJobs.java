package com.testtask.ohlc;

import com.testtask.ohlc.services.OhlcProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

public class OhlcScheduledJobs {

    @Autowired
    private OhlcProcessingService ohlcProcessingService;

    @Scheduled(cron = "0 * * * * *")
    public void scheduleMinuteProcessing() {
        System.out.println("Minute processing - " + new Date());
        ohlcProcessingService.processMinuteOhlcAfterPeriod();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void scheduleHourProcessing() {
        System.out.println("Hour processing - " + new Date());
        ohlcProcessingService.processHourOhlcAfterPeriod();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyProcessing() {
        System.out.println("Daily processing" + new Date());
        ohlcProcessingService.processDailyOhlcAfterPeriod();
    }
}
