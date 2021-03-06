package com.testtask.ohlc;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.interfaces.Quote;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.model.OhlcStorage;
import com.testtask.ohlc.model.TestQuoteObject;
import com.testtask.ohlc.services.OhlcProcessingService;
import com.testtask.ohlc.services.QuotesGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "jobs.enabled=false")
class OhlcApplicationTests {

    @Autowired
    private OhlcProcessingService ohlcProcessingService;

    @Autowired
    private QuotesGenerator quotesGenerator;

    @BeforeEach
    public void clearStorage() {
        ohlcProcessingService.getInstrumentsDataStorage().clear();
    }

    @Test
    public void isOhlcCreated() {
        Ohlc ohlc = new Ohlc(OhlcPeriod.M1);
        ohlc.setOpenPrice(42.0);
        assertNotNull(ohlc);
    }

    @Test
    public void isOhlcStorageCreated() {
        OhlcStorage ohlcStorage = new OhlcStorage();
        assertNotNull(ohlcStorage);
        assertNotNull(ohlcStorage.getMinuteOhlc());
        assertNotNull(ohlcStorage.getHourOhlc());
        assertNotNull(ohlcStorage.getDailyOhlc());
    }

    @Test
    public void isOhlcStorageContainsCorrectOhlcObjects() {
        OhlcStorage ohlcStorage = new OhlcStorage();
        assertSame(ohlcStorage.getMinuteOhlc().getOhlcPeriod(), OhlcPeriod.M1);
        assertSame(ohlcStorage.getHourOhlc().getOhlcPeriod(), OhlcPeriod.H1);
        assertSame(ohlcStorage.getDailyOhlc().getOhlcPeriod(), OhlcPeriod.D1);
    }

    @Test
    public void isOhlcServiceCreated() {
        assertNotNull(ohlcProcessingService);
    }

    @Test
    public void isStorageMapInOhlcServiceCreated() {
        assertNotNull(ohlcProcessingService.getInstrumentsDataStorage());
    }

    @Test
    public void shouldAddNewQuoteToMap() {
        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        quote.setInstrumentId(instrumentId);
        quote.setPrice(42);
        quote.setUtcTimestamp(System.currentTimeMillis());
        ohlcProcessingService.onQuote(quote);

        assertTrue(ohlcProcessingService.getInstrumentsDataStorage().containsKey(instrumentId));
    }

