(ns merantix-test.prod
  (:require [merantix-test.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
