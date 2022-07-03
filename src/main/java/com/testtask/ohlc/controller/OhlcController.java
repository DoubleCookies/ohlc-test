package com.testtask.ohlc.controller;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.services.OhlcProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OhlcController {

    private final OhlcProcessingService ohlcProcessingService;

    @Autowired
    public OhlcController(OhlcProcessingService ohlcProcessingService) {
        this.ohlcProcessingService = ohlcProcessingService;
    }

    @PostMapping(value = "/getCurrent")
    public Ohlc getCurrent(@RequestParam long instrumentId, @RequestParam OhlcPeriod period) {
        return ohlcProcessingService.getCurrent(instrumentId, period);
    }

    @PostMapping(value = "getHistorical")
    public List<Ohlc> getHistorical(@RequestParam long instrumentId, @RequestParam OhlcPeriod period) {
        return ohlcProcessingService.getHistorical(instrumentId, period);
    }

    @PostMapping(value = "getHistoricalAndCurrent")
    public List<Ohlc> getHistoricalAndCurrent(@RequestParam long instrumentId, @RequestParam OhlcPeriod period) {
        return ohlcProcessingService.getHistoricalAndCurrent(instrumentId, period);
    }
}
