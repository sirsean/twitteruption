(ns twitteruption.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[twitteruption started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[twitteruption has shut down successfully]=-"))
   :middleware identity})
