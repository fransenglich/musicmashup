package com.example.musicmashup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MBReleaseGroups(OurResultAlbum[] albums) { }