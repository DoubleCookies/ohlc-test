package com.testtask.ohlc;

import com.testtask.ohlc.interfaces.Quote;
import com.testtask.ohlc.model.TestQuoteObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuotesGenerator {

    private final Random random = new Random();

    public List<Quote> createSingleInstrumentQuotes(int count, long instrumentId) {
        List<Quote> quoteList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double price = random.nextDouble() * 100.0;
            TestQuoteObject quoteObject = new TestQuoteObject(price, instrumentId, System.currentTimeMillis());
            quoteList.add(quoteObject);
        }
        return quoteList;
    }

    public List<Quote> createMultipleInstrumentQuotes(int count, long... instrumentId) {
        List<Quote> quoteList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double price = random.nextDouble() * 100.0;
            TestQuoteObject quoteObject = new TestQuoteObject(price, random.nextInt(instrumentId.length), System.currentTimeMillis());
            quoteList.add(quoteObject);
        }
        return quoteList;
    }
}
