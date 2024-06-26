package name.englich.frans.musicmashup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Represents an album by the requested artist, with the data
 * needed for our return. MBAlbum represents the data returned from MusicBrainz.
 * <br/>
 * This is not a record because we're doing threading and need to modify a member variable.
 */
public class ResultAlbum {

    final private String m_title;

    /**
     * The MBID of this album (release-group), not the artist.
     */
    String m_mbid;

    /**
     * The URL to the front cover image. If it's empty, it's our way of signal that
     * no URL could be found.
     */
    URL image;

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
    public URL getImage() {
        return image;
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
    }

    /**
     * Converts the list of MusicBrainz's artists to the format we use
     * in our JSON return.
     *
     * @param albums The MusicBrainz's artists.
     * @return The result we return from our web service
     */
    static ArrayList<ResultAlbum>  from(List<MBAlbum> albums) {
        // We set the initial size to improve performance.
        ArrayList<ResultAlbum> retval = new ArrayList<>(albums.size());

        List<CompletableFuture<Void>> futures;
        futures = new ArrayList<>(albums.size());

        for (MBAlbum album : albums) {
            ResultAlbum resAlbum = new ResultAlbum(album.title(), album.id());

            // We use runAsync(): it starts instantly and fetchImageURL() writes to
            // its own object.
            CompletableFuture<Void> job = CompletableFuture.runAsync(() -> {
                resAlbum.fetchImageURL();
            }, ResultController.caaExecutor);

            futures.add(job);
            retval.add(resAlbum);
        }

        // The futures are running, and we wait on each. When we've waited to the end
        // of the list, we know all have completed.
        futures.forEach(CompletableFuture::join);

        return retval;
    }

    /**
     * Blocking function that retrieves the URL from Cover Art Archive and assigns it to our member variable m_imageURL.
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

        try {
            JsonNode rootNode = objectMapper.readValue(caaURL, JsonNode.class);
            Iterator<JsonNode> images = rootNode.get("images").elements();

            while (images.hasNext()) {
                JsonNode image = images.next();

                // We only want the front cover.
                if (image.get("front").asBoolean()) {
                    this.image = new URL(image.get("image").textValue());
                    break;
                }
            }
        }
        catch (java.io.FileNotFoundException e) {
            // No JSON for this album, we leave m_imageURL empty.
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // We now have written to imageURL or not. If it's
       //  empty, no front cover was found.
    }
}
