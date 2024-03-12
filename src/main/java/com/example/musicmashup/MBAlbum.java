package com.example.musicmashup;

/**
 * The album return from Music Brainz (MB). This is not what we return, however.
 *
 * @param id the MBID of the album
 * @param title the title of the album, for instance "Bleach"
 */
public record MBAlbum(String id, String title) {
}
