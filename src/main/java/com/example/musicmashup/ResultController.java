package com.example.musicmashup;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultController {
    @GetMapping("/greeting")
    public OurQueryResult greeting(@RequestParam(value = "mbid") String mbid) {
        String mbQuery = "https://musicbrainz.org/ws/2/artist/"
                       + mbid
                       + "?&fmt=json&inc=url-rels+release-groups";


        /*
        public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			Quote quote = restTemplate.getForObject(
					"http://localhost:8080/api/random", Quote.class);
			log.info(quote.toString());
		};
         */
        return new OurQueryResult("Title", "mbid");
    }
}