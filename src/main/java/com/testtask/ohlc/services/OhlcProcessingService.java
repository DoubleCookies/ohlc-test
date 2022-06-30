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

    @Autowired
    public OhlcProcessingService(OhlcStoreService ohlcStoreService) {
        this.ohlcStoreService = ohlcStoreService;
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

    public void processMinuteOhlcAfterPeriod() {
        updateHourOhlcBeforeSavingMinuteOhlc();
        updateDailyOhlcBeforeSavingHourOhlc();
        storeAllMinuteOhlc();
        clearMinuteOhlc();
    }

    public void processHourOhlcAfterPeriod() {
        updateDailyOhlcBeforeSavingHourOhlc();
        storeAllHourOhlc();
        clearHourOhlc();
    }

    public void processDailyOhlcAfterPeriod() {
        storeAllDailyOhlc();
        clearDailyOhlc();
    }

    /**
     * Update data of hour Ohlc with info from minute Ohlc
     */
    public void updateHourOhlcBeforeSavingMinuteOhlc() {
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
    public void updateDailyOhlcBeforeSavingHourOhlc() {
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

    public void storeAllMinuteOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getMinuteOhlc();
            if (ohlc.isOhlcWithPrice()) {
                storeOhlc(ohlc);
            }
        }
    }

    public void storeAllHourOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getHourOhlc();
            if (ohlc.isOhlcWithPrice()) {
                storeOhlc(ohlc);
            }
        }
    }

    public void storeAllDailyOhlc() {
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
        }
    }

    private void clearHourOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getHourOhlc();
            ohlc.clearOhlc();
        }
    }

    private void clearDailyOhlc() {
        for (Map.Entry<Long, OhlcStorage> entry : instrumentsDataStorage.entrySet()) {
            Ohlc ohlc = entry.getValue().getDailyOhlc();
            ohlc.clearOhlc();
        }
    }
}
