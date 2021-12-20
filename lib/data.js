var inventory = 0;

const getInventory = () => inventory;

const setInventory = (newInventory) => {
    if (newInventory === inventory)
        throw "The command has no effect on the inventory.";
    if (newInventory < 0)
        throw "Inventory cannot be negative.";
    inventory = newInventory;
}

const getInventoryMsg = () => "Current inventory is: " + inventory;

export { getInventory, setInventory, getInventoryMsg };
