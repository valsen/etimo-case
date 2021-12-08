(ns etimo-case.data
  {:author "Victor Josephson"}
  (:require [clojure.test :refer [is]]))

;; The inventory count is the only global, mutable state of the program.
;; For this specific implementation (single-user console app),
;; it would arguably be better to remove it from global state and just pass
;; a value around between the functions to minimize/eliminate side-effects.
;; But keeping it in global state makes sense for future multi-client support,
;; e.g. if the program is made into a web app where the inventory state would be
;; shared between all clients.
(def inventory (atom 0))

(defn get-inventory []
  @inventory)

(defn set-inventory!
  "Resets the state of the inventory atom to the value of new-inventory."
  [new-inventory]
  (reset! inventory new-inventory))

(defn update-inventory!
  "This function has the side-effect of applying function f to the
   current value of the inventory atom and x. Throws exception on invalid updates."
  {:test (fn []
           ;; negative adjustment decreases inventory
           (is (= (do
                    (set-inventory! 10)
                    (update-inventory! - 3))
                  7))
           ;; postitive adjustment increases inventory
           (is (= (do
                    (set-inventory! 0)
                    (update-inventory! + 1))
                  1))
           ;; adjustment cannot be nil
           (is (thrown? Exception (update-inventory! + nil)))
           ;; adjustment cannot be 0
           (is (thrown? Exception (do
                                    (set-inventory! 1)
                                    (update-inventory! - 0))))
           ;; inventory cannot become negative.
           (is (thrown? Exception (do
                                    (set-inventory! 1)
                                    (update-inventory! - 2)))))}
  [f x]
  (when (nil? x)
    (throw (ex-info "The command's numerical argument cannot be nil." {})))
  (when (identical? (f @inventory x) @inventory)
    (throw (ex-info "The command has no effect on the inventory." {})))
  (when (< (f @inventory x) 0)
    (throw (ex-info "Inventory cannot be negative." {})))
  (swap! inventory f x))

