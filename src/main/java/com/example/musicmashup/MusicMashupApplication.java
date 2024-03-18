package com.example.musicmashup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class MusicMashupApplication {
	/**
	 * Pool for Cover Art Archive fetches.
	 */
	public static ExecutorService caaExecutor = null;

	public static void main(String[] args) {
		/**
		 * We use this pool for fetching from Cover Art Archive.
		 * We don't compute much, but we spend a lot of time waiting
		 * for HTTP, so a high count makes sense.
		 * Plenty of research can be spent on this constant, such as what the typically
		 * queried artist has as number of albums.
		 * .newCachedThreadPool() could be considered.
		 */
		caaExecutor = Executors.newFixedThreadPool(20);

		SpringApplication.run(MusicMashupApplication.class, args);
	}
}
