-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS library_management;

-- Use the database
USE library_management;

-- Drop tables if they exist to avoid conflicts
DROP TABLE IF EXISTS history;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS biblio;
DROP TABLE IF EXISTS user;

-- Create user table
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- Increased length to accommodate hash
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    date_created DATE NOT NULL
);

-- Create biblio table with foreign key to user
CREATE TABLE biblio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    description TEXT,
    date_creation DATE NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Create book table
CREATE TABLE book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100),
    price DOUBLE,
    description TEXT,
    available BOOLEAN DEFAULT TRUE,
    date_creation DATE NOT NULL,
    biblio_id INT NOT NULL,
    FOREIGN KEY (biblio_id) REFERENCES biblio(id) ON DELETE CASCADE
);

-- Create history table
CREATE TABLE history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    is_loan BOOLEAN NOT NULL,
    date TIMESTAMP NOT NULL,
    note TEXT,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

-- Insert default users (with SHA-256 hashed passwords)
-- Password "admin123" hashed: "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"
-- Password "user123" hashed: "6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090"
INSERT INTO user (username, password, email, full_name, is_admin, date_created) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@library.com', 'Administrator', TRUE, CURDATE()),
('john', '6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090', 'john@example.com', 'John Smith', FALSE, CURDATE()),
('mary', '6ca13d52ca70c883e0f0bb101e425a89e8624de51db2d2392593af6a84118090', 'mary@example.com', 'Mary Johnson', FALSE, CURDATE());

-- Insert sample data for biblios with user ownership
INSERT INTO biblio (name, location, description, date_creation, user_id) VALUES 
('Main Library', 'Floor 1, Building A', 'The main library containing all genres', CURDATE(), 1),
('Science Library', 'Floor 2, Building B', 'Library dedicated to science books', CURDATE(), 2),
('Fiction Collection', 'Floor 1, Building C', 'Collection of fiction books', CURDATE(), 3),
('History Archives', 'Floor 3, Building A', 'Historical books and documents', CURDATE(), 2);

-- Insert sample data for books
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('SCI001', 'Physics Fundamentals', 'Richard Feynman', 29.99, 'Introduction to physics concepts', TRUE, CURDATE(), 2),
('FIC001', 'The Great Adventure', 'John Doe', 19.99, 'An epic adventure novel', TRUE, CURDATE(), 3),
('HIST001', 'World War II Chronicles', 'History Group', 39.99, 'Detailed account of WWII', TRUE, CURDATE(), 4),
('SCI002', 'Chemistry Basics', 'Marie Curie', 24.99, 'Introduction to chemistry', FALSE, CURDATE(), 2),
('FIC002', 'Mystery at Midnight', 'Agatha Smith', 15.99, 'A thrilling mystery novel', TRUE, CURDATE(), 3),
('MAIN001', 'Library Guidebook', 'Admin Team', 9.99, 'Guide to using the library', TRUE, CURDATE(), 1);

-- Insert sample data for history
INSERT INTO history (book_id, is_loan, date, note) VALUES 
(4, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Borrowed for research project'),
(4, FALSE, NOW(), 'Returned with minor damage on page 42'),
(2, TRUE, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Borrowed for summer reading'),
(2, FALSE, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Returned in good condition'),
(5, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY), 'Borrowed for weekend reading');

-- Add additional indexes for performance
CREATE INDEX idx_book_biblio ON book(biblio_id);
CREATE INDEX idx_history_book ON history(book_id);
CREATE INDEX idx_biblio_user ON biblio(user_id);
