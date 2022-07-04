package com.testtask.ohlc.services;

import com.testtask.ohlc.interfaces.Quote;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true, havingValue = "true")
public class KafkaListenerService {

    private final OhlcProcessingService ohlcProcessingService;

    public KafkaListenerService(OhlcProcessingService ohlcProcessingService) {
        this.ohlcProcessingService = ohlcProcessingService;
    }

    @KafkaListener(
            topics = "ohlc",
            containerFactory = "greetingKafkaListenerContainerFactory")
    public void greetingListener(Quote quote) {
        ohlcProcessingService.onQuote(quote);
    }
}