    @Test
    public void shouldStoreInfoAboutSingleQuote() {
        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        double price = 42;

        quote.setInstrumentId(instrumentId);
        quote.setPrice(price);
        quote.setUtcTimestamp(System.currentTimeMillis());
        ohlcProcessingService.onQuote(quote);

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getMinuteOhlc().getOpenPrice(), price);
        assertEquals(storage.getMinuteOhlc().getClosePrice(), price);
        assertEquals(storage.getMinuteOhlc().getLowPrice(), price);
        assertEquals(storage.getMinuteOhlc().getHighPrice(), price);
    }

    @Test
    public void shouldStoreInfoAboutTwoQuotes() {
        long instrumentId = 1L;
        double price1 = 42;
        double price2 = 58;
        TestQuoteObject quote1 = new TestQuoteObject(price1, instrumentId, Instant.now().getEpochSecond());
        TestQuoteObject quote2 = new TestQuoteObject(price2, instrumentId, Instant.now().getEpochSecond());

        ohlcProcessingService.onQuote(quote1);
        ohlcProcessingService.onQuote(quote2);

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getMinuteOhlc().getOpenPrice(), price1);
        assertEquals(storage.getMinuteOhlc().getClosePrice(), price2);
        assertEquals(storage.getMinuteOhlc().getLowPrice(), price1);
        assertEquals(storage.getMinuteOhlc().getHighPrice(), price2);
    }

    @Test
    public void shouldStoreInfoAboutMultipleQuotes() {
        int count = 100;
        long instrumentId = 1L;
        List<Quote> testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);

        double openPrice = testQuoteList.get(0).getPrice();
        double closePrice = testQuoteList.get(count - 1).getPrice();

        double minPrice = openPrice;
        double maxPrice = openPrice;

        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getMinuteOhlc().getOpenPrice(), openPrice);
        assertEquals(storage.getMinuteOhlc().getClosePrice(), closePrice);
        assertEquals(storage.getMinuteOhlc().getLowPrice(), minPrice);
        assertEquals(storage.getMinuteOhlc().getHighPrice(), maxPrice);
    }

    @Test
    public void shouldStoreOhlcInfoForMultipleInstruments() {
        int count = 500;
        long[] instrumentIds = {1, 2, 3, 4};
        List<Quote> testQuoteList = quotesGenerator.createMultipleInstrumentQuotes(count, instrumentIds);

        for (Quote quote : testQuoteList) {
            ohlcProcessingService.onQuote(quote);
        }

        for (long instrumentId : instrumentIds) {
            List<Quote> filteredQuotes = testQuoteList.stream()
                    .filter(x -> x.getInstrumentId() == instrumentId).collect(Collectors.toList());
            Ohlc ohlc = new Ohlc(OhlcPeriod.M1);
            if (filteredQuotes.size() > 0) {
                Quote initQuote = filteredQuotes.get(0);
                ohlc.setHighPrice(initQuote.getPrice());
                ohlc.setLowPrice(initQuote.getPrice());
                ohlc.setOpenPrice(initQuote.getPrice());
                ohlc.setClosePrice(initQuote.getPrice());
            } else {
                if (!ohlcProcessingService.getInstrumentsDataStorage().containsKey(instrumentId))
                    continue;
                else
                    assert false;
            }
            for (Quote quote : filteredQuotes) {
                double price = quote.getPrice();
                ohlc.setClosePrice(price);
                if (price < ohlc.getLowPrice())
                    ohlc.setLowPrice(price);
                if (price > ohlc.getHighPrice())
                    ohlc.setHighPrice(price);
            }

            OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
            assertEquals(storage.getMinuteOhlc().getLowPrice(), ohlc.getLowPrice());
            assertEquals(storage.getMinuteOhlc().getHighPrice(), ohlc.getHighPrice());
            assertEquals(storage.getMinuteOhlc().getOpenPrice(), ohlc.getOpenPrice());
            assertEquals(storage.getMinuteOhlc().getClosePrice(), ohlc.getClosePrice());
        }
    }

    @Test
    public void shouldInitHourOhlcWithMinuteOhlc() {
        int count = 10;
        long instrumentId = 1L;
        List<Quote> testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);

        double openPrice = testQuoteList.get(0).getPrice();
        double closePrice = testQuoteList.get(count - 1).getPrice();

        double minPrice = openPrice;
        double maxPrice = openPrice;

        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }
        ohlcProcessingService.processMinuteOhlcAfterPeriod();
        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getHourOhlc().getOpenPrice(), openPrice);
        assertEquals(storage.getHourOhlc().getClosePrice(), closePrice);
        assertEquals(storage.getHourOhlc().getLowPrice(), minPrice);
        assertEquals(storage.getHourOhlc().getHighPrice(), maxPrice);
    }

    @Test
    public void shouldUpdateTwiceHourOhlcWithMinuteOhlc() {
        int count = 10;
        long instrumentId = 1L;

        // First batch
        List<Quote> testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);
        double openPrice = testQuoteList.get(0).getPrice();
        double minPrice = openPrice;
        double maxPrice = openPrice;

        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }
        ohlcProcessingService.processMinuteOhlcAfterPeriod();

        // Second batch
        testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);
        double closePrice = testQuoteList.get(count - 1).getPrice();
        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }
        ohlcProcessingService.processMinuteOhlcAfterPeriod();

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getHourOhlc().getOpenPrice(), openPrice);
        assertEquals(storage.getHourOhlc().getClosePrice(), closePrice);
        assertEquals(storage.getHourOhlc().getLowPrice(), minPrice);
        assertEquals(storage.getHourOhlc().getHighPrice(), maxPrice);
    }

    @Test
    public void shouldInitDailyOhlc() {
        int count = 10;
        long instrumentId = 1L;
        List<Quote> testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);

        double openPrice = testQuoteList.get(0).getPrice();
        double closePrice = testQuoteList.get(count - 1).getPrice();

        double minPrice = openPrice;
        double maxPrice = openPrice;

        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }
        ohlcProcessingService.processMinuteOhlcAfterPeriod();
        ohlcProcessingService.processHourOhlcAfterPeriod();

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getDailyOhlc().getOpenPrice(), openPrice);
        assertEquals(storage.getDailyOhlc().getClosePrice(), closePrice);
        assertEquals(storage.getDailyOhlc().getLowPrice(), minPrice);
        assertEquals(storage.getDailyOhlc().getHighPrice(), maxPrice);
    }

    @Test
    public void shouldUpdateDailyOhlc() {
        int count = 10;
        long instrumentId = 1L;

        // First batch
        List<Quote> testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);
        double openPrice = testQuoteList.get(0).getPrice();
        double minPrice = openPrice;
        double maxPrice = openPrice;

        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }
        ohlcProcessingService.processMinuteOhlcAfterPeriod();
        ohlcProcessingService.processHourOhlcAfterPeriod();

        // Second batch
        testQuoteList = quotesGenerator.createSingleInstrumentQuotes(count, instrumentId);
        double closePrice = testQuoteList.get(count - 1).getPrice();
        for (Quote quote : testQuoteList) {
            double currentPrice = quote.getPrice();
            ohlcProcessingService.onQuote(quote);
            if (currentPrice < minPrice) {
                minPrice = currentPrice;
            }
            if (currentPrice > maxPrice)
                maxPrice = currentPrice;
        }
        ohlcProcessingService.processMinuteOhlcAfterPeriod();
        ohlcProcessingService.processHourOhlcAfterPeriod();

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        assertEquals(storage.getDailyOhlc().getOpenPrice(), openPrice);
        assertEquals(storage.getDailyOhlc().getClosePrice(), closePrice);
        assertEquals(storage.getDailyOhlc().getLowPrice(), minPrice);
        assertEquals(storage.getDailyOhlc().getHighPrice(), maxPrice);
    }

    @Test
    public void shouldClearOhlcAfterScheduledProcessing() {
        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        double price = 42;

        quote.setInstrumentId(instrumentId);
        quote.setPrice(price);
        quote.setUtcTimestamp(System.currentTimeMillis());
        ohlcProcessingService.onQuote(quote);

        ohlcProcessingService.processMinuteOhlcAfterPeriod();
        ohlcProcessingService.processHourOhlcAfterPeriod();
        ohlcProcessingService.processDailyOhlcAfterPeriod();

        OhlcStorage storage = ohlcProcessingService.getInstrumentsDataStorage().get(instrumentId);
        Ohlc minuteOhlc = storage.getMinuteOhlc();
        Ohlc hourOhlc = storage.getHourOhlc();
        Ohlc dailyOhlc = storage.getHourOhlc();
        assertFalse(minuteOhlc.isOhlcWithPrice());
        assertFalse(hourOhlc.isOhlcWithPrice());
        assertFalse(dailyOhlc.isOhlcWithPrice());
    }

    @Test
    public void shouldReturnLastOhlc() {
        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        double price = 42;

        quote.setInstrumentId(instrumentId);
        quote.setPrice(price);
        quote.setUtcTimestamp(System.currentTimeMillis());
        ohlcProcessingService.onQuote(quote);

        Ohlc ohlc = ohlcProcessingService.getCurrent(1L, OhlcPeriod.M1);

        assertNotNull(ohlc);
        assertEquals(ohlc.getLowPrice(), price);
    }

    @Test
    public void shouldReturnNullIfOhlcNorFound() {
        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        double price = 42;

        quote.setInstrumentId(instrumentId);
        quote.setPrice(price);
        quote.setUtcTimestamp(System.currentTimeMillis());
        ohlcProcessingService.onQuote(quote);

        Ohlc ohlc = ohlcProcessingService.getCurrent(10000L, OhlcPeriod.M1);

        assertNull(ohlc);
    }

    @Test
    public void shouldProcessGetHistorical() {
        long instrumentId = 1L;
        ohlcProcessingService.getHistorical(instrumentId, OhlcPeriod.M1);
    }

    @Test
    public void shouldProcessGetHistoricalAndCurrent() {
        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        double price = 42;

        quote.setInstrumentId(instrumentId);
        quote.setPrice(price);
        quote.setUtcTimestamp(System.currentTimeMillis());
        ohlcProcessingService.onQuote(quote);

        List<Ohlc> ohlc = ohlcProcessingService.getHistoricalAndCurrent(1L, OhlcPeriod.M1);
    }

    @Test
    public void shouldHaveCorrectOhlcTimestamp() {
        Calendar minute = Calendar.getInstance();
        minute.set(Calendar.MILLISECOND, 0);
        minute.set(Calendar.SECOND, 0);

        Calendar hour = Calendar.getInstance();
        hour.set(Calendar.MILLISECOND, 0);
        hour.set(Calendar.SECOND, 0);
        hour.set(Calendar.MINUTE, 0);

        Calendar day = Calendar.getInstance();
        day.set(Calendar.MILLISECOND, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.HOUR_OF_DAY, 0);

        TestQuoteObject quote = new TestQuoteObject();
        long instrumentId = 1L;
        double price = 42;

        quote.setInstrumentId(instrumentId);
        quote.setPrice(price);
        quote.setUtcTimestamp(minute.getTimeInMillis());
        ohlcProcessingService.onQuote(quote);

        Ohlc minuteOhlc = ohlcProcessingService.getCurrent(1L, OhlcPeriod.M1);
        Ohlc hourOhlc = ohlcProcessingService.getCurrent(1L, OhlcPeriod.H1);
        Ohlc dailyOhlc = ohlcProcessingService.getCurrent(1L, OhlcPeriod.D1);

        assertNotNull(minuteOhlc);
        assertNotNull(hourOhlc);
        assertNotNull(dailyOhlc);

        assertEquals(minuteOhlc.getPeriodStartUtcTimestamp(), minute.getTimeInMillis());
        assertEquals(hourOhlc.getPeriodStartUtcTimestamp(), hour.getTimeInMillis());
        assertEquals(dailyOhlc.getPeriodStartUtcTimestamp(), day.getTimeInMillis());
    }

}
