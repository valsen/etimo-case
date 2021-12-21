const router = require('./router');
const handlers = require('./handlers');
const errors = require('./errors');

test('Command L returns printInventoryMsg handler and empty args', () => {
    const expectedCmdObj = {
        handler: handlers.printInventoryMsg,
        args: {}
    };
    const actualCmdObj = router.parseCommand('L');
    expect(actualCmdObj).toEqual(expectedCmdObj);
});

test('Command I followed by non-negative int returns restock handler and correct qty', () => {
    // I5
    let expectedCmdObj = {
        handler: handlers.restock,
        args: { qty: 5 }
    };
    expect(router.parseCommand('I5')).toEqual(expectedCmdObj);

    // I999999
    expectedCmdObj = {
        handler: handlers.restock,
        args: { qty: 999999 }
    };
    expect(router.parseCommand('I999999')).toEqual(expectedCmdObj);
});

test('Command S followed by non-negative int returns sell handler and correct qty', () => {
    // S1
    let expectedCmdObj = {
        handler: handlers.sell,
        args: { qty: 1 }
    };
    expect(router.parseCommand('S1')).toEqual(expectedCmdObj);

    // I345
    expectedCmdObj = {
        handler: handlers.sell,
        args: { qty: 345 }
    };
    expect(router.parseCommand('S345')).toEqual(expectedCmdObj);
});

test('Invalid commands throw exception', () => {
    expect(() => {router.parseCommand('Lasdf')}).toThrow(errors.INVALID_COMMAND);
    expect(() => {router.parseCommand('L3')}).toThrow(errors.INVALID_COMMAND);
    expect(() => {router.parseCommand('l')}).toThrow(errors.INVALID_COMMAND);
    expect(() => {router.parseCommand('S-1')}).toThrow(errors.INVALID_COMMAND);
    expect(() => {router.parseCommand('S5a')}).toThrow(errors.INVALID_COMMAND);
    expect(() => {router.parseCommand('I-10')}).toThrow(errors.INVALID_COMMAND);
    expect(() => {router.parseCommand('I10x')}).toThrow(errors.INVALID_COMMAND);
});