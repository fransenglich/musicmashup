package com.example.musicmashup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
//public record MBReleaseGroups(MBAlbum[] albums) { }

public class MBReleaseGroups {

    public List<MBAlbum> albums;

    MBReleaseGroups(@JsonProperty("release-groups") List<MBAlbum> release_groups) {
        albums = release_groups;
    }
}