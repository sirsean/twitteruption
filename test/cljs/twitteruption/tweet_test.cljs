(ns twitteruption.tweet-test
  (:require [cljs.test :refer-macros [is are deftest testing]]
            [pjstadig.humane-test-output]
            [twitteruption.tweet :as t]))

(deftest test-format-tweet
  (are [fmt content index total expected]
       (= expected (t/format-tweet fmt content index total))

       "" "something" 10 20 ""
       "{text}" "entry" 1 10 "entry"
       "{text} ({index}/{total})" "entry" 1 10 "entry (1/10)"))

(deftest test-replace-index
  (are [coll index value expected]
       (= expected (t/replace-index coll index value))

       [] -1 :foo []
       [] 0 :foo []
       [] 1 :foo []
       [:foo] 0 :bar [:bar]
       [:foo :bar] 1 :baz [:foo :baz]))

(deftest test-remove-index
  (are [coll index expected]
       (= expected (t/remove-index coll index))

       [] -1 []
       [] 0 []
       [] 1 []
       [:foo] 0 []
       [:foo :bar :baz] 1 [:foo :baz]))

(deftest test-insert-index
  (are [coll index value expected]
       (= expected (t/insert-index coll index value))

       [] -1 :foo []
       [] 1 :foo []
       [] 0 :foo [:foo]
       [:foo] 0 :bar [:bar :foo]
       [:foo] 1 :bar [:foo :bar]
       [:foo] 2 :bar [:foo]
       [:foo :bar] 1 :baz [:foo :baz :bar]))

(deftest test-move-index
  (are [coll from to expected]
       (= expected (t/move-index coll from to))

       [] 0 0 []
       [] -1 0 []
       [] 0 -1 []
       [] 0 1 []
       [] 1 0 []
       [:foo :bar] 1 0 [:bar :foo]
       [:foo :bar] 0 1 [:bar :foo]
       [:zero :one :two :three :four] 0 1 [:one :zero :two :three :four]))
