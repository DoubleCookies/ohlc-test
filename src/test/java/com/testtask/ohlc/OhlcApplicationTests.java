package com.testtask.ohlc;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.model.OhlcStorage;
import com.testtask.ohlc.model.TestQuoteObject;
import com.testtask.ohlc.services.OhlcProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OhlcApplicationTests {

	@Autowired
	private OhlcProcessingService ohlcProcessingService;

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

}
