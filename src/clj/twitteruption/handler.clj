(ns twitteruption.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [twitteruption.layout :refer [error-page]]
            [twitteruption.routes.home :refer [home-routes]]
            [twitteruption.routes.api :refer [api-routes]]
            [twitteruption.routes.auth :refer [auth-routes]]
            [compojure.route :as route]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [twitteruption.env :refer [defaults]]
            [environ.core :refer [env]]
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
    (-> #'api-routes
        (wrap-json-body {:keywords? true})
        wrap-json-response)
    (-> #'auth-routes
        wrap-json-response)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app []
  (-> #'app-routes
      (session/wrap-session {:store (cookie-store {:key (env :cookie-store-key)})})
      middleware/wrap-base))
