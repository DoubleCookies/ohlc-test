package com.testtask.ohlc.interfaces;

public interface Quote {
    double getPrice();
    long getInstrumentId();
    long getUtcTimestamp();
}
