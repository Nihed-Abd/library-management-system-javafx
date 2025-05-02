# Library Management System

A JavaFX application for managing libraries, books, and loan/return history.

## Requirements

- Java 11 or higher
- JavaFX 11 or higher
- MySQL Database (via XAMPP)

## Database Setup

1. Start XAMPP and ensure MySQL server is running
2. Open phpMyAdmin (http://localhost/phpmyadmin/)
3. Create a new database named `library_management`
4. Import the SQL script from `database/library_management.sql`

## Project Structure

The project follows a standard MVC architecture:

- **Model**: Contains the data classes (Biblio, Book, History)
- **DAO**: Data Access Objects for database operations
- **Service**: Business logic layer
- **Controller**: JavaFX controllers for UI interaction
- **View**: FXML files defining the UI
- **Util**: Utility classes for database connection and dialogs

## Features

### Library Management
- View all libraries
- Add, edit, and delete libraries
- View book count for each library

### Book Management
- View all books or books by library
- Add, edit, and delete books
- Loan and return books
- Track book availability

### History Tracking
- Track loan and return history
- Filter history by library or book
- View detailed history information

## How to Run

1. Ensure MySQL is running via XAMPP
2. Build and run the application using `LibraryApp.java` as the main class
3. The application will automatically create necessary database tables if they don't exist

## Dependencies (To Be Added)

Add these JAR files to your project's lib folder:
- MySQL Connector/J (for database connection)
- JavaFX SDK (if not included in your JDK)

## Icons

The application uses icons in the `/src/main/resources/com/library/images/` directory. You'll need to replace the placeholder icon files with actual icons.
