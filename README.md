# Etimo Case
This is a simple but extensible implementation of a console application
with the following functionality currently implemented:
1) The user can sell products with the command `S` followed by a positive integer, e.g. `S5`.
2) The user can re-stock products with the command `I` followed by a positive integer, e.g. `I3`.
3) The user can query the current inventory count with the command `L`.

User commands are routed using the `core/parse-command` function, which is a pure function that
returns a map with `:handler` and `:args` keys for the given command.
New commands can be easily added to `core/parse-command`, and creating a new handler
is as simple as adding a function to the `handlers` namespace (in `src/clj/etimo_case/handlers.core`).

## Tests
I've included test cases right in the function definitions,
between the docstring and function parameters. This way it's easy to
quickly get an understanding of what a function does, by looking
at the inputs and outputs of the test cases right along the source code.

## How to run the program and test suite
First, make sure you have Java and the [Clojure command line tools](https://clojure.org/guides/getting_started) installed on your machine.
If you're on a mac with homebrew, install the command line tools with:
```
brew install clojure/tools/clojure
```

From the root of the repo, run the program with:
```
clj -M:run
```
Run the test suite with:
```
clj -M:test
``` 

You can also build an uberjar of the program with the following command:
```
clojure -X:uberjar :jar EtimoCase.jar :main-class etimo-case.core
```
Then run it with:
```
java -cp EtimoCase.jar clojure.main -m etimo-case.core
```

