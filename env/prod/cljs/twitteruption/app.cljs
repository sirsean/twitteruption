(ns twitteruption.app
  (:require [twitteruption.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
