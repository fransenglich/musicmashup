package com.example.musicmashup;

public record TestJsonReturn (String mbid,
                              String description,
                              TestJsonReturnAlbum[] albums) {}
