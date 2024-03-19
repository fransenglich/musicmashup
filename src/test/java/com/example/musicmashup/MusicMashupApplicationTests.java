package com.example.musicmashup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)
class MusicMashupApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ResultController controller;

	@Autowired
	private TestRestTemplate restTemplate;

	/**
	 * Ensure our context loads.
	 */
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

	@Test
	void shouldGiveErrorMessage() throws Exception {
		assertEquals(this.restTemplate.getForObject("http://localhost:" + port + "/musicmashup?mbid=NOT_EXIST",
						String.class),
				"{\"mbid\":\"NOT_EXIST\",\"description\":\"MusicBrainz doesn't have entry for MBID NOT_EXIST\",\"albums\":[]}");
	}

	/**
	 * Check that invalid interface is handled.
	 */
	@Test
	void invalidRequest() throws Exception {
		TestRestTemplate testRestTemplate = new TestRestTemplate();
		ResponseEntity<String> response = testRestTemplate.
				getForEntity("http://localhost:" + port + "/musicmashup?WRONG_PARAM", String.class);

		Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * A success test.
	 */
	@Test
	void fetchNirvana() throws Exception {
		System.out.println("Port:" + port);
		TestJsonReturn rv = this.restTemplate.getForObject("http://localhost:"
						+ port
						+ "/musicmashup?mbid=5b11f4ce-a62d-471e-81fc-a69a8278c7da",
				TestJsonReturn.class);

		String str =  this.restTemplate.getForObject("http://localhost:"
						+ port
						+ "/musicmashup?mbid=5b11f4ce-a62d-471e-81fc-a69a8278c7da",
				String.class);

		assertEquals("5b11f4ce-a62d-471e-81fc-a69a8278c7da", str);

		// Wikipedia might change, so we do some simple checks. It would be
		// possible to pull in some library that can tell "this is likely HTML".
		assertThat(!rv.description().isEmpty());
		assertThat(rv.description().contains("Nirvana"));
		assertThat(rv.description().contains("<p>"));

	}
}
