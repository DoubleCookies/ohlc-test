package com.testtask.ohlc.model;

import com.testtask.ohlc.enums.OhlcPeriod;

public class Ohlc {
    private double openPrice;
    private double highPrice;
    private double lowPrice;
    private double closePrice;
    private OhlcPeriod ohlcPeriod;
    private long periodStartUtcTimestamp;

    public Ohlc(OhlcPeriod ohlcPeriod) {
        this.ohlcPeriod = ohlcPeriod;
    }

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

    /**
     * Checks if Ohlc was "active" - because openPrice will be always updated if Ohlc was used
     * @return true, if price != 0
     */
    public boolean isOhlcWithPrice() {
        return openPrice != 0;
    }

    /**
     * Clear all prices in Ohlc
     */
    public void clearOhlc() {
        closePrice = 0;
        openPrice = 0;
        lowPrice = 0;
        highPrice = 0;
    }


}
