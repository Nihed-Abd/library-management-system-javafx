-- Add sample books to make the system functional
-- These will be added to your existing libraries (biblio_id values 6, 7, and 8)

-- Add books to "pertit ariana" library (biblio_id = 6)
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('BOOK001', 'The Art of Programming', 'John Smith', 29.99, 'A comprehensive guide to programming', TRUE, CURDATE(), 6),
('BOOK002', 'Data Structures and Algorithms', 'Alice Johnson', 34.99, 'Understanding computer science fundamentals', TRUE, CURDATE(), 6),
('BOOK003', 'Web Development Basics', 'David Brown', 24.99, 'Learn HTML, CSS and JavaScript', TRUE, CURDATE(), 6);

-- Add books to "knjbhjb" library (biblio_id = 7)
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('BOOK004', 'Fiction Adventure', 'Michael Wilson', 19.99, 'An exciting adventure novel', TRUE, CURDATE(), 7),
('BOOK005', 'Mystery in Paris', 'Sophie Martin', 22.99, 'A thrilling mystery story', TRUE, CURDATE(), 7);

-- Add books to "qzdqzd" library (biblio_id = 8)
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('BOOK006', 'History of Science', 'Robert Miller', 39.99, 'The evolution of scientific thought', TRUE, CURDATE(), 8),
('BOOK007', 'Philosophy Basics', 'Emma White', 27.99, 'Introduction to philosophical concepts', TRUE, CURDATE(), 8);
