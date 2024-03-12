package com.example.musicmashup;

import java.util.List;

public record OurQueryResult(String mbid, List<OurResultAlbum> albums) { }