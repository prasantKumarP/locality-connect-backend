-- Database initialization script for Locality Connect

-- This script will be automatically executed by Spring Boot if you place it in src/main/resources
-- and configure: spring.jpa.hibernate.ddl-auto=none
-- For now, it's here as a reference for manual setup

-- Create database (run this separately in PostgreSQL)
-- CREATE DATABASE locality_connect;

-- Sample data insertion (run after Spring Boot creates the tables)

-- Insert a sample locality
INSERT INTO localities (name, description, address, city, state, pincode, voting_threshold_percentage, voting_period_days, active, created_at, updated_at)
VALUES 
('Green Valley Apartments', 'A peaceful residential community', '123 Main Street', 'Bangalore', 'Karnataka', '560001', 50, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sunset Heights', 'Premium gated community', '456 Park Avenue', 'Bangalore', 'Karnataka', '560002', 60, 45, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert an admin user (password: admin123)
-- Password is BCrypt encoded
INSERT INTO users (username, email, password, full_name, phone_number, locality_id, role, active, created_at, updated_at)
VALUES 
('admin', 'admin@localityconnect.com', '$2a$10$XYZ...', 'Admin User', '9999999999', 1, 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note: For actual BCrypt password, use an online BCrypt generator or register through the API

-- Sample queries to verify data
-- SELECT * FROM localities;
-- SELECT * FROM users;
-- SELECT * FROM suggestions;
-- SELECT * FROM votes;
