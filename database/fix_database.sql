-- Check if the history table exists and re-create it with the correct schema
DROP TABLE IF EXISTS history;

-- Create history table with the correct column names
CREATE TABLE history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    is_loan BOOLEAN NOT NULL,
    date TIMESTAMP NOT NULL,
    note TEXT,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

-- Insert some sample history records
INSERT INTO history (book_id, is_loan, date, note) VALUES 
(4, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY), 'Borrowed for research project'),
(4, FALSE, NOW(), 'Returned with minor damage on page 42'),
(2, TRUE, DATE_SUB(NOW(), INTERVAL 10 DAY), 'Borrowed for summer reading'),
(2, FALSE, DATE_SUB(NOW(), INTERVAL 3 DAY), 'Returned in good condition'),
(5, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY), 'Borrowed for weekend reading');
