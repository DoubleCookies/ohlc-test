package com.testtask.ohlc.services;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.interfaces.OhlcService;
import com.testtask.ohlc.interfaces.Quote;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.model.OhlcStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OhlcProcessingService implements OhlcService {

    private final OhlcStoreService ohlcStoreService;
    private final OhlcTimestampService ohlcTimestampService;

    @Autowired
    public OhlcProcessingService(OhlcStoreService ohlcStoreService, OhlcTimestampService ohlcTimestampService) {
        this.ohlcStoreService = ohlcStoreService;
        this.ohlcTimestampService = ohlcTimestampService;
    }

    private final Map<Long, OhlcStorage> instrumentsDataStorage = new HashMap<>();

    public Map<Long, OhlcStorage> getInstrumentsDataStorage() {
        return instrumentsDataStorage;
    }

    @Override
    public Ohlc getCurrent(long instrumentId, OhlcPeriod period) {
        if (instrumentsDataStorage.containsKey(instrumentId)) {
            OhlcStorage storage = instrumentsDataStorage.get(instrumentId);
            switch (period) {
                case M1: return storage.getMinuteOhlc();
                case H1: return storage.getHourOhlc();
                case D1: return storage.getDailyOhlc();
                default: return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        return ohlcStoreService.getHistorical(instrumentId, period);
    }

    @Override
    public List<Ohlc> getHistoricalAndCurrent(long instrumentId, OhlcPeriod period) {
        Ohlc currentOhlc = getCurrent(instrumentId, period);
        List<Ohlc> historicalOhlc = getHistorical(instrumentId, period);

        // Current Ohlc should be first because it has biggest timestamp and historical Ohlc are sorted
        // in descending order
        if (currentOhlc != null && historicalOhlc != null)
            historicalOhlc.add(0, currentOhlc);

        return historicalOhlc;
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

    /**
     * Init OhlcStorage for all OHLC
     * @return OhlcStorageObject with minute, hour and year OHLC
     */
    private OhlcStorage initOhlcStorageForInstrument() {
        OhlcStorage storage = new OhlcStorage();
        storage.getMinuteOhlc().setPeriodStartUtcTimestamp(ohlcTimestampService.getMinuteOhlcTimestamp());
        storage.getHourOhlc().setPeriodStartUtcTimestamp(ohlcTimestampService.getHourOhlcTimestamp());
        storage.getDailyOhlc().setPeriodStartUtcTimestamp(ohlcTimestampService.getDailyOhlcTimestamp());
        return storage;
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
        if (ohlc.getOpenPrice() == 0) {
            ohlc.setOpenPrice(price);
            // also set low price because it's first price
            ohlc.setLowPrice(price);
        }
        ohlc.setClosePrice(price);
        if (price < ohlc.getLowPrice()) {
            ohlc.setLowPrice(price);
            return;
        }
        if (price > ohlc.getHighPrice()) {
            ohlc.setHighPrice(price);
        }
    }

    /**
     * Update hour & daily OHLC, store and clear minute OHLC
     */
    public void processMinuteOhlcAfterPeriod() {
        updateHourOhlcBeforeSavingMinuteOhlc();
        updateDailyOhlcBeforeSavingHourOhlc();
        storeAllMinuteOhlc();
        clearMinuteOhlc();
    }

    /**
     * Update daily OHLC, store and clear hour OHLC
     */
    public void processHourOhlcAfterPeriod() {
        updateDailyOhlcBeforeSavingHourOhlc();
        storeAllHourOhlc();
        clearHourOhlc();
    }

    /**
     * Store and clear daily OHLC
     */
    public void processDailyOhlcAfterPeriod() {
        storeAllDailyOhlc();
        clearDailyOhlc();
    }

    /**
     * Update data of hour Ohlc with info from minute Ohlc
     */
    private void updateHourOhlcBeforeSavingMinuteOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc minuteOhlc = entry.getValue().getMinuteOhlc();
            Ohlc hourOhlc = entry.getValue().getHourOhlc();
            if (minuteOhlc.isOhlcWithPrice()) {
                if (hourOhlc.getOpenPrice() == 0) {
                    initUpdatedOhlc(hourOhlc, minuteOhlc);
                } else {
                    updateOhlc(hourOhlc, minuteOhlc);
                }
            }
        }
    }

    /**
     * Update data of daily Ohlc with info from hour Ohlc
     */
    private void updateDailyOhlcBeforeSavingHourOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc hourOhlc = entry.getValue().getHourOhlc();
            Ohlc dailyOhlc = entry.getValue().getDailyOhlc();
            if (hourOhlc.isOhlcWithPrice()) {
                if (dailyOhlc.getOpenPrice() == 0) {
                    initUpdatedOhlc(dailyOhlc, hourOhlc);
                } else {
                    updateOhlc(dailyOhlc, hourOhlc);
                }
            }
        }
    }

    /**
     * Init "long" Ohlc with data from "short" Ohlc
     *
     * @param longOhlc  updated Ohlc (hour/day)
     * @param shortOhlc short Ohlc (minute/hour)
     */
    private void initUpdatedOhlc(Ohlc longOhlc, Ohlc shortOhlc) {
        longOhlc.setOpenPrice(shortOhlc.getOpenPrice());
        longOhlc.setClosePrice(shortOhlc.getClosePrice());
        longOhlc.setHighPrice(shortOhlc.getHighPrice());
        longOhlc.setLowPrice(shortOhlc.getLowPrice());
    }

    /**
     * Update "long" Ohlc with data from "short" Ohlc
     *
     * @param longOhlc  updated Ohlc (hour/day)
     * @param shortOhlc short Ohlc (minute/hour)
     */
    private void updateOhlc(Ohlc longOhlc, Ohlc shortOhlc) {
        longOhlc.setClosePrice(shortOhlc.getClosePrice());
        if (shortOhlc.getLowPrice() < longOhlc.getLowPrice())
            longOhlc.setLowPrice(shortOhlc.getLowPrice());
        if (shortOhlc.getHighPrice() > longOhlc.getHighPrice())
            longOhlc.setHighPrice(shortOhlc.getHighPrice());
    }

    private void storeAllMinuteOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getMinuteOhlc();
            if (ohlc.isOhlcWithPrice()) {
                storeOhlc(ohlc);
            }
        }
    }

    private void storeAllHourOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getHourOhlc();
            if (ohlc.isOhlcWithPrice()) {
                storeOhlc(ohlc);
            }
        }
    }

    private void storeAllDailyOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getDailyOhlc();
            if (ohlc.isOhlcWithPrice()) {
                storeOhlc(ohlc);
            }
        }
    }

    private void storeOhlc(Ohlc ohlc) {
        ohlcStoreService.storeOhlc(ohlc);
    }

    private void clearMinuteOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getMinuteOhlc();
            ohlc.clearOhlc();
            ohlc.setPeriodStartUtcTimestamp(ohlcTimestampService.getMinuteOhlcTimestamp());
        }
    }

    private void clearHourOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getHourOhlc();
            ohlc.clearOhlc();
            ohlc.setPeriodStartUtcTimestamp(ohlcTimestampService.getHourOhlcTimestamp());
        }
    }

    private void clearDailyOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getDailyOhlc();
            ohlc.clearOhlc();
            ohlc.setPeriodStartUtcTimestamp(ohlcTimestampService.getDailyOhlcTimestamp());
        }
    }
}
