const errors = require('./errors');

var inventory = 0;

const getInventory = () => inventory;

const setInventory = (newInventory) => {
    if (newInventory < 0)
        throw new Error(errors.NEGATIVE_INVENTORY);
    inventory = newInventory;
}

const getInventoryMsg = () => "Current inventory is: " + inventory;

module.exports = { getInventory, setInventory, getInventoryMsg };
