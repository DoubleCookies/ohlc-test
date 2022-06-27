package com.testtask.ohlc;

import com.testtask.ohlc.enums.OhlcPeriod;
import com.testtask.ohlc.model.Ohlc;
import com.testtask.ohlc.model.OhlcStorage;
import com.testtask.ohlc.services.OhlcProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
