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

-- Insert sample items
INSERT INTO items (itemName, category, size, colour, price, quantity, supplier_id) VALUES
('T-Shirt', 'Clothing', 'M', 'Red', 19.99, 50, 1),
('Jeans', 'Clothing', 'L', 'Blue', 49.99, 30, 1),
('Sneakers', 'Footwear', '42', 'White', 89.99, 20, 2),
('Jacket', 'Outerwear', 'XL', 'Black', 99.50, 15, 2);

SELECT * FROM users;
SELECT * FROM items;
SELECT * FROM suppliers;