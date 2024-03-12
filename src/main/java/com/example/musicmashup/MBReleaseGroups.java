package com.example.musicmashup;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MBReleaseGroups {

    public List<MBAlbum> albums;

    MBReleaseGroups(@JsonProperty("release-groups") List<MBAlbum> release_groups) {
        albums = release_groups;
    }
}