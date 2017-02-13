(ns twitteruption.storm
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [twitteruption.tweet :as t]))

(defn tv
  [e]
  (-> e .-target .-value))

(defn send-button-text
  [num-tweets]
  (str "Tweet " num-tweets " time" (when (not (= 1 num-tweets)) "s")))

(defn show-tweet
  [i tweet num-tweets]
  ^{:key i}
  [:li tweet
   [:button
    {:on-click #(rf/dispatch [:ts-edit-tweet i])}
    "Edit"]
   [:button
    {:on-click #(rf/dispatch [:ts-delete-tweet i])}
    "Delete"]
   [:button
    {:disabled (<= i 0)
     :on-click #(rf/dispatch [:ts-move-tweet i (dec i)])}
    "Up"]
   [:button
    {:disabled (>= i (dec num-tweets))
     :on-click #(rf/dispatch [:ts-move-tweet i (inc i)])}
    "Down"]])

(defn logout
  [e]
  (.preventDefault e)
  (rf/dispatch [:ts-logout]))

(defn component
  []
  (let [whoami @(rf/subscribe [:whoami])
        fmt @(rf/subscribe [:format])
        content @(rf/subscribe [:content])
        editing @(rf/subscribe [:editing])
        tweets @(rf/subscribe [:formatted-tweets])
        last-tweetstorm-href @(rf/subscribe [:last-tweetstorm-href])
        num-tweets (count tweets)
        formatted-content (if (nil? editing)
                            (t/format-tweet fmt content (inc num-tweets) (inc num-tweets))
                            (t/format-tweet fmt content (inc editing) num-tweets))
        formatted-length (count formatted-content)]
    [:div.container
     [:div.row
      [:div.col-xs-12
       [:h1 "twitteruption"]]]
     (if whoami
       [:div.row
        [:div.col-xs-12
         [:p (:username whoami)]
         [:a {:href "#"
              :on-click logout}
          "Logout"]]]
       [:div.row
        [:div.col-xs-12
         [:a {:href "/oauth/start"}
          "Authenticate"]]])
     [:div.row
      [:div.col-xs-12.col-md-6
       [:div.row
        [:div.col-xs-12
         [:span "Format"]
         [:input {:type "text"
                  :value fmt
                  :on-change #(rf/dispatch [:set-ts-format (tv %)])}]]]
       [:div.row
        [:div.col-xs-12
         [:textarea {:value content
                     :on-change #(rf/dispatch [:set-ts-content (tv %)])}]]]
       [:div.row
        [:div.col-xs-12
         (str formatted-length " characters")]]
       (if (nil? editing)
         [:div
          [:button
           {:disabled (or (<= (count content) 0) (> formatted-length 140))
            :on-click #(rf/dispatch [:add-ts-tweet content])}
           "Add"]]
         [:div
          [:button
           {:disabled (or (<= (count content) 0) (> formatted-length 140))
            :on-click #(rf/dispatch [:ts-save-tweet editing content])}
           "Save"]
          [:button
           {:on-click #(rf/dispatch [:ts-stop-editing])}
           "Cancel"]])]
      [:div.col-xs-12.col-lg-6
       (when last-tweetstorm-href
         [:p
          [:a {:href last-tweetstorm-href
               :target "_blank"}
           "Go see it on Twitter"]])
       [:ul
        (map-indexed #(show-tweet %1 %2 num-tweets) tweets)]
       (let [send-text (send-button-text num-tweets)]
         [:div.row
          [:div.col-xs-12
           [:button
            {:disabled (<= num-tweets 0)
             :on-click #(rf/dispatch [:ts-send-tweets tweets])}
            send-text]]])]]
     ]))
