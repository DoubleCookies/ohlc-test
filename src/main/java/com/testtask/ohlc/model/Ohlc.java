package com.testtask.ohlc.model;

import com.testtask.ohlc.enums.OhlcPeriod;

public class Ohlc {
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private OhlcPeriod ohlcPeriod;
    private long periodStartUtcTimestamp;

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public OhlcPeriod getOhlcPeriod() {
        return ohlcPeriod;
    }

    public void setOhlcPeriod(OhlcPeriod ohlcPeriod) {
        this.ohlcPeriod = ohlcPeriod;
    }

    public long getPeriodStartUtcTimestamp() {
        return periodStartUtcTimestamp;
    }

    public void setPeriodStartUtcTimestamp(long periodStartUtcTimestamp) {
        this.periodStartUtcTimestamp = periodStartUtcTimestamp;
    }
}
