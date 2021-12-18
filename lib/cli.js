const readline = require('readline');
const handlers = require('./handlers');

const CMD_PATTERN = /^([a-zA-Z]+)([\d]+)?$/;

const parseCommand = (str) => {
    str = typeof(str) == 'string' && str.trim().length > 0 ? str.trim() : false;
    if (!str) throw "Please try again.";
    const cmdMatch = str.match(CMD_PATTERN);
    if (!cmdMatch) throw "Command syntax invalid, please try again.";

    const [_, action, qty] = str.match(CMD_PATTERN);
    const cmdObj = {};
    switch (action) {
        case "I":
            cmdObj.handler = handlers.restock;
            cmdObj.args = { qty: parseInt(qty) };
            break;
        case "S":
            cmdObj.handler = handlers.sell;
            cmdObj.args = { qty: parseInt(qty) };
            break;
        case "L":
            cmdObj.handler = handlers.printInventoryMsg;
            break;
        default:
            throw "Command not recognized, please try again.";
    };
    return cmdObj;
}

export const init = () => {
    const _interface = readline.createInterface({
        input : process.stdin,
        output : process.stdout,
        prompt : 'Enter a command: '
    });

    _interface.prompt();

    _interface.on('line', (str) => {
        try {
            const { handler, args } = parseCommand(str);
            handler(args);
        } catch (e) {
            console.log(e);
        }
        _interface.prompt();
    });
    _interface.on('close', () => process.exit(0));
}

