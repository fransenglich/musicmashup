package com.example.musicmashup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MusicMashupApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ResultController controller;

	@Autowired
	private TestRestTemplate restTemplate;


	/**
	 * A simple test to ensure our context loads.
	 */
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}


	@Test
	void myTest() {
		assertEquals("NO", "BAR");
	}
}
