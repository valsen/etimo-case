const data = require('./data');
const errors = require('./errors');

beforeEach(() => {
    data.setInventory(0);
});

test('set and get inventory', () => {
    data.setInventory(7);
    expect(data.getInventory()).toBe(7);
    data.setInventory(123456);
    expect(data.getInventory()).toBe(123456);
});

test('inventory message contains correct inventory', () => {
    data.setInventory(5);
    const expectedInventoryMsg = "Current inventory is: 5";
    expect(data.getInventoryMsg()).toBe(expectedInventoryMsg);
});

test('negative inventory throws exception', () => {
    expect(() => data.setInventory(-1)).toThrow(errors.NEGATIVE_INVENTORY);
    expect(() => data.setInventory(-99)).toThrow(errors.NEGATIVE_INVENTORY);
});