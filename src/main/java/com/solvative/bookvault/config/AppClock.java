package com.solvative.bookvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AppClock {

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}
}

