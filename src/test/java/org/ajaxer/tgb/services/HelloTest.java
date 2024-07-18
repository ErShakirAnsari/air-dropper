package org.ajaxer.tgb.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Shakir Ansari
 * @since 2024-07-18
 */
@Slf4j
public class HelloTest
{
	@Test
	public void hello()
	{
		Instant s = Instant.ofEpochMilli(System.currentTimeMillis());
		Instant e = Instant.ofEpochMilli(System.currentTimeMillis() + (22 * 60 * 60 * 1000L));
		log.info("start: {}", s);
		log.info("end: {}", e);

		Duration duration = Duration.between(s, e);
		long hours = duration.toHours();
		long minutes = duration.toMinutes() % 60;

		System.out.println("Time difference: " + hours + " hours " + minutes + " minutes");
	}
}
