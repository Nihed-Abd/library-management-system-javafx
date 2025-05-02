package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/library_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Get database connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // Close connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    // Initialize database (create tables if they don't exist)
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            var statement = conn.createStatement();
            
            // Create Biblio table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS biblio (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "location VARCHAR(255) NOT NULL, " +
                "date_creation DATE NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT" +
                ")"
            );
            
            // Create Book table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS book (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "biblio_id INT NOT NULL, " +
                "is_available BOOLEAN DEFAULT TRUE, " +
                "title VARCHAR(255) NOT NULL, " +
                "author VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "price DOUBLE NOT NULL, " +
                "date_creation DATE NOT NULL, " +
                "FOREIGN KEY (biblio_id) REFERENCES biblio(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Create History table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS history (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "status ENUM('LOAN', 'RETURN') NOT NULL, " +
                "book_id INT NOT NULL, " +
                "date_time DATETIME NOT NULL, " +
                "note TEXT, " +
                "FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE" +
                ")"
            );
            
            System.out.println("Database initialized successfully.");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
