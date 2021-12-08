(ns etimo-case.test
  (:require [clojure.test :refer [run-tests]]
            [etimo-case.core]
            [etimo-case.data]
            [etimo-case.handlers]))

(defn -main []
  (let [{:keys [fail error]} (run-tests 'etimo-case.core
                                        'etimo-case.data
                                        'etimo-case.handlers)]
    (System/exit (if (zero? (+ fail error)) 0 1))))

