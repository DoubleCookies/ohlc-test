package com.testtask.ohlc;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.model.OhlcStorage;
import com.testtask.ohlc.model.TestQuoteObject;
import com.testtask.ohlc.services.OhlcProcessingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OhlcApplicationTests {

	@Autowired
	private OhlcProcessingService ohlcProcessingService;

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
	public void isOhlcStorageContainsCorrectOhlcs() {
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
		quote.setUtcTimestamp(Instant.now().getEpochSecond());
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
		quote.setUtcTimestamp(Instant.now().getEpochSecond());
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

}
