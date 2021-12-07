(ns etimo-case.core
  "This is a simple but extendable implementation of a console application
   with the following functionality currently implemented:
   1) The user can sell products with the command 'S' followed by a positive integer, e.g. 'S5'.
   2) The user can re-stock products with the command 'I' followed by a positive integer, e.g. 'I3'.
   3) The user can query the current inventory count with the command 'L'.
   4) The user can optionally set an initial inventory count by supplying it as a command-line arg to the program.

   My goal with this implementation is to eliminate global state and the complexity that usually comes with managing it.
   I have tried to keep each function as 'pure' as possible, i.e. with few or no side-effects.

   I have also tried to make it easy to add new user commands and behavior, by defining a command
   as a map that contains the command's handler function, the current inventory and
   any other data needed by the handler function.

   I've included test cases right in the function definitions,
   between the docstring and function parameters. This way it's easy to
   quickly get an understanding of what a function accomplishes, by looking
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

(defn get-user-input
  "Asks the user to enter a command. Stores the user's input with an :input key
   in the supplied data map, which is then returned."
  [data]
  (print "Enter a command: ")
  (flush)
  (assoc data :input (read-line)))

(defn update-inventory
  "Returns a map with the updated :inventory as the sum of the current inventory and the adjustment amount. 
   Throws exception on invalid adjustment."
  {:test (fn []
           ;; negative adjustment decreases inventory
           (is (= (update-inventory {:inventory 10
                                     :adjustment -3})
                  {:inventory 7}))
           ;; postitive adjustment increases inventory
           (is (= (update-inventory {:inventory 0
                                     :adjustment 1})
                  {:inventory 1}))
           ;; adjustment cannot be nil
           (is (thrown? Exception (update-inventory {:inventory 0
                                                     :adjustment nil})))
           ;; adjustment cannot be 0
           (is (thrown? Exception (update-inventory {:inventory 1
                                                     :adjustment 0})))
           ;; inventory cannot become negative. TODO: should the available stock be sold?
           (is (thrown? Exception (update-inventory {:inventory 1
                                                     :adjustment -2}))))}
  [{:keys [inventory adjustment]}]
  (when (nil? adjustment)
    (throw (ex-info "Adjustment cannot be nil." {})))
  (when (zero? adjustment)
    (throw (ex-info "Adjustment cannot be 0." {})))
  (when (< (+ inventory adjustment) 0)
    (throw (ex-info "Inventory cannot be negative." {})))
  {:inventory (+ inventory adjustment)})

(defn print-inventory
  "Prints the inventory as a side-effect and returns the function's argument unmodified."
  [data]
  (println (str "The current inventory is: " (:inventory data)))
  data)

(defn parse-command
  "Parses the raw user-input into a map representing a program command. 
   The output map contains a :handler function and the current :inventory,
   as well as any additional keys needed by the handler function.
   Throws exception on invalid user input.

   Adding new commands and program behavior can be easily done here by
   extending the condp form with more command names and handler functions,
   as long as the command conforms to the command-pattern regex.
   As an example, I have added an 'exit' command to terminate the program.
"
  {:test (fn []
           (is (= (parse-command {:input "S12"
                                  :inventory 0})
                  {:handler update-inventory
                   :inventory 0
                   :adjustment -12}))
           (is (= (parse-command {:input "I3"
                                  :inventory 0})
                  {:handler update-inventory
                   :inventory 0
                   :adjustment 3}))
           (is (= (parse-command {:input "L"
                                  :inventory 5})
                  {:handler print-inventory
                   :inventory 5}))
           ;; 'X' is not a valid command
           (is (thrown? Exception (parse-command {:input "X1"
                                                  :inventory 0})))
           ;; The 'S' command must be uppercase
           (is (thrown? Exception (parse-command {:input "s1"
                                                  :inventory 0})))
           ;; commands cannot have trailing characters after its numerical component.
           (is (thrown? Exception (parse-command {:input "I5xxx"
                                                  :inventory 0}))))}
  [{:keys [input inventory]}]
  (let [[_ action qty] (re-find command-pattern input)]
    (condp = action
      "I" {:handler update-inventory
           :inventory inventory
           :adjustment (read-string qty)}
      "S" {:handler update-inventory
           :inventory inventory
           :adjustment (* -1 (read-string qty))}
      "L" {:handler print-inventory
           :inventory inventory}
      "exit" {:handler (fn [_] (System/exit 0))}
      (throw (ex-info "Command not recognized, please try again" {})))))

(defn main-loop
  "This function is like a data pipeline, where each step's output is a 
   map which serves as the next step's input, starting with the initial inventory.
   At the end of the pipeline, the function recurses with the modified data map,
   which at that point contains the updated inventory. If any step throws an exception,
   its message is printed, after which main-loop recurses with the same (i.e. unmodified)
   data as it was called with."
  [{:keys [inventory] :as data}]
  (try
    (as-> data $
      (get-user-input $)
      (parse-command $)
      ((:handler $) $)
      (main-loop $))
    (catch Exception e
      (println (.getMessage e))
      (main-loop {:inventory inventory}))))

(defn -main
  "The program's entrypoint. The user may provide a non-negative integer
   argument for the initial inventory. If no initial inventory is provided,
   it defaults to 0."
  ([]
   (main-loop {:inventory 0}))
  ([inventory]
   (if-not (re-find #"^\d+$" inventory)
     (println "Invalid initial inventory provided. Exiting...")
     (main-loop {:inventory (read-string inventory)}))))

