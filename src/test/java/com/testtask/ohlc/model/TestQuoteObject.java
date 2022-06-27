package com.testtask.ohlc.model;

import com.testtask.ohlc.interfaces.Quote;

public class TestQuoteObject implements Quote {

    private double price;
    private long instrumentId;
    private long utcTimestamp;

    @Override
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(long instrumentId) {
        this.instrumentId = instrumentId;
    }

    @Override
    public long getUtcTimestamp() {
        return utcTimestamp;
    }

    public void setUtcTimestamp(long utcTimestamp) {
        this.utcTimestamp = utcTimestamp;
    }
}
