(ns twitteruption.routes.auth
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [environ.core :refer [env]]
            [oauth.client :as oauth]))

(def consumer (oauth/make-consumer
                (env :consumer-token)
                (env :consumer-secret)
                "https://api.twitter.com/oauth/request_token"
                "https://api.twitter.com/oauth/access_token"
                "https://api.twitter.com/oauth/authorize"
                :hmac-sha1))

(defn start-oauth
  [session]
  (let [request-token (oauth/request-token
                        consumer
                        (str (env :hostname) "/oauth/callback"))
        redirect-uri (oauth/user-approval-uri
                       consumer
                       (:oauth_token request-token))
        session (assoc session :request-token request-token)]
    (-> (response/found redirect-uri)
        (assoc :session session))))

(defn oauth-callback
  [{:keys [request-token] :as session} verifier]
  (let [access-token (oauth/access-token
                       consumer
                       request-token
                       verifier)
        session (assoc session :access-token access-token)]
    (-> (response/found "/")
        (assoc :session session))))

(defroutes auth-routes
  (GET "/oauth/start" {session :session}
       (start-oauth session))
  (GET "/oauth/callback" {session :session
                          {verifier :oauth_verifier} :params}
       (oauth-callback session verifier))
  (GET "/oauth/logout" {session :session}
       (-> (response/ok [])
           (assoc :session nil))))
