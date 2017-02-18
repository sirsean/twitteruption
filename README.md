# twitteruption

This webapp will help you construct a tweetstorm and send it out to Twitter all at once.

A veritable eruption of tweets!

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

You're going to need to set up `profiles.clj`:

```
{:profiles/dev  {:env {:port 4000
                       :cookie-store-key "a 16-byte secret"
                       :hostname "http://localhost:4000"
                       :consumer-token "<twitter consumer token>"
                       :consumer-secret "<twitter consumer secret>"}}
 :profiles/test {:env {}}}
```

To start a web server for the application, run:

    lein run

To start the frontend:

    lein figwheel

To run the tests:

    lein doo phantom test once
