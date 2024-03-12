package com.example.musicmashup;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        MBQueryReturn retval = restTemplate.getForObject(mbURL, MBQueryReturn.class);
        // TODO error handling.
        ArrayList<OurResultAlbum> albums = OurResultAlbum.from(retval.albums);

        String description = getWikiDescription(retval);

        return new OurQueryResult(mbid, description, albums);
    }

    /**
     * Does the second part, fetches from Wikipedia. First stop is Wikidata.
     *
     * @param mbReturn The data from MusicBrainz
     * @return The HTML, potentially tag-soup, of Wikipedia's description of the artist.
     */
    String getWikiDescription(MBQueryReturn mbReturn) {
        final String wikiID = mbReturn.getWikiID();

        final String wdURL = "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=sitelinks?ids="
                            + wikiID;

        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();

        WDQueryReturn retval = restTemplate.getForObject(wdURL, WDQueryReturn.class);

        return "";
    }
}