package com.testtask.ohlc.services;

import com.testtask.ohlc.interfaces.OhlcDao;
import com.testtask.ohlc.model.Ohlc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
}
