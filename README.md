# Etimo Case
This is a simple but extensible implementation of a console application
written in Node.js with the following functionality currently implemented:
1) The user can sell products with the command `S` followed by a positive integer, e.g. `S5`.
2) The user can re-stock products with the command `I` followed by a positive integer, e.g. `I3`.
3) The user can query the current inventory count with the command `L`.

User commands are routed using the `router`'s `parseCommand` function, which is a pure function that
returns an object with the properties `handler` and `args` for the given command.
New commands can be easily added to the `router`'s `routes` object, and creating a new handler
is as simple as adding a function to the `handlers` module.

## Tests
Jest is used as the testing framework.

## How to run the program and test suite
From the root directory, you can run the program directly with:
```
node .
```
You can also install install the program as a global script with:
```
npm install -g .
```
The globally installed script can be run as a command line application with:
```
etimo-case
```
Run the test suite with:
```
npm run test
``` 
