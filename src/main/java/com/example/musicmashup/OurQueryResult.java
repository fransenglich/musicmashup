package com.example.musicmashup;

import java.util.ArrayList;

/**
 * A Java record for our JSON result.
 * It is the MBID followed by the list of albums.
 *
 * @param mbid The MBID of the artist
 * @param albums A list of the artist's albums.
 */
public record OurQueryResult(String mbid, ArrayList<OurResultAlbum> albums) { }