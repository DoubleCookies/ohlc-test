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
        long instrumentId = quote.getInstrumentId();
        double quotePrice = quote.getPrice();

        if (!instrumentsDataStorage.containsKey(instrumentId)) {
            OhlcStorage storage = initOhlcStorageForInstrument();
            setInitOhlcMinuteQuote(storage, quotePrice);
            instrumentsDataStorage.put(instrumentId, storage);
        } else {
            OhlcStorage storage = instrumentsDataStorage.get(instrumentId);
            updateOhlcMinuteQuote(storage, quotePrice);
        }
    }

    private OhlcStorage initOhlcStorageForInstrument() {
        return new OhlcStorage();
    }

    /**
     * Init all OHLC prices - if there is only 1 quote close == open and low == high
     * @param storage OhlcStorage for current instrument
     * @param price Incoming price
     */
    private void setInitOhlcMinuteQuote(OhlcStorage storage, double price) {
        storage.getMinuteOhlc().setOpenPrice(price);
        storage.getMinuteOhlc().setClosePrice(price);
        storage.getMinuteOhlc().setHighPrice(price);
        storage.getMinuteOhlc().setLowPrice(price);
    }

    /**
     * Update OHLC - new close price is guaranteed, high and low requires additional check
     * @param storage OhlcStorage for current instrument
     * @param price Incoming price
     */
    private void updateOhlcMinuteQuote(OhlcStorage storage, double price) {
        storage.getMinuteOhlc().setClosePrice(price);
        if (price < storage.getMinuteOhlc().getLowPrice()) {
            storage.getMinuteOhlc().setLowPrice(price);
            return;
        }
        if (price > storage.getMinuteOhlc().getHighPrice()) {
            storage.getMinuteOhlc().setHighPrice(price);
        }
    }
}
