package com.testtask.ohlc.services;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.interfaces.OhlcDao;
import com.testtask.ohlc.model.Ohlc;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Dummy implementation of OhlcDao - actual implementation is already coded by co-workers
 */
@Service
public class OhlcDaoImpl implements OhlcDao {

    @Override
    public void store(Ohlc ohlc) {

    }

    @Override
    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        return null;
    }
}
