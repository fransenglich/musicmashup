package com.example.musicmashup;

/**
 * Represents an album by the requested artist.
 *
 * @param title The album title, such as "Bleach", without quotes
 * @param id The MBID of the album, not the artist
 * @param image the URL to the front cover of the album
 */
public record OurResultAlbum (String title, String id, String image) {}
