package com.example.musicmashup;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album by the requested artist.
 *
 * @param title The album title, such as "Bleach", without quotes
 * @param id The MBID of the album, not the artist
 * @param image the URL to the front cover of the album
 */
public record OurResultAlbum (String title, String id, String image) {

    /**
     * Converts from MusicBrainz return to what we need to return.
     *
     * @param albums The MusicBrainz data
     * @return Representation of what we need to return
     */
    public static List<OurResultAlbum>  from(List<MBAlbum> albums) {
        // We set the initial size to improve performance.
        ArrayList<OurResultAlbum> retval = new ArrayList<OurResultAlbum>(albums.size());

        for (MBAlbum album : albums) {
            final String imageUrl = "https://coverartarchive.org/release/"
                                  + album.id()
                                  + "/front.jpg";

            retval.add(new OurResultAlbum(album.title(),
                                          album.id(),
                                          imageUrl));
        }

        return retval;
    }
}
