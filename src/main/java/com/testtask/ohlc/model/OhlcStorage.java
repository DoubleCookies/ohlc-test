package com.testtask.ohlc.model;

import com.testtask.ohlc.enums.OhlcPeriod;

public class OhlcStorage {

    private final Ohlc minuteOhlc;
    private final Ohlc hourOhlc;
    private final Ohlc dailyOhlc;

    public Ohlc getMinuteOhlc() {
        return minuteOhlc;
    }

    public Ohlc getHourOhlc() {
        return hourOhlc;
    }

    public Ohlc getDailyOhlc() {
        return dailyOhlc;
    }

    public OhlcStorage() {
        this.minuteOhlc = new Ohlc(OhlcPeriod.M1);
        this.hourOhlc = new Ohlc(OhlcPeriod.H1);
        this.dailyOhlc = new Ohlc(OhlcPeriod.D1);
    }
}
