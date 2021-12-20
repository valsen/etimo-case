import * as data from './data.js';

export const sell = ({ qty }) => data.setInventory(data.getInventory() - qty);

export const restock = ({ qty }) => data.setInventory(data.getInventory() + qty);

export const printInventoryMsg = () => console.log(data.getInventoryMsg());
