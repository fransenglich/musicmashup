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
        ArrayList<OurResultAlbum> retval = new ArrayList<OurResultAlbum>();

        for (MBAlbum album : albums) {
            retval.add(new OurResultAlbum(album.title(),
                                          album.id(),
                                   "TODO construct image URL"));
        }

        return retval;
    }
}
