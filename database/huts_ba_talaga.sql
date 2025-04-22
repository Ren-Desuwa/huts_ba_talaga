-- House Utilities Management System Database Initialization Script

-- Drop existing tables if they exist to start fresh
DROP TABLE IF EXISTS reading_history;
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS subscription;
DROP TABLE IF EXISTS water;
DROP TABLE IF EXISTS gas;
DROP TABLE IF EXISTS electricity;
DROP TABLE IF EXISTS household;
DROP TABLE IF EXISTS users;

-- Create Users table
CREATE TABLE users (
    username TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    email TEXT,
    full_name TEXT
);

-- Create Household table
CREATE TABLE household (
    id TEXT PRIMARY KEY,
    name TEXT,
    address TEXT,
    number_of_occupants INTEGER
);

-- Create Electricity utility table
CREATE TABLE electricity (
    id TEXT PRIMARY KEY,
    name TEXT,
    provider TEXT,
    account_number TEXT UNIQUE,
    rate_per_kwh REAL,
    meter_reading REAL
);

-- Create Gas utility table
CREATE TABLE gas (
    id TEXT PRIMARY KEY,
    name TEXT,
    provider TEXT,
    account_number TEXT UNIQUE,
    rate_per_unit REAL,
    meter_reading REAL
);

-- Create Water utility table
CREATE TABLE water (
    id TEXT PRIMARY KEY,
    name TEXT,
    provider TEXT,
    account_number TEXT UNIQUE,
    rate_per_cubic_meter REAL,
    meter_reading REAL
);

-- Create Subscription table
CREATE TABLE subscription (
    id TEXT PRIMARY KEY,
    name TEXT,
    provider TEXT,
    account_number TEXT UNIQUE,
    type TEXT,
    monthly_cost REAL,
    next_billing_date TEXT
);

-- Create Bill table
CREATE TABLE bill (
    id TEXT PRIMARY KEY,
    utility_id TEXT,
    amount REAL,
    consumption REAL,
    issue_date TEXT,
    due_date TEXT,
    is_paid INTEGER,
    paid_date TEXT
);

-- Create Reading History table
CREATE TABLE reading_history (
    id TEXT PRIMARY KEY,
    utility_id TEXT,
    utility_type TEXT,
    reading_date TEXT,
    reading_value REAL
);

-- Insert default users for testing
INSERT INTO users (username, password, email, full_name)
VALUES 
    ('admin', 'admin123', 'admin@example.com', 'Administrator'),
    ('user', 'user123', 'user@example.com', 'Regular User');

-- Insert sample data for household
INSERT INTO household (id, name, address, number_of_occupants)
VALUES ('hhld-001', 'Main Residence', '123 Main Street, Anytown', 4);

-- Insert sample electricity provider
INSERT INTO electricity (id, name, provider, account_number, rate_per_kwh, meter_reading)
VALUES ('elec-001', 'Home Electricity', 'PowerCo', 'E-12345', 0.14, 10250.5);

-- Insert sample gas provider
INSERT INTO gas (id, name, provider, account_number, rate_per_unit, meter_reading)
VALUES ('gas-001', 'Home Heating', 'GasCorp', 'G-67890', 1.25, 5680.2);

-- Insert sample water provider
INSERT INTO water (id, name, provider, account_number, rate_per_cubic_meter, meter_reading)
VALUES ('wtr-001', 'Water Supply', 'AquaServices', 'W-24680', 2.50, 1842.7);

-- Insert sample subscriptions
INSERT INTO subscription (id, name, provider, account_number, type, monthly_cost, next_billing_date)
VALUES 
    ('sub-001', 'Internet Service', 'NetProvider', 'NET-123', 'INTERNET', 59.99, '2025-05-15'),
    ('sub-002', 'Mobile Phone', 'MobileTel', 'MOB-456', 'PHONE', 39.99, '2025-05-10'),
    ('sub-003', 'Streaming Service', 'StreamFlix', 'STR-789', 'STREAMING', 12.99, '2025-04-30');

-- Insert sample electricity reading history
INSERT INTO reading_history (id, utility_id, utility_type, reading_date, reading_value)
VALUES 
    ('rh-001', 'elec-001', 'electricity', '2025-01-22', 10050.2),
    ('rh-002', 'elec-001', 'electricity', '2025-02-22', 10150.8),
    ('rh-003', 'elec-001', 'electricity', '2025-03-22', 10250.5);

-- Insert sample gas reading history
INSERT INTO reading_history (id, utility_id, utility_type, reading_date, reading_value)
VALUES 
    ('rh-004', 'gas-001', 'gas', '2025-01-22', 5500.5),
    ('rh-005', 'gas-001', 'gas', '2025-02-22', 5590.8),
    ('rh-006', 'gas-001', 'gas', '2025-03-22', 5680.2);

-- Insert sample water reading history
INSERT INTO reading_history (id, utility_id, utility_type, reading_date, reading_value)
VALUES 
    ('rh-007', 'wtr-001', 'water', '2025-01-22', 1800.2),
    ('rh-008', 'wtr-001', 'water', '2025-02-22', 1820.5),
    ('rh-009', 'wtr-001', 'water', '2025-03-22', 1842.7);

-- Insert sample bills
INSERT INTO bill (id, utility_id, amount, consumption, issue_date, due_date, is_paid, paid_date)
VALUES 
    ('bill-001', 'elec-001', 28.00, 200.0, '2025-01-25', '2025-02-15', 1, '2025-02-10'),
    ('bill-002', 'gas-001', 112.50, 90.0, '2025-01-25', '2025-02-15', 1, '2025-02-10'),
    ('bill-003', 'wtr-001', 50.75, 20.3, '2025-01-25', '2025-02-15', 1, '2025-02-10'),
    ('bill-004', 'elec-001', 35.00, 250.0, '2025-02-25', '2025-03-15', 1, '2025-03-10'),
    ('bill-005', 'gas-001', 125.00, 100.0, '2025-02-25', '2025-03-15', 1, '2025-03-10'),
    ('bill-006', 'wtr-001', 55.75, 22.3, '2025-02-25', '2025-03-15', 1, '2025-03-10'),
    ('bill-007', 'elec-001', 42.00, 300.0, '2025-03-25', '2025-04-15', 0, NULL),
    ('bill-008', 'gas-001', 118.75, 95.0, '2025-03-25', '2025-04-15', 0, NULL),
    ('bill-009', 'wtr-001', 57.50, 23.0, '2025-03-25', '2025-04-15', 0, NULL);

-- Create indexes for performance
CREATE INDEX idx_bill_utility_id ON bill(utility_id);
CREATE INDEX idx_bill_due_date ON bill(due_date);
CREATE INDEX idx_bill_is_paid ON bill(is_paid);
CREATE INDEX idx_reading_history_utility_id ON reading_history(utility_id);
CREATE INDEX idx_reading_history_date ON reading_history(reading_date);
CREATE INDEX idx_subscription_type ON subscription(type);
CREATE INDEX idx_subscription_billing_date ON subscription(next_billing_date);