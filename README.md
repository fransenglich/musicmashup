
See the README in my Swift case, which is the first case I wrote, about reasoning about software creation and so forth.

This was my first time with these technologies, such as Spring Boot, Jackson, Gradle and Intellij. It was a delight.

This README has comments that are specific to this (Java) case.

# Java Music Mashup

# Known Issues

* Wikipedia's API may return not well-formed or invalid HTML, which is conditions (bugs, in fact) that propagate into our own API. It needs be documented, it could be a sensible design choice to pass it through the API

# On concurrency

What catches my attention is this in the case description:

> The API should respond with all of the data as quickly as possible. This can be challenging because some of the external APIs can be slow. Some of them even enforce rate limits.

I don't know if this is a "trick question", because the case can be solved without querying Cover Art Archive (CAA). On one hand the case is solved in an efficient manner, but parallelized/asynchronous code is more complex, and I hence don't demonstrate that in particular. On the other hand I have plenty of other complex code demonstrated. No matter what, here's how I solved it, as well as discussion of the problem:

The current implementation has no noticeable performance drawback, all depending on requirements of course. Our REST return partly depends on Wikipedia which in turn depends on Wikidata, which depends on MusicBrainz. It's not possible to parallelize these requests because of their innate dependencies. In short, it's possible to construct URLs to images at CAA without querying the service.

However, if CAA was queried, then parallelization would have been possible *and* make sense. Doing one GET request sequentially per album is very time costly, compared to a parallelized approach. I believe the implementation is complete, but I here discuss how to code if CCA had to be queried.

The mashup service is not computationally expensive, because we're not using CPU cycles ourselves on anything heavy, and we mostly run in efficiently coded libraries, the marshalling done by Jackson and so on. What do consume *time* is waiting for the returns from the various services. Hence, parallelization through threading is not needed, but asynchronous fetching (which though typically uses threads) would allow multiple GET requests to be done, and hence reduce our query time.

The async code would be implemented using Spring Boot's `@Async` applied to the function fetching the URL from CCA. The caller would 

* Relatively large images are served by the API. CCA has smaller, but that's not necessarily of interest. Here's missing in the spec. If it's supposed to be thumbnails in some UI, smaller images are useful. However, if it's to be viewed as on display on a dashboard, larger images are better, and those are served.

# How to Build & Run

This service is a Spring Boot and Gradle application, built on Java 17. Hence, you can run it by:

* Running the JAR file (included in the zip):

    java -jar build/libs/musicmashup-0.0.1-SNAPSHOT.jar

* Running it via Gradle:

    ./gradlew bootRun

After you've started the service, you can access it on your local host on port 8080, such as for The Beatles:

<http://localhost:8080/musicmashup?mbid=b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d>

