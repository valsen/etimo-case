const handlers = require('./handlers');
const data = require('./data');

beforeEach(() => {
    data.setInventory(0);
})

describe('Unit tests for handlers check that correct functions are called with correct args', () => {
    test('sell handler should call setInventory with current inventory - sell quantity', () => {
        // setup: set inventory to 4, then mock setInventory
        data.setInventory(4);
        let spy = jest.spyOn(data, 'setInventory');
        spy.mockReturnValue();
        // test: sell 5. setInventory should be called with 4 - 5 = -1
        handlers.sell({qty: 5});
        expect(data.setInventory).toHaveBeenCalledWith(-1);
        // teardown: restore original implementation of setInventory
        spy.mockRestore();
    
        // setup: set inventory to 20, then mock setInventory
        data.setInventory(20);
        spy = jest.spyOn(data, 'setInventory');
        spy.mockReturnValue();
        // test: sell 1. setInventory should be called with 20 - 1 = 19
        handlers.sell({qty: 1});
        expect(data.setInventory).toHaveBeenCalledWith(19);
        spy.mockRestore();
    });

    test('restock handler should call setInventory with current inventory + restock quantity', () => {
        // setup: set inventory to 4, then mock setInventory
        data.setInventory(4);
        let spy = jest.spyOn(data, 'setInventory');
        spy.mockReturnValue();
        // test: restock 5. setInventory should be called with 4 + 5 = 9
        handlers.restock({qty: 5});
        expect(data.setInventory).toHaveBeenCalledWith(9);
        // teardown: restore original implementation of setInventory
        spy.mockRestore();
    
        // setup: set inventory to 20, then mock setInventory
        data.setInventory(20);
        spy = jest.spyOn(data, 'setInventory');
        spy.mockReturnValue();
        // test: restock 1. setInventory should be called with 20 + 1 = 21
        handlers.restock({qty: 1});
        expect(data.setInventory).toHaveBeenCalledWith(21);
        spy.mockRestore();
    });
});

describe('Integration tests for handlers check behavior and side effects when integrating with data module', () => {
    test('restocking should increment inventory by the given qty', () => {
        handlers.restock({ qty: 4 });
        expect(data.getInventory()).toBe(4); // 0 + 4 = 4
        handlers.restock({ qty: 1 });
        expect(data.getInventory()).toBe(5); // 4 + 1 = 5
        handlers.restock({ qty: 100 });
        expect(data.getInventory()).toBe(105); // 5 + 100 = 105
    });
    
    test('selling should decrement inventory by the given qty', () => {
        data.setInventory(10);
        handlers.sell({ qty: 4 });
        expect(data.getInventory()).toBe(6); // 10 - 4 = 6
        handlers.sell({ qty: 1 });
        expect(data.getInventory()).toBe(5); // 6 - 1 = 5
        handlers.sell({ qty: 5 });
        expect(data.getInventory()).toBe(0); // 5 - 5 = 0
    });
    
    test('Selling more than available inventory throws exception', () => {
        // inventory is 0, trying to sell 1
        expect(() => {handlers.sell({ qty: 1 })}).toThrow("Inventory cannot be negative.");
    
        // inventory is 10, trying to sell 15
        data.setInventory(10);
        expect(() => {handlers.sell({ qty: 15 })}).toThrow("Inventory cannot be negative.");
    });

})
