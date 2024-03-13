package com.example.musicmashup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

@RestController
public class ResultController {
    /**
     * The main entry point for Our REST interface. The returned
     * structure is automatically marshalled by the Jackson framework.
     *
     * @param mbid The MusicBrainz identifier (MBID) for the artist.
     * @return A structure containing the artist information.
     */
    @GetMapping("/musicmashup")
    public OurQueryResult musicmashup(@RequestParam(value = "mbid") String mbid) {
        String mbURL = "https://musicbrainz.org/ws/2/artist/"
                + mbid
                + "?&fmt=json&inc=url-rels+release-groups";

        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();

        MBQueryReturn mbReturn = restTemplate.getForObject(mbURL, MBQueryReturn.class);
        // TODO error handling.
        ArrayList<OurResultAlbum> albums = OurResultAlbum.from(mbReturn.albums);

        String wpLink = "";
        try {
           wpLink = getLinkFromWikidata(mbReturn);
        } catch (Exception e) {
        }

        String description = extractFromWikipedia(wpLink);

        return new OurQueryResult(mbid, description, albums);
    }

    /**
     * Constructs the link to the Wikipedia API by fetching from Wikidata.
     *
     * @param mbReturn The data from MusicBrainz
     * @return The URL. For instance https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=Nirvana_(band)
     */
    String getLinkFromWikidata(MBQueryReturn mbReturn) throws IOException {
        final String wikiID = mbReturn.getWikiID();

        URL wdURL = null;

        try {
            wdURL = new URL("https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=sitelinks&ids="
                            + wikiID);
        } catch (Exception e) {
        }

        ObjectMapper objectMapper = new ObjectMapper();

        /*
        TODO document why we use JsonNode instead.
         */
        JsonNode rootNode = objectMapper.readValue(wdURL, JsonNode.class);

        // TODO Error handling
        JsonNode entities = rootNode.get("entities");
        JsonNode entry = entities.get(wikiID);
        JsonNode sitelinks = entry.get("sitelinks");
        JsonNode enwiki = sitelinks.get("enwiki");

        final String artistLinkTitle = enwiki.get("title").textValue();
        // TODO: Edge case: Sometimes MusicBrainz will refer to Wikipedia directly.

        // TODO URL encode
        return "https://en.wikipedia.org/w/api.php?action=query" +
               "&format=json&prop=extracts&exintro=true&redirects=true&titles=" +
               artistLinkTitle;
    }

    /**
     * Queries the Wikipedia API, parses the returned JSON, and returns the description.
     *
     * @param url The URL to the Wikipedia API to query
     * @return The description of the artist in HTML
     */
    String extractFromWikipedia(String url) {
        return "";
    }
}