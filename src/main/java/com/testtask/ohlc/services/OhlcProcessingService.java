package com.testtask.ohlc.services;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.interfaces.OhlcService;
import com.testtask.ohlc.interfaces.Quote;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.model.OhlcStorage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OhlcProcessingService implements OhlcService {
    private Map<Long, OhlcStorage> instrumentsDataStorage = new HashMap<>();

    public Map<Long, OhlcStorage> getInstrumentsDataStorage() {
        return instrumentsDataStorage;
    }

    @Override
    public Ohlc getCurrent(long instrumentId, OhlcPeriod period) {
        return null;
    }

    @Override
    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        return null;
    }

    @Override
    public List<Ohlc> getHistoricalAndCurrent(long instrumentId, OhlcPeriod period) {
        return null;
    }

    @Override
    public void onQuote(Quote quote) {

    }
}
