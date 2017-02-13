(ns twitteruption.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [twitteruption.layout :refer [error-page]]
            [twitteruption.routes.home :refer [home-routes]]
            [compojure.route :as route]
            [twitteruption.env :refer [defaults]]
            [mount.core :as mount]
            [twitteruption.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
