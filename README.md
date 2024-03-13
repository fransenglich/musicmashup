
* See my documentation on the Swift case for reasoning about software:
LINK

This README contains aspects or issues not mentioned in the above link.

# Known Topics

* Wikipedia's API may return not well-formed or invalid HTML, which is conditions (bugs, in fact) that propagate into our own API.

* What catches my attention is this in the case description:

> The API should respond with all the data as quickly as possible. This can be challenging because some of the external APIs can be slow.

My initial thought was that concurrent fetches would be needed, but that's not possible due to that all fetches are sequential. TODO

* Relatively large images are served by the API. Cover Art Archive has smaller, but that's not necessarily of interest. Here's missing in the spec. If it's supposed to be thumbnails in some UI, smaller images are useful. However, if it's to be viewed as a display on a dashboard, larger images are better, as are served.

* I find the code for marshalling from JSON cumbersome. However, I don't know a more convenient way.

Input: MBID
Output: JSON that has:
    * "mbid" - copied from input
    * "description" - from Wikipedia
    * "album" that has:
        - title     - from MusicBrainz
        - id (MBID) (of album) - from MusicBrainz
        - image     - This URL can be constructed using the MBID

FIXME The problem of Cover Art Archive being slow, is solved by doing many requests at the same time.

# How to Build & Run

This service is a Spring Boot and Gradle application, built on Java 17. Hence, you can run it by:

* Running the JAR file:

    java -jar build/libs/musicmashup-0.0.1-SNAPSHOT.jar

* Running it via Gradle:

    ./gradlew bootRun

After you've started the service, you can access it on your local host, such as for The Beatles:

<http://localhost:8080/musicmashup?mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d>

# 
# WIP
1. DONE Query MusicBrainz with the MBID to get list of albums:

        https://musicbrainz.org/ws/2/artist/5b11f4ce-a62d-471e-81fc-a69a8278c7da?&fmt=json&inc=url-rels+release-groups

2. Get link to Wikidata via MusicBrainz 'type="wikidata"->url'

3. Add fragments so it looks like this:

        https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q11649&format=json&props=sitelinks

4. Query Wikidata to get "enwiki" and the "title"

5. Construct URL to Wikipedia. For instance:

        https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&redirects=true&titles=Nirvana_(band)

6. Query Wikipedia
7. Copy the result into your own return
8. Construct also album links & data into your own return.

TODO
The Beatles: b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d
https://spring.io/guides/gs/securing-web
* "You should be able to start the API directly from Maven/Gradle (for example by using Jetty and mvn jetty:run)"