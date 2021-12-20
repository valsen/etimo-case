import * as handlers from "./handlers.js";

const routes = {
    "restock": {
        regex: /^(I)(\d+)$/,
        handler: handlers.restock
    },
    "sell": {
        regex: /^(S)(\d+)$/,
        handler: handlers.sell
    },
    "printInventory": {
        regex: /^(L)$/,
        handler: handlers.printInventoryMsg
    }
};

const parseCommandArgs = (route, match) => {
    const args = {};
    switch (route) {
        case "restock":
        case "sell":
            args.qty = parseInt(match[2]);
            break;
    }
    return args;
}

const parseCommand = (str) => {
    for (const [route, { regex, handler }] of Object.entries(routes)) {
        const match = str.match(regex);
        if (match) {
            return {
                handler: handler,
                args: parseCommandArgs(route, match)
            };
        }
    };
    throw "Command not recognized, please try again.";
}

export { parseCommand }