package com.example.musicmashup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)
class MusicMashupApplicationTests {
	@Autowired
	private TestRestTemplate template;

	@Test
	void shouldGiveErrorMessage() throws Exception {
		assertEquals("{\"mbid\":\"NOT_EXIST\",\"description\":\"MusicBrainz doesn't have entry for MBID NOT_EXIST\",\"albums\":[]}",
				this.template.getForEntity("/musicmashup?mbid=NOT_EXIST", String.class).getBody());
	}

	/**
	 * Check that invalid interface is handled.
	 */
	@Test
	void invalidRequest() throws Exception {
		ResponseEntity<String> response = template.getForEntity("/musicmashup?WRONG_PARAM", String.class);
		Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * A typical query, for Nirvana.
	 */
	@Test
	public void fetchNirvana() throws Exception {
		ResponseEntity<TestJsonReturn> response = template.getForEntity("/musicmashup?mbid=5b11f4ce-a62d-471e-81fc-a69a8278c7da", TestJsonReturn.class);
		TestJsonReturn json = response.getBody();

		// Check the mbid
		assertEquals("5b11f4ce-a62d-471e-81fc-a69a8278c7da", json.mbid());

		// Check the description
		// Wikipedia might change, so we do some simple checks. It would be
		// possible to pull in some library that can tell "this is likely HTML".
		assertThat(!json.description().isEmpty());
		assertThat(json.description().contains("Nirvana"));
		assertThat(json.description().contains("<p>"));


	}
}
