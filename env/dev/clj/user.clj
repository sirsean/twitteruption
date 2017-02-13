(ns user
  (:require [mount.core :as mount]
            [twitteruption.figwheel :refer [start-fw stop-fw cljs]]
            twitteruption.core))

(defn start []
  (mount/start-without #'twitteruption.core/http-server
                       #'twitteruption.core/repl-server))

(defn stop []
  (mount/stop-except #'twitteruption.core/http-server
                     #'twitteruption.core/repl-server))

(defn restart []
  (stop)
  (start))


