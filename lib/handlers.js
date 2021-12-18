const data = require('./data');

const handlers = {};

handlers.sell = ({ qty }) => data.setInventory(data.getInventory() - qty);
handlers.restock = ({ qty }) => data.setInventory(data.getInventory() + qty);
handlers.printInventoryMsg = () => console.log(data.getInventoryMsg());

module.exports = handlers;
