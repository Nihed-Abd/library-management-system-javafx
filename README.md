# Library Management System

A comprehensive JavaFX desktop application for managing libraries, books, and loan/return history with a modern UI and role-based access control.

![Library Management System Screenshot](placeholder-for-screenshot.png)

## Features

### Authentication & User Management
- Secure login with SHA-256 password hashing
- User registration with email validation
- Role-based permissions (Admin/Regular users)
- User-specific data access control

### Library Management (Biblio)
- **Create:** Add new libraries with name, location, and description
- **Read:** View all libraries with search/filter capabilities
- **Update:** Edit library details with permission control
- **Delete:** Remove libraries (with confirmation and checks for existing books)
- **Advanced Features:**
  - Pagination for handling large datasets
  - Dynamic search with instant filtering
  - Color-coded UI for better user experience

### Book Management
- **Create:** Add books to libraries with complete metadata
- **Read:** View books by library or all books with powerful filtering options
- **Update:** Modify book details with permission checks
- **Delete:** Remove books from libraries (with proper validation)
- **Special Features:**
  - Book status tracking (Available/Loaned)
  - Color-coded status indicators
  - Category-based filtering

### Loan/Return System
- Loan books with notes and automatic timestamp
- Return books with condition notes
- Complete history tracking for accountability
- **Advanced Features:**
  - Color-coded loan/return status
  - Date-based filtering
  - User-specific history views

### History Tracking
- **Create:** Automatically generated when loans/returns happen
- **Read:** View complete history with multiple filter options
- **Filter Capabilities:**
  - By book, library, date range, and loan/return type
  - Pagination for large history datasets
- **Special Features:**
  - Color-coded transaction types (loans in red, returns in green)
  - Detailed transaction information

## Technical Features

### Architecture
- **MVC Pattern:** Clear separation of Model, View, and Controller
- **DAO Layer:** Database access abstraction
- **Service Layer:** Business logic encapsulation
- **Controller Layer:** UI interaction handling
- **FXML Views:** Declarative UI layout

### UI/UX Features
- Modern, clean user interface
- Responsive layouts that adapt to window size
- Subtle animations for better user experience
- Interactive tables with sorting and filtering
- Dialog-based interactions for CRUD operations
- Form validation with meaningful error messages

### Database Integration
- MySQL database connection via XAMPP
- Transaction management for data integrity
- Prepared statements to prevent SQL injection
- Efficient querying with proper indexing

## Database Setup

1. Start XAMPP and ensure MySQL server is running
2. Open phpMyAdmin (http://localhost/phpmyadmin/)
3. Create a new database named `library_management`
4. Import the SQL script from `database/library_management.sql`

Alternatively, you can run the following SQL scripts in this order:
1. `database/library_management.sql` - Main database schema
2. `database/add_libraries_books.sql` - Sample data (optional)

## Requirements

- Java 11 or higher
- JavaFX 11 or higher
- MySQL Database (via XAMPP)
- Maven for dependency management

## Project Structure

```
library-management-system/
├── src/main/java/com/library/
│   ├── app/          # Application entry point
│   ├── controller/   # JavaFX controllers
│   ├── dao/          # Data Access Objects
│   ├── model/        # Data models
│   ├── service/      # Business logic
│   └── util/         # Utilities
├── src/main/resources/com/library/
│   ├── css/          # Stylesheets
│   ├── images/       # Icons and images
│   └── view/         # FXML files
└── database/         # SQL scripts
```

## How to Run

### Method 1: Using Maven
```bash
mvn clean javafx:run
```

### Method 2: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Ensure Maven dependencies are resolved
3. Run the `LibraryApp.java` as the main class

## Default Login Credentials

- **Admin User:**
  - Username: admin
  - Password: admin123

- **Regular User:**
  - Username: john
  - Password: user123

## License

[MIT License](LICENSE)
