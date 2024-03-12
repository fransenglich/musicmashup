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
        String mbQuery = "https://musicbrainz.org/ws/2/artist/"
                       + mbid
                       + "?&fmt=json&inc=url-rels+release-groups";

        RestTemplateBuilder builder = new RestTemplateBuilder();
        RestTemplate restTemplate = builder.build();

        MBReleaseGroups retval = restTemplate.getForObject(mbQuery, MBReleaseGroups.class);
        ArrayList<OurResultAlbum> albums = OurResultAlbum.from(retval.albums);

        return new OurQueryResult(mbid, albums);
    }
}