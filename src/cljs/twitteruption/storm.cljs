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

(defn logout
  [e]
  (.preventDefault e)
  (rf/dispatch [:ts-logout]))

(defn header
  []
  (let [whoami @(rf/subscribe [:whoami])]
    [:div.header.row.middle-xs
     [:div.col-xs-3.col-md-1
      [:img.logo {:src "/img/logo.png"}]]
     [:div.col-xs-5.col-md-7
      [:div.title "twitteruption"]]
     (when whoami
       [:div.col-xs-4.end-xs
        [:p
         (:username whoami)]
        [:p
         [:a.action
          {:href "#"
           :on-click logout}
          "Logout"]]])]))

(defn character-count
  [length]
  (let [clz (if (> length 140)
              "character-count invalid"
              "character-count")]
    [:div {:class clz}
     (str length " characters")]))

(defn formatter
  []
  (let [fmt @(rf/subscribe [:format])]
    [:div.formatter.row.middle-xs
     [:div.col-xs-2.end-xs
      [:span "Format"]]
     [:div.col-xs-10.col-md-8
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
       [:div.col-xs-12.center-xs
        [:textarea {:id "editor-textarea"
                    :value content
                    :rows 6
                    :on-key-press (fn [e]
                                    (let [shift? (.-shiftKey e)
                                          enter? (= 13 (.-which e))]
                                      (when (and shift? enter?)
                                        (.preventDefault e)
                                        (rf/dispatch [:add-ts-tweet content]))))
                    :on-change #(rf/dispatch [:set-ts-content (tv %)])}]]]
      [:div.row.middle-xs
       [:div.col-xs-4
        (when (pos? (count content))
          (character-count formatted-length))]
       (if (nil? editing)
         [:div.col-xs-8.end-xs
          [:button.big
           {:disabled (or (<= (count content) 0) (> formatted-length 140))
            :on-click (fn []
                        (.. js/document
                            (getElementById "editor-textarea")
                            (focus))
                        (rf/dispatch [:add-ts-tweet content]))}
           "Add"]]
         [:div.col-xs-8.end-xs
          [:a.action
           {:href "#"
            :on-click (fn [e]
                        (.preventDefault e)
                        (rf/dispatch [:ts-stop-editing]))}
           "Cancel"]
          [:button.big
           {:disabled (or (<= (count content) 0) (> formatted-length 140))
            :on-click #(rf/dispatch [:ts-save-tweet editing content])}
           "Save"]])]]]))

(defn show-tweet
  [i tweet num-tweets]
  (let [length (count tweet)]
    ^{:key i}
    [:div.tweet.row.middle-xs
     [:div.col-xs-10
      [:div.content.row
       [:div.col-xs-12
        [:span tweet]]]
      [:div.info.row.bottom-xs
       [:div.col-xs-6
        (character-count length)]
       [:div.col-xs-6.end-xs
        [:a.action
         {:href "#"
          :on-click (fn [e]
                      (.preventDefault e)
                      (rf/dispatch [:ts-edit-tweet i]))}
         "Edit"]
        [:a.action
         {:href "#"
          :on-click (fn [e]
                      (.preventDefault e)
                      (rf/dispatch [:ts-delete-tweet i]))}
         "Delete"]]]]
     [:div.col-xs-2
      [:div.row
       [:div.col-xs-12
        [:button.small
         {:disabled (<= i 0)
          :on-click #(rf/dispatch [:ts-move-tweet i (dec i)])}
         [:i.fa.fa-arrow-up]]]]
      [:div.row
       [:div.col-xs-12
        [:button.small
         {:disabled (>= i (dec num-tweets))
          :on-click #(rf/dispatch [:ts-move-tweet i (inc i)])}
         [:i.fa.fa-arrow-down]]]]]]))

(defn tweet-list
  []
  (let [tweets @(rf/subscribe [:formatted-tweets])
        last-tweetstorm-href @(rf/subscribe [:last-tweetstorm-href])
        num-tweets @(rf/subscribe [:num-tweets])
        longest-tweet @(rf/subscribe [:longest-tweet])
        sending? @(rf/subscribe [:sending?])
        send-disabled? (or (<= num-tweets 0)
                           (> longest-tweet 140)
                           sending?)]
    [:div.row.tweet-list
     [:div.col-xs-12
      (when last-tweetstorm-href
        [:div.row
         [:div.col-xs-12.center-xs
          [:a {:href last-tweetstorm-href
               :target "_blank"}
           "Go see it on Twitter"]]])
      (when (and (not (seq tweets))
                 (not last-tweetstorm-href))
        [:div.row
         [:div.col-xs-12.center-xs
          [:em "Your tweets will appear here before they are sent."]]])
      (when (seq tweets)
        [:div.row
         [:div.col-xs-12
          [:div.row
           [:div.col-xs-12
            (map-indexed #(show-tweet %1 %2 num-tweets) tweets)]]
          (let [send-text (send-button-text num-tweets)]
            [:div.row
             [:div.col-xs-12.center-xs
              [:button.big
               {:disabled send-disabled?
                :on-click #(rf/dispatch [:ts-send-tweets tweets])}
               send-text]]])]])]]))

(defn unauthenticated
  []
  [:div.row
   [:div.col-xs-10.col-xs-offset-1.center-xs
    [:p "This is twitteruption."]
    [:p "It will let you construct a tweetstorm and send it out all at once. And they'll be threaded, just as they're supposed to be!"]
    [:p "Like an eruption of tweets, as it were."]
    [:p "But in order to use it, you have to log in via Twitter."]]
   [:div.col-xs-12.center-xs
    [:p [:a.action
         {:href "/oauth/start"}
         "Authenticate"]]]])

(defn component
  []
  (let [whoami @(rf/subscribe [:whoami])]
    [:div.container
     (header)
     (if-not whoami
       (unauthenticated)
       [:div.row
        [:div.col-xs-12
         (formatter)
         [:div.row
          [:div.col-xs-12.col-md-6
           (editor)]
          [:div.col-xs-12.col-md-6
           (tweet-list)]]]])]))
