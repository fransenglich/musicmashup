package name.englich.frans.musicmashup;

/**
 * A simple placeholder for the URL entry in MusicBrainz' JSON.
 *
 * @param id the MBID for this URL
 * @param resource the actual URL
 */
public record MBURL(String id, String resource) {
}
