package com.testtask.ohlc.config;

import com.testtask.ohlc.OhlcScheduledJobs;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class OhlcScheduledJobsConfig {

    @Bean
    @ConditionalOnProperty(value = "jobs.enabled", matchIfMissing = true, havingValue = "true")
    public OhlcScheduledJobs scheduledJobs() {
        return new OhlcScheduledJobs();
    }
}
