(ns twitteruption.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [twitteruption.core-test]
            [twitteruption.tweet-test]))

(doo-tests 'twitteruption.core-test
           'twitteruption.tweet-test)

