package com.testtask.ohlc.services;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.interfaces.OhlcDao;
import com.testtask.ohlc.model.Ohlc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class OhlcStoreService {

    private final OhlcDao ohlcDao;

    @Autowired
    public OhlcStoreService(OhlcDao ohlcDao) {
        this.ohlcDao = ohlcDao;
    }

    public void storeOhlc(Ohlc ohlc) {
        ohlcDao.store(ohlc);
    }

    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        return ohlcDao.getHistorical(instrumentId, period);
    }
}
