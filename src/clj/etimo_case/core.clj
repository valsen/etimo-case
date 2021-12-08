(ns etimo-case.core
  {:author "Victor Josephson"}
  (:require [clojure.test :refer [is]]
            [etimo-case.data :as data]
            [etimo-case.handlers :as handlers]))

;; This regex pattern enforces a consistent syntax for user commands.
;; It matches strings beginning with one or more letters (representing an action),
;; optionally followed by one or more digits as a numerical argument.
(def command-pattern #"^([a-zA-Z]+)([\d]+)?$")

(defn get-user-input
  "Asks the user to enter a command. Returns the user's input as a string."
  []
  (print "Enter a command: ")
  (flush)
  (read-line))

(defn parse-command
  "This function acts as a router for incoming commands. 
   The output map contains a :handler function and the current :inventory,
   as well as any additional keys needed by the handler function.
   An exception is thrown if command is invalid/unrecognized.

   Adding new commands and program behavior can be easily done here by
   extending the condp form with more command names and handler functions,
   as long as the command conforms to the command-pattern regex.
   As an example, I have added an 'exit' command to terminate the program."
  {:test (fn []
           (is (= (parse-command "S12")
                  {:handler handlers/sell!
                   :args {:qty 12}}))
           (is (= (parse-command "I3")
                  {:handler handlers/re-stock!
                   :args {:qty 3}}))
           (is (= (parse-command "L")
                  {:handler handlers/print-inventory-message}))
           ;; 'X' is not a valid command
           (is (thrown? Exception (parse-command "X1")))
           ;; The 'S' command must be uppercase
           (is (thrown? Exception (parse-command "s1")))
           ;; commands cannot have trailing characters after its numerical component.
           (is (thrown? Exception (parse-command "I5xxx"))))}
  [input]
  (let [[_ action qty] (re-find command-pattern input)]
    (condp = action
      "I" {:handler handlers/re-stock!
           :args {:qty (read-string qty)}}
      "S" {:handler handlers/sell!
           :args {:qty (read-string qty)}}
      "L" (if qty
            (throw (ex-info "Command 'L' does not take any argument." {}))
            {:handler handlers/print-inventory-message})
      "exit" {:handler handlers/exit}
      ;; throw exception if no match
      (throw (ex-info "Command not recognized, please try again." {})))))

(defn console-loop
  "This function parses a command from user input, calls its handler function and then calls itself.
   If an exception is caught, an error message is printed before recursing."
  []
  (try
    (as-> (get-user-input) $
      (parse-command $)
      ((:handler $) (:args $)))
    (console-loop)
    (catch Exception e
      (println (.getMessage e))
      (console-loop))))

(defn -main
  "The program's entrypoint."
  []
  (data/set-inventory! 0)
  (console-loop))

