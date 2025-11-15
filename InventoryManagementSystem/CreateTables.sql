-- Remove existing database
DROP DATABASE IF EXISTS clothing_inventory;

-- Create fresh database
CREATE DATABASE clothing_inventory;
USE clothing_inventory;

-- Users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) DEFAULT 'employee'
);

-- Insert test users
INSERT INTO users (username, password, role) VALUES
('admin', '1234', 'admin'),
('john_doe', 'pass123', 'employee');

-- Suppliers table
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    contact VARCHAR(100)
);

-- Insert sample suppliers
INSERT INTO suppliers (name, contact) VALUES
('Supplier A', 'contactA@example.com'),
('Supplier B', 'contactB@example.com');

-- Items table (expanded)
CREATE TABLE items (
    itemId INT AUTO_INCREMENT PRIMARY KEY,
    itemName VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    size VARCHAR(10),
    colour VARCHAR(20),
    price DECIMAL(10,2),
    quantity INT DEFAULT 0,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);


-- Sales table 
CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantity_sold INT NOT NULL CHECK (quantity_sold > 0),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(itemId)
);

-- Insert sample items
INSERT INTO items (itemName, category, size, colour, price, quantity, supplier_id) VALUES
('T-Shirt', 'Clothing', 'M', 'Red', 19.99, 50, 1),
('Jeans', 'Clothing', 'L', 'Blue', 49.99, 30, 1),
('Sneakers', 'Footwear', '42', 'White', 89.99, 20, 2),
('Jacket', 'Outerwear', 'XL', 'Black', 99.50, 15, 2);

-- Item_Returns table for simple returns
CREATE TABLE item_returns (
    return_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantity_returned INT NOT NULL CHECK (quantity_returned > 0),
    return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES items(itemId)
);

-- Item_Exchanges table for item swaps
CREATE TABLE item_exchanges (
    exchange_id INT AUTO_INCREMENT PRIMARY KEY,
    returned_item_id INT NOT NULL,
    returned_quantity INT NOT NULL CHECK (returned_quantity > 0),
    new_item_id INT NOT NULL,
    new_quantity INT NOT NULL CHECK (new_quantity > 0),
    exchange_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(255),
    FOREIGN KEY (returned_item_id) REFERENCES items(itemId),
    FOREIGN KEY (new_item_id) REFERENCES items(itemId)
);

SELECT * FROM users;
SELECT * FROM items;
SELECT * FROM suppliers;
SELECT * FROM sales;
SELECT * FROM item_returns;
SELECT * FROM exchanges;
