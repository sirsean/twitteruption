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
  :current-username
  (fn [_ _]
    (subscribe [:whoami]))
  (fn [whoami _]
    (-> whoami :username)))

(reg-sub
  :short-url-length
  (fn [_ _]
    (subscribe [:whoami]))
  (fn [whoami _]
    (-> whoami :configs :short-url-length-https)))

(reg-sub
  :whoami-fetched?
  (fn [db _]
    (-> db :storm :whoami-fetched?)))

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
  :num-tweets
  (fn [_ _]
    (subscribe [:tweets]))
  (fn [tweets _]
    (count tweets)))

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

(reg-sub
  :longest-tweet
  (fn [_ _]
    [(subscribe [:short-url-length])
     (subscribe [:formatted-tweets])])
  (fn [[url-length tweets] _]
    (apply max (->> tweets
                    (map #(t/url-length-placeholder % url-length))
                    (map count)))))

(reg-sub
  :sending?
  (fn [db _]
    (-> db :storm :sending?)))
