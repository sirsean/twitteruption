(ns twitteruption.db)

(def default-db
  {:page :home
   :storm {:whoami nil
           :format "{text} ({index}/{total})"
           :content ""
           :tweets []
           :last-tweetstorm-href nil}})
