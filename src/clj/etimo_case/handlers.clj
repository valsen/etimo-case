(ns etimo-case.handlers
  {:author "Victor Josephson"}
  (:require [clojure.test :refer [is]]
            [etimo-case.data :as data]))

(defn sell!
  "Removes qty from inventory."
  {:test (fn []
           ;; tests a sequence of three calls to sell!
           (data/set-inventory! 3)
           (is (= (sell! {:qty 2}) 1))
           (is (= (sell! {:qty 1}) 0))
           (is (thrown? Exception (sell! {:qty 1}))))}
  [{:keys [qty]}]
  (data/update-inventory! - qty))

(defn re-stock!
  "Adds qty to inventory."
  {:test (fn []
           ;; tests a sequence of two calls to re-stock!
           (data/set-inventory! 0)
           (is (= (re-stock! {:qty 2}) 2))
           (is (= (re-stock! {:qty 9}) 11)))}
  [{:keys [qty]}]
  (data/update-inventory! + qty))

(defn get-inventory-message
  "Returns a message containing the current inventory count.
   For a web app, this could be used as the handler function
   for the command 'L'."
  []
  (str "The current inventory is: " (data/get-inventory)))

(defn print-inventory-message
  "Prints a message with the current inventory."
  [_]
  (println (get-inventory-message)))

(defn exit
  [_]
  (System/exit 0))
