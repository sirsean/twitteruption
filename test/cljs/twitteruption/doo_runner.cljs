(ns twitteruption.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [twitteruption.core-test]))

(doo-tests 'twitteruption.core-test)

