package com.example.musicmashup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an album by the requested artist.
 */
public class ResultAlbum /*(String title, String mbid, String imageURL)*/ {

    private String m_title;

    /**
     * The MBID of this album (release-group), not the artist.
     */
    String m_mbid;

    /**
     * The URL to the front cover image. If it's empty, it's our way of signal that
     * no URL could be found.
     */
    URL m_imageURL;

    /**
     * Getter necessary for Jackson's marshalling.
     */
    public String getMbid() {
        return m_mbid;
    }

    /**
     * Getter necessary for Jackson's marshalling.
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * Getter necessary for Jackson's marshalling.
     */
    public URL getImageURL () {
        return m_imageURL;
    }

    /**
     * Standard constructor.
     *
     * @param title The album title, such as "Bleach", without quotes
     * @param mbid The MBID of the album, not the artist
     */
    ResultAlbum(String title, String mbid) {
        this.m_title = title;
        this.m_mbid = mbid;
        //this.image = image;
    }
    /**
     * Converts from MusicBrainz return to what we need to return.
     *
     * @param albums The MusicBrainz data
     * @return Representation of what we need to return
     */
    /*
    static ArrayList<ResultAlbum>  from(List<MBAlbum> albums) {
        // We set the initial size to improve performance.
        ArrayList<ResultAlbum> retval = new ArrayList<>(albums.size());

        List<CompletableFuture<Void>> completableFutures;
        for (MBAlbum album : albums) {
            final String imageUrl = "https://coverartarchive.org/release-group/"
                                  + album.id()
                                  + "/front.jpg";

            ResultAlbum alb = new ResultAlbum(album.title(), album.id(), imageUrl);
            retval.add(alb);

           // CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
              //  alb.getImageURL();
            });
        }


        return retval;
    }
*/

    static ArrayList<ResultAlbum>  from(List<MBAlbum> albums) {
        // We set the initial size to improve performance.
        ArrayList<ResultAlbum> retval = new ArrayList<>(albums.size());

        List<CompletableFuture<Void>> futures;
        futures = new ArrayList<>(albums.size());

        for (MBAlbum album : albums) {
            ResultAlbum resAlbum = new ResultAlbum(album.title(), album.id());
            CompletableFuture<Void> job = CompletableFuture.supplyAsync(() -> {
                resAlbum.fetchImageURL();
                return null;
            });

            futures.add(job);
            retval.add(resAlbum);
        }

        // We block and wait for all our tasks (image URL per album) to finish.
        futures.forEach(CompletableFuture::join);

        return retval;
    }

    /**
     * Blocking function that retrieves the URL from Cover Art Archive and assigns it to our member variable.
     */

    public void fetchImageURL() {
       final ObjectMapper objectMapper = new ObjectMapper();

        // For instance: https://coverartarchive.org/release-group/76df3287-6cda-33eb-8e9a-044b5e15ffdd
        URL caaURL;

        try {
            caaURL = new URL("https://coverartarchive.org/release-group/" + m_mbid);
        }
        catch (Exception ignored) {
            return;
        }

        System.out.println("FOO BAR");

      //  URL imageURL = null;

        try {
            JsonNode rootNode = objectMapper.readValue(caaURL, JsonNode.class);
            Iterator<JsonNode> images = rootNode.get("images").elements();

            while (images.hasNext()) {
                JsonNode image = images.next();

                // We only want the front cover.
                if (image.get("front").asBoolean()) {
                    m_imageURL = new URL(image.get("image").textValue());
                    break;
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }

        // We now have written to imageURL or not. If it's
       //  empty, no front cover was found.

     //   final CompletableFuture<URL> completableFuture = new CompletableFuture<>(imageURL);
        //return completableFuture;
    }


}
