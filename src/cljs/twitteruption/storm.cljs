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
  (let [length (count tweet)]
    ^{:key i}
    [:div.row
     [:div.col-xs-10
      [:div.row
       [:div.col-xs-12
        [:span tweet]]]
      [:div.row
       [:div.col-xs-6
        (str length " characters")]
       [:div.col-xs-6.end-xs
        [:button
         {:on-click #(rf/dispatch [:ts-edit-tweet i])}
         "Edit"]
        [:button
         {:on-click #(rf/dispatch [:ts-delete-tweet i])}
         "Delete"]]]]
     [:div.col-xs-2
      [:div.row
       [:div.col-xs-12
        [:button
         {:disabled (<= i 0)
          :on-click #(rf/dispatch [:ts-move-tweet i (dec i)])}
         "Up"]]]
      [:div.row
       [:div.col-xs-12
        [:button
         {:disabled (>= i (dec num-tweets))
          :on-click #(rf/dispatch [:ts-move-tweet i (inc i)])}
         "Down"]]]]]))

(defn logout
  [e]
  (.preventDefault e)
  (rf/dispatch [:ts-logout]))

(defn header
  []
  (let [whoami @(rf/subscribe [:whoami])]
    [:div.row.middle-xs
     [:div.col-xs-8
      [:h1 "twitteruption"]]
     (if whoami
       [:div.col-xs-4.end-xs
        [:p
         (:username whoami)]
        [:p
         [:a {:href "#"
              :on-click logout}
          "Logout"]]]
       [:div.col-xs-4.end-xs
        [:a {:href "/oauth/start"}
         "Authenticate"]])]))

(defn formatter
  []
  (let [fmt @(rf/subscribe [:format])]
    [:div.row
     [:div.col-xs-12
      [:span "Format"]
      [:input {:type "text"
               :value fmt
               :on-change #(rf/dispatch [:set-ts-format (tv %)])}]]]))

(defn editor
  []
  (let [fmt @(rf/subscribe [:format])
        content @(rf/subscribe [:content])
        editing @(rf/subscribe [:editing])
        num-tweets @(rf/subscribe [:num-tweets])
        formatted-content (if (nil? editing)
                            (t/format-tweet fmt content (inc num-tweets) (inc num-tweets))
                            (t/format-tweet fmt content (inc editing) num-tweets))
        formatted-length (count formatted-content)]
    [:div.row.editor
     [:div.col-xs-12
      [:div.row
       [:div.col-xs-12
        [:textarea {:value content
                    :rows 6
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
          "Cancel"]])]]))

(defn tweet-list
  []
  (let [tweets @(rf/subscribe [:formatted-tweets])
        last-tweetstorm-href @(rf/subscribe [:last-tweetstorm-href])
        num-tweets @(rf/subscribe [:num-tweets])
        longest-tweet @(rf/subscribe [:longest-tweet])]
    [:div.row.tweet-list
     [:div.col-xs-12
      (when last-tweetstorm-href
        [:p
         [:a {:href last-tweetstorm-href
              :target "_blank"}
          "Go see it on Twitter"]])
      [:div.row
       [:div.col-xs-12
        (map-indexed #(show-tweet %1 %2 num-tweets) tweets)]]
      (let [send-text (send-button-text num-tweets)]
        [:div.row
         [:div.col-xs-12.center-xs
          [:button
           {:disabled (or (<= num-tweets 0) (> longest-tweet 140))
            :on-click #(rf/dispatch [:ts-send-tweets tweets])}
           send-text]]])]]))

(defn component
  []
  [:div.container
   (header)
   (formatter)
   [:div.row
    [:div.col-xs-12.col-lg-6
     (editor)]
    [:div.col-xs-12.col-lg-6
     (tweet-list)]]])
