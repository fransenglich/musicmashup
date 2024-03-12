package com.example.musicmashup;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Mirrors the JSON from MusicBrainz (MB).
 * <br>
 * To study, GET the JSON and look at the output. All these small
 * records mirror the return, so no ingenious crafting of them.
 * <br>
 * This is a class and not a record because we need a custom JsonProperty.
 */
public class MBQueryReturn {

    public List<MBAlbum> albums;

    public List<MBRelation> relations;

    /**
     * Returns the wiki ID for this artist.
     * <br>
     *  The URL looks like this:
     *  https://www.wikidata.org/wiki/Q11649
     * <br>  
     * We extract and return: Q11649
     */
    String getWikiID() {
        for (MBRelation relation  : relations) {
           if (relation.type().equals("wikidata")) {
               final String res = relation.url().resource();
               final int last = res.lastIndexOf("/");

               final String substring = res.substring(last + 1, res.length());
               return substring;
           }

        }

        return ""; // TODO error protocol
    }
    
    MBQueryReturn(@JsonProperty("release-groups") List<MBAlbum> release_groups,
                  List<MBRelation> relations) {
        albums = release_groups;
        this.relations = relations;
    }
}