package com.testtask.ohlc;

import com.testtask.ohlc.model.Ohlc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OhlcApplicationTests {

	@Test
	void isOhlcCreated() {
		Ohlc ohlc = new Ohlc();
		ohlc.setOpenPrice(42.0);
		assertNotNull(ohlc);
	}

}
