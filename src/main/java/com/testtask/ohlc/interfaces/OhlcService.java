package com.testtask.ohlc.interfaces;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.model.Ohlc;

import java.util.List;

public interface OhlcService extends QuoteListener {
    /** latest non persisted OHLC */
    Ohlc getCurrent (long instrumentId, OhlcPeriod period);
    /** all OHLCs which are kept in a database */
    List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period);
    /** latest non persisted OHLC and OHLCs which are kept in a database */
    List<Ohlc> getHistoricalAndCurrent (long instrumentId, OhlcPeriod period);
}
