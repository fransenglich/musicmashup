
* See my documentation on the Swift case for reasoning about software:
LINK

This README contains aspects or issues not mentioned in the above link.

# Known Issues

* Wikipedia's API may return not well-formed or invalid HTML, which is conditions (bugs, in fact) that propagate into our own API.

Input: MBID
Output: JSON that has:
    * "mbid" - copied from input
    * "description" - 
    * "album" that has:
        - title     - from MusicBrainz
        - id (MBID) (of album) - from MusicBrainz
        - image     - This URL can be constructed using the MBID

The problem of Cover Art Archive being slow, is solved by doing many requests at the same time.


WIP
1. Query MusicBrainz with the MBID to get list of albums.
2. Get link to Wikidata via MusicBrainz 'type="wikidata"->url'
3. Add fragments so it looks like this:
    https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q11649&format=json&props=sitelinks

4. Query Wikidata to get "enwiki" and the "title"
5. Construct URL to Wikipedia. For instance:
    https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=Nirvana_(band)
6. Query Wikipedia
7. Copy the result into your own return

