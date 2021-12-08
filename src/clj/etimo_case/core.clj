(ns etimo-case.core
  "This is a simple but extensible implementation of a console application
   with the following functionality currently implemented:
   1) The user can sell products with the command 'S' followed by a positive integer, e.g. 'S5'.
   2) The user can re-stock products with the command 'I' followed by a positive integer, e.g. 'I3'.
   3) The user can query the current inventory count with the command 'L'.

   User commands are routed using the parse-command function, which is a pure function that
   returns a map with :handler and :args keys for the given command.
   New commands can be easily added to parse-command, and creating a new handler
   is as simple as defining a function that takes a map as its only argument.

   I've included test cases right in the function definitions,
   between the docstring and function parameters. This way it's easy to
   quickly get an understanding of what a function does, by looking
   at the inputs and outputs of the test cases right along the source code.

   With clojure command line tools installed, you can run the program with 'clj -M:run',
   and the test suite with 'clj -M:test'. You can also build an uberjar with:
  'clojure -X:uberjar :jar EtimoCase.jar :main-class etimo-case.core' and run it with:
  'java -cp EtimoCase.jar clojure.main -m etimo-case.core'."
  {:author "Victor Josephson"}
  (:require [clojure.test :refer [is]]
            [etimo-case.data :as data]
            [etimo-case.handlers :as handlers]))

;; This regex pattern enforces a consistent syntax for user commands.
;; It matches strings beginning with one or more letters (representing an action),
;; optionally followed by one or more digits as a numerical argument.
(def command-pattern #"^([a-zA-Z]+)([\d]+)?$")

(defn get-user-input
  "Asks the user to enter a command. Stores the user's input with an :input key
   in the supplied data map, which is then returned."
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

