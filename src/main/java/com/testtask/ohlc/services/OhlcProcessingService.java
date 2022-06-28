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
            setInitOhlcQuote(storage.getMinuteOhlc(), quotePrice);
            instrumentsDataStorage.put(instrumentId, storage);
        } else {
            OhlcStorage storage = instrumentsDataStorage.get(instrumentId);
            updateOhlcQuote(storage.getMinuteOhlc(), quotePrice);
        }
    }

    private OhlcStorage initOhlcStorageForInstrument() {
        return new OhlcStorage();
    }

    /**
     * Init all OHLC prices - if there is only 1 quote close == open and low == high
     *
     * @param ohlc  OhlcStorage for current instrument
     * @param price Incoming price
     */
    private void setInitOhlcQuote(Ohlc ohlc, double price) {
        ohlc.setOpenPrice(price);
        ohlc.setClosePrice(price);
        ohlc.setHighPrice(price);
        ohlc.setLowPrice(price);
    }

    /**
     * Update OHLC - new close price is guaranteed, high and low requires additional check
     *
     * @param ohlc  OhlcStorage for current instrument
     * @param price Incoming price
     */
    private void updateOhlcQuote(Ohlc ohlc, double price) {
        ohlc.setClosePrice(price);
        if (price < ohlc.getLowPrice()) {
            ohlc.setLowPrice(price);
            return;
        }
        if (price > ohlc.getHighPrice()) {
            ohlc.setHighPrice(price);
        }
    }

    public void updateHourOhlcAfterSavingMinuteOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc minuteOhlc = entry.getValue().getMinuteOhlc();
            Ohlc hourOhlc = entry.getValue().getHourOhlc();
            if (minuteOhlc.isOhlcWithPrice()) {
                if (hourOhlc.getOpenPrice() == 0) {
                    initUpdatedOhlc(hourOhlc, minuteOhlc);
                }
            }
        }
    }

    private void initUpdatedOhlc(Ohlc oldOhlc, Ohlc newOhlc) {
        oldOhlc.setOpenPrice(newOhlc.getOpenPrice());
        oldOhlc.setClosePrice(newOhlc.getClosePrice());
        oldOhlc.setHighPrice(newOhlc.getHighPrice());
        oldOhlc.setLowPrice(newOhlc.getLowPrice());
    }
}
