(ns twitteruption.subscriptions
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [twitteruption.tweet :as t]))

(reg-sub
  :page
  (fn [db _]
    (:page db)))

(reg-sub
  :whoami
  (fn [db _]
    (-> db :storm :whoami)))

(reg-sub
  :format
  (fn [db _]
    (-> db :storm :format)))

(reg-sub
  :content
  (fn [db _]
    (-> db :storm :content)))

(reg-sub
  :editing
  (fn [db _]
    (-> db :storm :editing)))

(reg-sub
  :tweets
  (fn [db _]
    (-> db :storm :tweets)))

(reg-sub
  :last-tweetstorm-href
  (fn [db _]
    (-> db :storm :last-tweetstorm-href)))

(reg-sub
  :formatted-tweets
  (fn [_ _]
    [(subscribe [:format])
     (subscribe [:tweets])])
  (fn [[fmt tweets] _]
    (map-indexed
      (fn [i content]
        (t/format-tweet fmt content (inc i) (count tweets)))
      tweets)))
