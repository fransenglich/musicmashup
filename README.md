
# Java Music Mashup

See the README in my Swift case, which is the first case I wrote, about reasoning about software creation and so forth. Many of its comments applies to this case too.

This was my first time with these technologies, such as Spring Boot, Jackson, Gradle and Intellij. It was a delight.

This README has comments that are specific to this (Java) case.

# On concurrency

What catches my attention is this in the case description:

> The API should respond with all the data as quickly as possible. This can be challenging because some of the external APIs can be slow. Some of them even enforce rate limits.

I don't know if this is a "trick question", because the case can be solved without querying Cover Art Archive (CAA). That is, it's possible to construct links to the cover art images in CAA without querying for the JSON, and the first version I wrote did exactly that.

Also, CAA doesn't have rate limits, according to documentation. One GET request per query is done to MusicBrainz, Wikidata and Wikipedia. 

However, because of the case description above, it seems as if Cygni/Accenture wanted to write some form of parallelism, so I rewrote the code to asynchronously fetch the JSON from CAA and use it to construct the links in the response. However, this is of course massively slower (N GET requests for N albums, compared to 0 requests), but it does demonstrate a bit of asynchronous code. Hence, the current version is not the fastest, but is perhaps better at what the case is actually about: judging my coding skills.

It should be noticed that much can't be parallelized in the case. Our REST return partly depends on Wikipedia which in turn depends on Wikidata, which depends on MusicBrainz. It's not possible to parallelize these requests because of their innate dependencies.

The mashup service is not computationally expensive, because we're not using CPU cycles ourselves on anything heavy, and we mostly run in efficiently coded libraries, the marshalling done by Jackson and so on. What do consume *time* is waiting for the returns from the various services.

I chose to do it in a simple but yet performant manner, Java 8's CompletableFuture with a pool, essentially. However, there are many modern libraries out there for threading/async (such as EA Async), and I chose to keep it simple. Bringing in dependencies can be discussed from many angles, and for this case, I chose not to use any. 

As a C++ programmer on the large tool/API framework Qt, we've spent a lot of time on designing and writing thread-safe APIs and a luxury many modern solutions has: reentrant code. Getting better to know Java's approach will be interesting.

# Known Issues

* Wikipedia's API may return not well-formed or invalid HTML, which is conditions (a bug, in fact) that propagate into our own API. It needs be documented, it could be a sensible design choice to pass it through the API

* Error handling is currently generally not done.

* The edge case of Wikipedia being served directly, as opposed to via Wikidata, hasn't been detected.

* More attention to threading, to put it in context with the framework at large. Currently, we assume we're the only runner.

# Testing

I've been on W3C's XQuery Test Task Force, hold an ISTQB certificate in testing and have a heart and mindset for robustness and reliability.

The tests can be run through:

    gradle test

and subsequently view <file://./build/reports/tests/test/index.html>

The tests are not exhaustive, they demonstrate the principle. 

We also test directly against the web APIs. This could be replaced with some form of in-memory integration to off-load those.

# How to Build & Run

This service is a Spring Boot and Gradle application, built on Java 17. Hence, you can run it by:

* Running the JAR file (included in the zip):

    java -jar build/libs/musicmashup-0.0.1-SNAPSHOT.jar

* Running it via Gradle:

    ./gradlew bootRun

After you've started the service, you can access it on your local host on port 8080, such as for The Beatles:

<http://localhost:8080/musicmashup?mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d>

or Zara Larsson:

<http://localhost:8080/musicmashup?mbid=134e6410-6954-45d1-bd4a-0f2d2ad5471d>

# Topics

* Relatively large images are served by the API. CAA has smaller, but that's not necessarily of interest. Here's missing in the spec. If it's supposed to be thumbnails in some UI, smaller images are useful. However, if it's to be viewed as on display on a dashboard, larger images are better, and those are served.

* IntelliJ and Gradle give some warnings on my setup, but according to googling they should be harmless, although broken window theory applies.

* A rigid approach to quality assurance is needed. Deploy static and dynamic testing, test analysis and design, and apply Black-Box testing principles:

  - Equivalence partitioning
  - Boundary Value Analysis
  - Decision Table Testing
  - State Transition Testing

Concretely:
  - More testing of non-ASCII/Unicode
  - More testing of error handling.
