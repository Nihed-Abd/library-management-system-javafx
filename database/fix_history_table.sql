-- This script will fix the history table structure to match what the application expects

-- First, check if history table exists and back up any existing data
CREATE TABLE IF NOT EXISTS history_backup AS SELECT * FROM history;

-- Drop and recreate the history table with the correct structure
DROP TABLE IF EXISTS history;

CREATE TABLE history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    is_loan BOOLEAN NOT NULL,
    date TIMESTAMP NOT NULL,
    note TEXT,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

-- Attempt to restore data if backup exists and has records
-- Note: This assumes the original table had compatible column names
-- If column names were different, you'll need to adjust this query
INSERT INTO history (book_id, is_loan, date, note)
SELECT book_id, 
       -- If the original column was named differently (e.g., 'loan_type', 'type', etc.)
       -- replace 'is_loan' below with the actual column name
       is_loan, 
       date, 
       note 
FROM history_backup;

-- Drop the backup table when done
-- DROP TABLE history_backup;
-- (Commented out for safety - uncomment after verifying the fix works)
