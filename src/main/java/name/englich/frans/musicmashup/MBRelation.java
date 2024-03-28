package name.englich.frans.musicmashup;

/**
 * Represents MusicBrainz's JSON.
 *
 * @param type The kind, for instance "wikidata"
 * @param url The URL to this service.
 */
public record MBRelation(String type,
                         MBURL url) {
}
