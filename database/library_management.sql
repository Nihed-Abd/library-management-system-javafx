-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS library_management;

-- Use the database
USE library_management;

-- Create User table
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    date_created DATE NOT NULL
);

-- Create Biblio table
CREATE TABLE IF NOT EXISTS biblio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    date_creation DATE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Create Book table
CREATE TABLE IF NOT EXISTS book (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    biblio_id INT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    date_creation DATE NOT NULL,
    FOREIGN KEY (biblio_id) REFERENCES biblio(id) ON DELETE CASCADE
);

-- Create History table
CREATE TABLE IF NOT EXISTS history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status ENUM('LOAN', 'RETURN') NOT NULL,
    book_id INT NOT NULL,
    date_time DATETIME NOT NULL,
    note TEXT,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

-- Insert sample data (optional)
INSERT INTO user (username, password, email, full_name, is_admin, date_created) VALUES
('admin', 'admin123', 'admin@library.com', 'System Administrator', TRUE, '2025-01-01'),
('john', 'john123', 'john@example.com', 'John Doe', FALSE, '2025-01-15'),
('mary', 'mary123', 'mary@example.com', 'Mary Smith', FALSE, '2025-02-10');

INSERT INTO biblio (location, date_creation, name, description, user_id) VALUES
('Main Floor', '2025-01-15', 'Science Library', 'Collection of scientific books and journals', 1),
('Second Floor', '2025-02-20', 'Fiction Library', 'Collection of fiction and literature books', 1),
('Basement', '2025-03-10', 'Reference Library', 'Collection of reference materials and encyclopedias', 1),
('Home Office', '2025-03-15', 'John\'s Personal Library', 'My personal collection of tech books', 2),
('Living Room', '2025-03-20', 'Mary\'s Reading Collection', 'Fiction and self-improvement books', 3);

INSERT INTO book (name, biblio_id, is_available, title, author, description, price, date_creation) VALUES
('SCI-001', 1, TRUE, 'Introduction to Physics', 'Albert Einstein', 'Basic physics textbook', 29.99, '2025-01-20'),
('SCI-002', 1, TRUE, 'Organic Chemistry', 'Marie Curie', 'Advanced chemistry book', 39.99, '2025-01-25'),
('FIC-001', 2, TRUE, 'The Great Novel', 'Jane Austen', 'Classic literature book', 19.99, '2025-02-25'),
('FIC-002', 2, FALSE, 'Modern Poetry', 'Robert Frost', 'Collection of modern poems', 15.99, '2025-02-27'),
('REF-001', 3, TRUE, 'Encyclopedia of Knowledge', 'Various Authors', 'Comprehensive reference book', 59.99, '2025-03-15'),
('TECH-001', 4, TRUE, 'Java Programming', 'James Gosling', 'Complete guide to Java', 45.99, '2025-03-16'),
('TECH-002', 4, TRUE, 'Python for Beginners', 'Guido van Rossum', 'Learn Python programming', 35.99, '2025-03-17'),
('FIC-003', 5, TRUE, 'Pride and Prejudice', 'Jane Austen', 'Classic romance novel', 12.99, '2025-03-21');

INSERT INTO history (status, book_id, date_time, note) VALUES
('LOAN', 4, '2025-04-15 10:30:00', 'Borrowed by Student #1234'),
('RETURN', 4, '2025-04-20 14:45:00', 'Returned with minor damage');
