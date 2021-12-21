const data = require('./data');

const sell = ({ qty }) => data.setInventory(data.getInventory() - qty);

const restock = ({ qty }) => data.setInventory(data.getInventory() + qty);

const printInventoryMsg = () => console.log(data.getInventoryMsg());

module.exports = { sell, restock, printInventoryMsg };