(ns etimo-case.test
  (:require [clojure.test :refer [run-tests]]
            [etimo-case.core]))

(defn -main []
  (let [{:keys [fail error]} (run-tests 'etimo-case.core)]
    (System/exit (if (zero? (+ fail error)) 0 1))))

