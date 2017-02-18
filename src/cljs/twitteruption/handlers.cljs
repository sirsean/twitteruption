(ns twitteruption.handlers
  (:require [twitteruption.db :as db]
            [re-frame.core :refer [dispatch reg-event-db reg-event-fx]]
            [twitteruption.tweet :refer [replace-index remove-index insert-index move-index]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [ajax.core :refer [GET POST]]
            [cognitect.transit :as t]))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(reg-event-db
  :set-ts-format
  (fn [db [_ fmt]]
    (assoc-in db [:storm :format] fmt)))

(reg-event-db
  :set-ts-content
  (fn [db [_ content]]
    (assoc-in db [:storm :content] content)))

(reg-event-db
  :add-ts-tweet
  (fn [db [_ content]]
    (-> db
        (update-in
          [:storm :tweets]
          conj
          content)
        (assoc-in
          [:storm :last-tweetstorm-href] nil)
        (assoc-in
          [:storm :content] ""))))

(reg-event-db
  :ts-edit-tweet
  (fn [db [_ index]]
    (let [content (nth (-> db :storm :tweets) index)]
      (-> db
          (assoc-in [:storm :content] content)
          (assoc-in [:storm :editing] index)))))

(reg-event-db
  :ts-stop-editing
  (fn [db _]
    (-> db
        (assoc-in [:storm :content] "")
        (update-in [:storm] dissoc :editing))))

(reg-event-db
  :ts-save-tweet
  (fn [db [_ index content]]
    (-> db
      (update-in [:storm :tweets] replace-index index content)
      (assoc-in [:storm :content] "")
      (update-in [:storm] dissoc :editing))))

(reg-event-db
  :ts-delete-tweet
  (fn [db [_ index]]
    (update-in db [:storm :tweets] remove-index index)))

(reg-event-db
  :ts-move-tweet
  (fn [db [_ index to]]
    (update-in db [:storm :tweets] move-index index to)))

(defn write-json
  [in]
  (t/write (t/writer :json) in))

(defn read-json
  [in]
  (t/read (t/reader :json) in))

(reg-event-fx
  :whoami
  (fn [{db :db} _]
    (GET "/api/whoami"
         {:response-format :json
          :handler #(dispatch [:got-whoami (transform-keys ->kebab-case-keyword %1)])
          :error-handler #(dispatch [:got-whoami nil])})
    {:db (assoc-in db [:storm :whoami-fetched?] false)}))

(reg-event-db
  :got-whoami
  (fn [db [_ whoami]]
    (-> db
        (assoc-in [:storm :whoami-fetched?] true)
        (assoc-in [:storm :whoami] whoami))))

(reg-event-fx
  :ts-send-tweets
  (fn [{db :db} [_ tweets]]
    (POST "/api/erupt"
          {:body (write-json (vec tweets))
           :headers {:content-type "application/json"}
           :format :json
           :response-format :json
           :handler #(dispatch [:ts-sent-tweets (transform-keys ->kebab-case-keyword %1)])})
    {:db (assoc-in db [:storm :sending?] true)}))

(reg-event-db
  :ts-sent-tweets
  (fn [db [_ {href :href}]]
    (-> db
      (assoc-in [:storm :tweets] [])
      (assoc-in [:storm :sending?] false)
      (assoc-in [:storm :last-tweetstorm-href] href))))

(reg-event-fx
  :ts-logout
  (fn [{db :db} _]
    (GET "/oauth/logout"
         {:handler #(dispatch [:ts-logged-out])})
    {:db (assoc-in db [:storm :whoami] nil)}))

(reg-event-db
  :ts-logged-out
  (fn [db _]
    db))
