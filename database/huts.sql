-- .read huts.sql 

-- -- Users Table: Basic authentication (plain-text passwords for simplicity)
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);

-- -- Unified Utility Readings Table
-- CREATE TABLE readings (
--     id INTEGER PRIMARY KEY AUTOINCREMENT,
--     user_id INTEGER NOT NULL,
--     type TEXT NOT NULL CHECK(type IN ('ELECTRICITY', 'WATER', 'GAS')),
--     value REAL NOT NULL CHECK(value >= 0),
--     reading_date DATE NOT NULL,
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );

-- -- Bills Table
-- CREATE TABLE bills (
--     id INTEGER PRIMARY KEY AUTOINCREMENT,
--     user_id INTEGER NOT NULL,
--     utility_type TEXT NOT NULL CHECK(utility_type IN ('ELECTRICITY', 'WATER', 'GAS')),
--     amount REAL NOT NULL CHECK(amount > 0),
--     due_date DATE NOT NULL,
--     is_paid BOOLEAN NOT NULL DEFAULT 0,
--     FOREIGN KEY (user_id) REFERENCES users(id)
-- );

-- INSERT INTO users (username, password) VALUES 
-- ('student1', 'password123'),
-- ('teacher', 'demo2023');

-- INSERT INTO readings (user_id, type, value, reading_date) VALUES 
-- (1, 'ELECTRICITY', 150.5, '2023-10-01'),
-- (1, 'WATER', 30.0, '2023-10-01'),
-- (1, 'GAS', 20.0, '2023-10-01'),
-- (2, 'ELECTRICITY', 200.0, '2023-10-01'),
-- (2, 'WATER', 40.0, '2023-10-01'),
-- (2, 'GAS', 25.0, '2023-10-01');

-- INSERT INTO bills (user_id, utility_type, amount, due_date, is_paid) VALUES 
-- (1, 'ELECTRICITY', 50.0, '2023-11-01', 0),
-- (1, 'WATER', 15.0, '2023-11-01', 0),
-- (1, 'GAS', 10.0, '2023-11-01', 0),
-- (2, 'ELECTRICITY', 70.0, '2023-11-01', 1),
-- (2, 'WATER', 20.0, '2023-11-01', 1),
-- (2, 'GAS', 15.0, '2023-11-01', 1);

-- -- get all electric readings for a user
-- SELECT * FROM readings 
-- WHERE user_id = 1 AND type = 'ELECTRICITY'
-- ORDER BY reading_date DESC;

-- -- get all unpaid bills for a user
-- SELECT * FROM bills 
-- WHERE user_id = 1 AND is_paid = 0 
-- AND due_date < DATE('now');

-- -- get total electricity consumption for the current month
-- SELECT SUM(value) AS total_kwh 
-- FROM readings 
-- WHERE type = 'ELECTRICITY' 
--   AND user_id = 1 
--   AND strftime('%Y-%m', reading_date) = strftime('%Y-%m', 'now');

