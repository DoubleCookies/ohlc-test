package com.testtask.ohlc.model;

import com.testtask.ohlc.enums.OhlcPeriod;

public class OhlcStorage {

    private Ohlc minuteOhlc;
    private Ohlc hourOhlc;
    private Ohlc dailyOhlc;

    public Ohlc getMinuteOhlc() {
        return minuteOhlc;
    }

    public void setMinuteOhlc(Ohlc minuteOhlc) {
        this.minuteOhlc = minuteOhlc;
    }

    public Ohlc getHourOhlc() {
        return hourOhlc;
    }

    public void setHourOhlc(Ohlc hourOhlc) {
        this.hourOhlc = hourOhlc;
    }

    public Ohlc getDailyOhlc() {
        return dailyOhlc;
    }

    public void setDailyOhlc(Ohlc dailyOhlc) {
        this.dailyOhlc = dailyOhlc;
    }

    public OhlcStorage() {
        this.minuteOhlc = new Ohlc(OhlcPeriod.M1);
        this.hourOhlc = new Ohlc(OhlcPeriod.H1);
        this.dailyOhlc = new Ohlc(OhlcPeriod.D1);
    }
}
