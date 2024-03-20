package com.example.musicmashup;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

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
	 *
	 * We baseline the current return from MusicBrainz, meaning that if any
	 * new album is released or a current is changed, this test will break. A softer/"fussy" approach
	 * could be done, such as checking only one album or so, but this is more rigid.
	 *
	 * We cannot assume the order of albums is stable, and we don't add that property to our API, so
	 * the testing is made more complex.
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

		// Now, we compare the albums.

		// Load our baseline.
		String resourceName = "expNirvanaAlbums.json";
		ClassLoader classLoader = getClass().getClassLoader();
		File jsonExpFile = new File(classLoader.getResource(resourceName).getFile());
		assertThat(jsonExpFile.exists());

		// Convert the JSON file to Java instances
		ObjectMapper objectMapper = new ObjectMapper();
		TestExpJson topRoot = objectMapper.readValue(jsonExpFile, TestExpJson.class);

		List<TestJsonAlbum> expAlbums = Arrays.asList(topRoot.albums());
		List<TestJsonAlbum> actAlbums = Arrays.asList(json.albums());

		Comparator<TestJsonAlbum> comp = new Comparator<TestJsonAlbum>() {
			@Override
			public int compare (TestJsonAlbum a, TestJsonAlbum b) {
				return a.mbid().compareTo(b.mbid());
			}
		};

		// We don't know if the order of albums from MusicBrainz is stable.
		Collections.sort(expAlbums, comp);
		Collections.sort(actAlbums, comp);

		assertEquals(expAlbums.size(), actAlbums.size());

		// This is simpler than turning the Java record into a class
		for(int i = 0; i < expAlbums.size(); ++i) {
			TestJsonAlbum a = expAlbums.get(i);
			TestJsonAlbum b = actAlbums.get(i);

			assertEquals(a.mbid(), b.mbid());
			assertEquals(a.title(), b.title());
			assertEquals(a.image(), b.image());
		}
	}
}
