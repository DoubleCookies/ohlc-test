package com.testtask.ohlc.services;

import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class OhlcTimestampService {

    public long getMinuteOhlcTimestamp() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTimeInMillis();
    }

    public long getHourOhlcTimestamp() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MINUTE, 0);
        return now.getTimeInMillis();
    }

    public long getDailyOhlcTimestamp() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        return now.getTimeInMillis();
    }
}
