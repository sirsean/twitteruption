(ns twitteruption.tweet
  (:require [clojure.string :as s]))

(defn format-tweet
  [fmt content index total]
  (-> fmt
      (s/replace #"\{text\}" content)
      (s/replace #"\{index\}" (str index))
      (s/replace #"\{total\}" (str total))))

(defn replace-index
  [coll index value]
  (cond
    (neg? index) coll
    (>= index (count coll)) coll
    :else
    (vec (concat
           (subvec coll 0 index)
           [value]
           (subvec coll (inc index) (count coll))))))

(defn remove-index
  [coll index]
  (cond
    (neg? index) coll
    (>= index (count coll)) coll
    :else
    (vec (concat
           (subvec coll 0 index)
           (subvec coll (inc index) (count coll))))))

(defn insert-index
  [coll index value]
  (cond
    (neg? index) coll
    (> index (count coll)) coll
    :else
    (vec (concat
           (subvec coll 0 index)
           [value]
           (subvec coll index (count coll))))))

(defn move-index
  [coll from to]
  (cond
    (neg? from) coll
    (>= from (count coll)) coll
    (neg? to) coll
    (> to (count coll)) coll
    :else
    (-> coll
        (remove-index from)
        (insert-index to (nth coll from)))))
