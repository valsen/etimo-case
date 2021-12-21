const readline = require("readline");
const router = require("./router");

const init = () => {
    const _interface = readline.createInterface({
        input : process.stdin,
        output : process.stdout,
        prompt : '> '
    });

    _interface.prompt();

    _interface.on('line', (str) => {
        str = typeof(str) == 'string' && str.trim().length > 0 ? str.trim() : false;
        if (!str) {
            // disregard empty or whitespace commands and display new prompt
            _interface.prompt();
            return;
        };

        try {
            // get the correct handler function and args for the user provided command
            const { handler, args } = router.parseCommand(str);
            // execute the command
            handler(args);
        } catch (e) {
            console.error(e.message);
        }
        // display new prompt after successfully executed command
        _interface.prompt();
    });
    _interface.on('close', () => process.exit(0));
}

module.exports = { init };