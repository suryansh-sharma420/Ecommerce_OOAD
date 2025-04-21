-- Start with a clean state (respecting foreign key constraints)
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM products;

-- Electronics Products
INSERT INTO products (name, description, price, stock_quantity, category, image_url, active) VALUES
('Laptop', 'High-performance laptop with SSD', 999.99, 10, 'Electronics', 'laptop.jpg', true),
('Smartphone', 'Latest model smartphone', 699.99, 15, 'Electronics', 'smartphone.jpg', true),
('Headphones', 'Wireless noise-canceling headphones', 149.99, 20, 'Electronics', 'headphones.jpg', true),
('Smartwatch', 'Fitness tracking smartwatch', 299.99, 12, 'Electronics', 'smartwatch.jpg', true),
('Tablet', '10-inch tablet with stylus', 449.99, 8, 'Electronics', 'tablet.jpg', true),
('Camera', 'Digital mirrorless camera', 799.99, 5, 'Electronics', 'camera.jpg', true),
('Gaming Console', 'Next-gen gaming console', 499.99, 7, 'Electronics', 'console.jpg', true),
('Wireless Earbuds', 'True wireless earbuds', 199.99, 25, 'Electronics', 'earbuds.jpg', true);

-- Clothing Products
INSERT INTO products (name, description, price, stock_quantity, category, image_url, active) VALUES
('Classic T-Shirt', 'Comfortable cotton t-shirt in various colors', 24.99, 100, 'Clothing', 'tshirt.jpg', true),
('Denim Jeans', 'Premium quality slim-fit jeans', 79.99, 50, 'Clothing', 'jeans.jpg', true),
('Hooded Sweatshirt', 'Warm and cozy hoodie for casual wear', 49.99, 75, 'Clothing', 'hoodie.jpg', true),
('Athletic Shorts', 'Breathable sports shorts with pockets', 29.99, 60, 'Clothing', 'shorts.jpg', true),
('Winter Jacket', 'Waterproof winter coat with thermal lining', 129.99, 30, 'Clothing', 'jacket.jpg', true),
('Dress Shirt', 'Formal cotton dress shirt', 59.99, 40, 'Clothing', 'dress-shirt.jpg', true),
('Running Shoes', 'Lightweight athletic running shoes', 89.99, 45, 'Clothing', 'running-shoes.jpg', true),
('Casual Sneakers', 'Comfortable everyday sneakers', 69.99, 55, 'Clothing', 'sneakers.jpg', true);

-- Books Products
INSERT INTO products (name, description, price, stock_quantity, category, image_url, active) VALUES
('The Art of Programming', 'Comprehensive guide to software development', 49.99, 20, 'Books', 'programming-book.jpg', true),
('Mystery of the Hidden Key', 'Thrilling mystery novel', 19.99, 30, 'Books', 'mystery-book.jpg', true),
('Cooking Masterclass', 'Professional cooking techniques and recipes', 34.99, 25, 'Books', 'cooking-book.jpg', true),
('World History', 'Detailed overview of world history', 39.99, 15, 'Books', 'history-book.jpg', true),
('Science Fiction Collection', 'Anthology of best sci-fi stories', 24.99, 35, 'Books', 'scifi-book.jpg', true),
('Business Strategy', 'Modern business management techniques', 44.99, 20, 'Books', 'business-book.jpg', true),
('Poetry Anthology', 'Collection of contemporary poetry', 22.99, 25, 'Books', 'poetry-book.jpg', true),
('Self-Help Guide', 'Personal development and motivation', 29.99, 40, 'Books', 'selfhelp-book.jpg', true);

-- Clear existing users just in case (optional, but safe during testing)
DELETE FROM users;

-- Insert Admin User (admin@example.com / admin123)
INSERT INTO users (email, first_name, last_name, password, role, enabled)
VALUES ('admin@example.com', 'Admin', 'User', 'admin123', 'ADMIN', true);

-- Insert Customer User (user@example.com / user123)
INSERT INTO users (email, first_name, last_name, password, role, enabled)
VALUES ('user@example.com', 'Customer', 'User', 'user123', 'CUSTOMER', true);

-- Insert another Test Customer (test@example.com / test123)
INSERT INTO users (email, first_name, last_name, password, role, enabled)
VALUES ('test@example.com', 'Test', 'User', 'test123', 'CUSTOMER', true); 