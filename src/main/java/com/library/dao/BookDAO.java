package com.library.dao;

import com.library.model.Book;
import com.library.model.Biblio;
import com.library.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    private final BiblioDAO biblioDAO = new BiblioDAO();
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM book ORDER BY title";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }

        return books;
    }
    
    public List<Book> getBooksByBiblioId(int biblioId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM book WHERE biblio_id = ? ORDER BY title";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, biblioId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting books by biblio ID: " + e.getMessage());
        }

        return books;
    }

    public Book getBookById(int id) {
        String query = "SELECT * FROM book WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        }
        
        return null;
    }

    public boolean addBook(Book book) {
        String query = "INSERT INTO book (name, biblio_id, available, title, author, description, price, date_creation) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, book.getName());
            pstmt.setInt(2, book.getBiblioId());
            pstmt.setBoolean(3, book.isAvailable());
            pstmt.setString(4, book.getTitle());
            pstmt.setString(5, book.getAuthor());
            pstmt.setString(6, book.getDescription());
            pstmt.setDouble(7, book.getPrice());
            pstmt.setDate(8, Date.valueOf(book.getDateCreation()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateBook(Book book) {
        String query = "UPDATE book SET name = ?, biblio_id = ?, available = ?, title = ?, " +
                      "author = ?, description = ?, price = ?, date_creation = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, book.getName());
            pstmt.setInt(2, book.getBiblioId());
            pstmt.setBoolean(3, book.isAvailable());
            pstmt.setString(4, book.getTitle());
            pstmt.setString(5, book.getAuthor());
            pstmt.setString(6, book.getDescription());
            pstmt.setDouble(7, book.getPrice());
            pstmt.setDate(8, Date.valueOf(book.getDateCreation()));
            pstmt.setInt(9, book.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int id) {
        String query = "DELETE FROM book WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean loanBook(int bookId) {
        // First, update the book's available status
        String updateQuery = "UPDATE book SET available = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setInt(1, bookId);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error loaning book: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with database connection: " + e.getMessage());
            return false;
        }
    }
    
    public boolean returnBook(int bookId) {
        // First, update the book's available status
        String updateQuery = "UPDATE book SET available = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setInt(1, bookId);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error returning book: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with database connection: " + e.getMessage());
            return false;
        }
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setName(rs.getString("name"));
        book.setBiblioId(rs.getInt("biblio_id"));
        book.setAvailable(rs.getBoolean("available"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setDescription(rs.getString("description"));
        book.setPrice(rs.getDouble("price"));
        book.setDateCreation(rs.getDate("date_creation").toLocalDate());
        
        // Get the associated biblio object
        Biblio biblio = biblioDAO.getBiblioById(book.getBiblioId());
        book.setBiblio(biblio);
        
        return book;
    }
}
