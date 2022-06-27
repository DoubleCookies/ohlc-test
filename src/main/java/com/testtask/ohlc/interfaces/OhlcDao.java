package com.testtask.ohlc.interfaces;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.model.Ohlc;

import java.util.List;

/** already implemented by your co-workers */
public interface OhlcDao {
    void store(Ohlc ohlc);
    /** loads OHLCs from DB selected by parameters and sorted by
     periodStartUtcTimestamp in descending order */
    List<Ohlc> getHistorical (long instrumentId, OhlcPeriod period);
}
