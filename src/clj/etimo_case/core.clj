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
  (:require [clojure.test :refer [is]]))

;; This regex pattern enforces a consistent syntax for user commands.
;; It matches strings beginning with one or more letters (representing an action),
;; optionally followed by one or more digits as a numerical argument.
(def command-pattern #"^([a-zA-Z]+)([\d]+)?$")

;; The inventory count is the only global, mutable state of the program.
;; For this specific implementation (single-user console app),
;; it would arguably be better to remove it from global state and just pass
;; a value around between the functions to minimize/eliminate side-effects.
;; But keeping it in global state makes sense for future multi-client support,
;; e.g. if the program is made into a web app where the inventory state would be
;; shared between all clients.
(def inventory (atom 0))

(defn set-inventory!
  "Resets the state of the inventory atom to the value of new-inventory."
  [new-inventory]
  (reset! inventory new-inventory))

(defn get-user-input
  "Asks the user to enter a command. Stores the user's input with an :input key
   in the supplied data map, which is then returned."
  []
  (print "Enter a command: ")
  (flush)
  (read-line))

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

(defn sell!
  "Removes qty from inventory."
  {:test (fn []
           ;; tests a sequence of three calls to sell!
           (set-inventory! 3)
           (is (= (sell! {:qty 2}) 1))
           (is (= (sell! {:qty 1}) 0))
           (is (thrown? Exception (sell! {:qty 1}))))}
  [{:keys [qty]}]
  (update-inventory! - qty))

(defn re-stock!
  "Adds qty to inventory."
  {:test (fn []
           ;; tests a sequence of two calls to re-stock!
           (set-inventory! 0)
           (is (= (re-stock! {:qty 2}) 2))
           (is (= (re-stock! {:qty 9}) 11)))}
  [{:keys [qty]}]
  (update-inventory! + qty))

(defn get-inventory-message
  "Returns a message containing the current inventory count.
   For a web app, this could be used as the handler function
   for the command 'L'."
  []
  (str "The current inventory is: " @inventory))

(defn print-inventory-message
  "Prints the inventory as a side-effect and returns the function's argument unmodified."
  [_]
  (println (get-inventory-message)))

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
                  {:handler sell!
                   :args {:qty 12}}))
           (is (= (parse-command "I3")
                  {:handler re-stock!
                   :args {:qty 3}}))
           (is (= (parse-command "L")
                  {:handler print-inventory-message}))
           ;; 'X' is not a valid command
           (is (thrown? Exception (parse-command "X1")))
           ;; The 'S' command must be uppercase
           (is (thrown? Exception (parse-command "s1")))
           ;; commands cannot have trailing characters after its numerical component.
           (is (thrown? Exception (parse-command "I5xxx"))))}
  [input]
  (let [[_ action qty] (re-find command-pattern input)]
    (condp = action
      "I" {:handler re-stock!
           :args {:qty (read-string qty)}}
      "S" {:handler sell!
           :args {:qty (read-string qty)}}
      "L" (if qty
            (throw (ex-info "Command 'L' does not take any argument." {}))
            {:handler print-inventory-message})
      "exit" {:handler (fn [_] (System/exit 0))}
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
  (console-loop))

