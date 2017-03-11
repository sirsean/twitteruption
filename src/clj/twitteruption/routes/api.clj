(ns twitteruption.routes.api
  (:require [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [byte-streams :as bs]
            [aleph.http :as http]
            [manifold.deferred :as md]
            [cheshire.core :as json]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [twitteruption.routes.auth :refer [consumer]]
            [oauth.client :as oauth]))

(defn whoami
  [session]
  (if-let [username (-> session :access-token :screen_name)]
    (response/ok {:username username})
    (response/not-found)))

(defn send-tweet
  [access-token status]
  (let [creds (oauth/credentials
                consumer
                (:oauth_token access-token)
                (:oauth_token_secret access-token)
                :POST
                "https://api.twitter.com/1.1/statuses/update.json"
                status)]
    (->
      (md/chain
        (http/post
          "https://api.twitter.com/1.1/statuses/update.json"
          {:query-params (merge creds status)})
        :body
        bs/to-reader
        #(json/parse-stream % ->kebab-case-keyword))
      (md/catch
        (fn [e]
          (println "ERROR TWEETING" status (.getMessage e)))))))

(defn tweet-url
  [username id]
  (str "https://twitter.com/" username "/status/" id))
  
(defn erupt
  [session tweets]
  (println "sending" tweets)
  (let [access-token (:access-token session)]
    (loop [first-id nil
           last-id nil
           tweets tweets]
      (if (pos? (count tweets))
        (let [status {:status (first tweets)
                      :in_reply_to_status_id last-id}
              response @(send-tweet access-token status)
              id (:id response)]
          (println id (:text response))
          (recur (or first-id id) id (rest tweets)))
        (response/ok {:id first-id
                      :href (tweet-url (:screen_name access-token) first-id)})))))

(defroutes api-routes
  (GET "/api/whoami" {session :session}
       (whoami session))
  (POST "/api/erupt" {session :session
                      body :body}
        (erupt session body)))

