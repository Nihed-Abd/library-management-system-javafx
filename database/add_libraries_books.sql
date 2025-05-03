-- Clear existing data first (if you want to start fresh)
-- Comment these out if you want to keep existing data
DELETE FROM history;
DELETE FROM book;
DELETE FROM biblio;

-- Add libraries for user ID 4 (NihedxTN)
INSERT INTO biblio (name, location, description, date_creation, user_id) VALUES 
('Science Library', 'Building A, Floor 2', 'Collection of science and technology books', CURDATE(), 4),
('Fiction Collection', 'Building B, Floor 1', 'Modern fiction and classics', CURDATE(), 4);

-- Add libraries for user ID 5
INSERT INTO biblio (name, location, description, date_creation, user_id) VALUES 
('History Archives', 'Main Building, Floor 3', 'Historical documents and books', CURDATE(), 5),
('Educational Resources', 'Learning Center', 'Textbooks and educational materials', CURDATE(), 5);

-- Add libraries for user ID 6 (toufa)
INSERT INTO biblio (name, location, description, date_creation, user_id) VALUES 
('Manga Collection', 'East Wing, Room 101', 'Japanese comics and graphic novels', CURDATE(), 6),
('Computer Science', 'Tech Center', 'Programming and computer science books', CURDATE(), 6);

-- Get the newly created biblio IDs (this is just for reference)
-- SELECT * FROM biblio;

-- Add books to the first library of user 4
SET @user4_biblio1 = (SELECT id FROM biblio WHERE user_id = 4 ORDER BY id LIMIT 1);
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('SCI001', 'Physics Fundamentals', 'Richard Feynman', 29.99, 'Introduction to physics concepts', TRUE, CURDATE(), @user4_biblio1),
('SCI002', 'Chemistry Basics', 'Marie Curie', 24.99, 'Introduction to chemistry', TRUE, CURDATE(), @user4_biblio1),
('SCI003', 'Astronomy Today', 'Neil Tyson', 34.99, 'Modern astronomical discoveries', TRUE, CURDATE(), @user4_biblio1);

-- Add books to the second library of user 4
SET @user4_biblio2 = (SELECT id FROM biblio WHERE user_id = 4 ORDER BY id DESC LIMIT 1);
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('FIC001', 'The Great Adventure', 'John Doe', 19.99, 'An epic adventure novel', TRUE, CURDATE(), @user4_biblio2),
('FIC002', 'Mystery at Midnight', 'Agatha Smith', 15.99, 'A thrilling mystery novel', TRUE, CURDATE(), @user4_biblio2);

-- Add books to the first library of user 5
SET @user5_biblio1 = (SELECT id FROM biblio WHERE user_id = 5 ORDER BY id LIMIT 1);
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('HIST001', 'World War II Chronicles', 'History Group', 39.99, 'Detailed account of WWII', TRUE, CURDATE(), @user5_biblio1),
('HIST002', 'Ancient Civilizations', 'Sarah Johnson', 42.99, 'Study of ancient civilizations', TRUE, CURDATE(), @user5_biblio1);

-- Add books to the second library of user 5
SET @user5_biblio2 = (SELECT id FROM biblio WHERE user_id = 5 ORDER BY id DESC LIMIT 1);
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('EDU001', 'Mathematics 101', 'Alan Turing', 45.99, 'Fundamental mathematics textbook', TRUE, CURDATE(), @user5_biblio2),
('EDU002', 'English Grammar', 'William Shakespeare', 22.99, 'Complete English grammar guide', TRUE, CURDATE(), @user5_biblio2);

-- Add books to the first library of user 6
SET @user6_biblio1 = (SELECT id FROM biblio WHERE user_id = 6 ORDER BY id LIMIT 1);
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('MANGA001', 'One Piece Vol.1', 'Eiichiro Oda', 12.99, 'Adventure manga about pirates', TRUE, CURDATE(), @user6_biblio1),
('MANGA002', 'Naruto Vol.1', 'Masashi Kishimoto', 12.99, 'Ninja adventure manga', TRUE, CURDATE(), @user6_biblio1);

-- Add books to the second library of user 6
SET @user6_biblio2 = (SELECT id FROM biblio WHERE user_id = 6 ORDER BY id DESC LIMIT 1);
INSERT INTO book (name, title, author, price, description, available, date_creation, biblio_id) VALUES 
('CS001', 'Java Programming', 'James Gosling', 49.99, 'Complete Java programming guide', TRUE, CURDATE(), @user6_biblio2),
('CS002', 'Python Basics', 'Guido van Rossum', 39.99, 'Introduction to Python', TRUE, CURDATE(), @user6_biblio2),
('CS003', 'Database Design', 'Oracle Team', 54.99, 'Database architecture and SQL', TRUE, CURDATE(), @user6_biblio2);
