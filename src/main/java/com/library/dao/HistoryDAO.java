package com.library.dao;

import com.library.model.Biblio;
import com.library.model.Book;
import com.library.model.History;
import com.library.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    
    private final BookDAO bookDAO;
    
    public HistoryDAO() {
        this.bookDAO = new BookDAO();
    }
    
    public List<History> getAllHistory() {
        String sql = "SELECT id, book_id, is_loan, date, note FROM history ORDER BY date DESC";
        List<History> historyList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                History history = mapResultSetToHistory(rs);
                loadBookForHistory(history);
                historyList.add(history);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting all history: " + e.getMessage());
        }
        
        return historyList;
    }
    
    public List<History> getHistoryByBookId(int bookId) {
        String sql = "SELECT id, book_id, is_loan, date, note FROM history WHERE book_id = ? ORDER BY date DESC";
        List<History> historyList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    History history = mapResultSetToHistory(rs);
                    loadBookForHistory(history);
                    historyList.add(history);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting history by book ID: " + e.getMessage());
        }
        
        return historyList;
    }
    
    public List<History> getHistoryByBiblioId(int biblioId) {
        String sql = "SELECT h.id, h.book_id, h.is_loan, h.date, h.note FROM history h " +
                     "JOIN book b ON h.book_id = b.id " +
                     "WHERE b.biblio_id = ? " +
                     "ORDER BY h.date DESC";
        List<History> historyList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, biblioId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    History history = mapResultSetToHistory(rs);
                    loadBookForHistory(history);
                    historyList.add(history);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting history by biblio ID: " + e.getMessage());
        }
        
        return historyList;
    }
    
    public History getHistoryById(int id) {
        String sql = "SELECT id, book_id, is_loan, date, note FROM history WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    History history = mapResultSetToHistory(rs);
                    loadBookForHistory(history);
                    return history;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error getting history by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addHistory(History history) {
        String sql = "INSERT INTO history (book_id, is_loan, date, note) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, history.getBookId());
            pstmt.setBoolean(2, history.isLoan());
            pstmt.setTimestamp(3, Timestamp.valueOf(history.getDate()));
            pstmt.setString(4, history.getNote());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        history.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding history: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean updateHistory(History history) {
        String sql = "UPDATE history SET book_id = ?, is_loan = ?, date = ?, note = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, history.getBookId());
            pstmt.setBoolean(2, history.isLoan());
            pstmt.setTimestamp(3, Timestamp.valueOf(history.getDate()));
            pstmt.setString(4, history.getNote());
            pstmt.setInt(5, history.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating history: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean deleteHistory(int id) {
        String sql = "DELETE FROM history WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting history: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean recordLoan(int bookId, String note) {
        History history = new History();
        history.setBookId(bookId);
        history.setLoan(true);
        history.setDate(LocalDateTime.now());
        history.setNote(note);
        
        return addHistory(history);
    }
    
    public boolean recordReturn(int bookId, String note) {
        History history = new History();
        history.setBookId(bookId);
        history.setLoan(false);
        history.setDate(LocalDateTime.now());
        history.setNote(note);
        
        return addHistory(history);
    }
    
    private History mapResultSetToHistory(ResultSet rs) throws SQLException {
        History history = new History();
        history.setId(rs.getInt("id"));
        history.setBookId(rs.getInt("book_id"));
        history.setLoan(rs.getBoolean("is_loan"));
        history.setDate(rs.getTimestamp("date").toLocalDateTime());
        history.setNote(rs.getString("note"));
        return history;
    }
    
    private void loadBookForHistory(History history) {
        Book book = bookDAO.getBookById(history.getBookId());
        history.setBook(book);
        if (book != null) {
            history.setBiblio(book.getBiblio());
        }
    }
}
