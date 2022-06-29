package com.testtask.ohlc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;

// There is no actual base, disable auto config
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, XADataSourceAutoConfiguration.class})
public class OhlcApplication {

	public static void main(String[] args) {
		SpringApplication.run(OhlcApplication.class, args);
	}

}
