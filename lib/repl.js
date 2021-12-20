import readline from "readline";
import * as router from "./router.js";

export const init = () => {
    const _interface = readline.createInterface({
        input : process.stdin,
        output : process.stdout,
        prompt : '> '
    });

    _interface.prompt();

    _interface.on('line', (str) => {
        str = typeof(str) == 'string' && str.trim().length > 0 ? str.trim() : false;
        if (!str) {
            _interface.prompt();
            return;
        };
        
        try {
            const { handler, args } = router.parseCommand(str);
            handler(args);
        } catch (e) {
            console.log(e);
        }
        _interface.prompt();
    });
    _interface.on('close', () => process.exit(0));
}

