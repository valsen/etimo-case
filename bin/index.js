#!/usr/bin/env node

const repl = require("../lib/repl");

const app = {};

app.init = () => {
    repl.init();
}

app.init();
