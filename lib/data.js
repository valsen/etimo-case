const data = {};

var inventory = 0;

data.getInventory = () => inventory;

data.setInventory = (newInventory) => {
    if (newInventory === inventory)
        throw "The command has no effect on the inventory.";
    if (newInventory < 0)
        throw "Inventory cannot be negative.";
    inventory = newInventory;
}

data.getInventoryMsg = () => "Current inventory is: " + inventory;

module.exports = data;
