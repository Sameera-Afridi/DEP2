CREATE TABLE customers (customer_id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100), email VARCHAR(100), phone VARCHAR(15));
CREATE TABLE flights (flight_id INT PRIMARY KEY AUTO_INCREMENT, flight_number VARCHAR(50), destination VARCHAR(100), departure_time DATETIME);
