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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
public class ResultController {
    /**
     * The main entry point for Our REST interface. The returned
     * structure is automatically marshalled by the Jackson framework.
     * TODO doc JavaNode/Jackson
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

        URL wpURL = null;
        try {
           wpURL = getLinkFromWikidata(mbReturn);
        } catch (Exception e) {
        }

        System.out.println("wpURL:" + wpURL);
        String description = "";

        try {
            description = extractFromWikipedia(wpURL);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Description:" + description);

        return new OurQueryResult(mbid, description, albums);
    }

    /**
     * Constructs the link to the Wikipedia API by fetching from Wikidata.
     *
     * @param mbReturn The data from MusicBrainz
     * @return The URL. For instance https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=Nirvana_(band)
     */
    private URL getLinkFromWikidata(MBQueryReturn mbReturn) throws IOException {
        final String wikiID = mbReturn.getWikiID();

        URL wdURL = null;

        try {
            wdURL = new URL("https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=sitelinks&ids="
                            + wikiID);
        } catch (Exception e) {
            System.out.println(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        /*
        The node name in the returned JSON does in this case vary, it is the identifier. Handling
        this is easier with JsonNOde, than Jackson's other marshalling.
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
        return new URL("https://en.wikipedia.org/w/api.php?action=query" +
                       "&format=json&prop=extracts&exintro=true&redirects=true&titles=" +
                       URLEncoder.encode(artistLinkTitle, StandardCharsets.UTF_8));
    }

    /**
     * Queries the Wikipedia API, parses the returned JSON, and returns the description.
     *
     * @param wpURL The URL to the Wikipedia API to query
     * @return The description of the artist in HTML
     */
    private String extractFromWikipedia(URL wpURL) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readValue(wpURL, JsonNode.class);

        // TODO Error handling
      //  JsonNode entities = rootNode.get("entities");
        JsonNode query = rootNode.get("query");
        JsonNode pages = query.get("pages");
        JsonNode actualPage = pages.elements().next();

        return actualPage.get("extract").textValue();
    }
}