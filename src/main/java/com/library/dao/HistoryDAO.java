package com.library.dao;

import com.library.model.History;
import com.library.model.Book;
import com.library.model.Biblio;
import com.library.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {
    
    private final BookDAO bookDAO = new BookDAO();
    private final BiblioDAO biblioDAO = new BiblioDAO();
    
    public List<History> getAllHistory() {
        List<History> historyList = new ArrayList<>();
        String query = "SELECT h.*, b.biblio_id FROM history h JOIN book b ON h.book_id = b.id ORDER BY h.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                History history = mapResultSetToHistory(rs);
                historyList.add(history);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all history: " + e.getMessage());
        }

        return historyList;
    }
    
    public List<History> getHistoryByBookId(int bookId) {
        List<History> historyList = new ArrayList<>();
        String query = "SELECT h.*, b.biblio_id FROM history h JOIN book b ON h.book_id = b.id WHERE h.book_id = ? ORDER BY h.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    History history = mapResultSetToHistory(rs);
                    historyList.add(history);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting history by book ID: " + e.getMessage());
        }

        return historyList;
    }
    
    public List<History> getHistoryByBiblioId(int biblioId) {
        List<History> historyList = new ArrayList<>();
        String query = "SELECT h.*, b.biblio_id FROM history h JOIN book b ON h.book_id = b.id WHERE b.biblio_id = ? ORDER BY h.date_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, biblioId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    History history = mapResultSetToHistory(rs);
                    historyList.add(history);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting history by biblio ID: " + e.getMessage());
        }

        return historyList;
    }

    public History getHistoryById(int id) {
        String query = "SELECT h.*, b.biblio_id FROM history h JOIN book b ON h.book_id = b.id WHERE h.id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHistory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting history by ID: " + e.getMessage());
        }
        
        return null;
    }

    public boolean addHistory(History history) {
        String query = "INSERT INTO history (status, book_id, date_time, note) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, history.getStatus().name());
            pstmt.setInt(2, history.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(history.getDateTime()));
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
            System.err.println("Error adding history: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateHistory(History history) {
        String query = "UPDATE history SET status = ?, book_id = ?, date_time = ?, note = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, history.getStatus().name());
            pstmt.setInt(2, history.getBookId());
            pstmt.setTimestamp(3, Timestamp.valueOf(history.getDateTime()));
            pstmt.setString(4, history.getNote());
            pstmt.setInt(5, history.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating history: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteHistory(int id) {
        String query = "DELETE FROM history WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting history: " + e.getMessage());
            return false;
        }
    }
    
    public boolean recordLoan(int bookId, String note) {
        // Create a new history record for a loan
        History history = new History();
        history.setStatus(History.HistoryStatus.LOAN);
        history.setBookId(bookId);
        history.setDateTime(LocalDateTime.now());
        history.setNote(note);
        
        return addHistory(history);
    }
    
    public boolean recordReturn(int bookId, String note) {
        // Create a new history record for a return
        History history = new History();
        history.setStatus(History.HistoryStatus.RETURN);
        history.setBookId(bookId);
        history.setDateTime(LocalDateTime.now());
        history.setNote(note);
        
        return addHistory(history);
    }
    
    private History mapResultSetToHistory(ResultSet rs) throws SQLException {
        History history = new History();
        history.setId(rs.getInt("id"));
        history.setStatus(History.HistoryStatus.valueOf(rs.getString("status")));
        history.setBookId(rs.getInt("book_id"));
        history.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        history.setNote(rs.getString("note"));
        
        // Get the associated book object
        Book book = bookDAO.getBookById(history.getBookId());
        history.setBook(book);
        
        // Get the associated biblio object
        int biblioId = rs.getInt("biblio_id");
        Biblio biblio = biblioDAO.getBiblioById(biblioId);
        history.setBiblio(biblio);
        
        return history;
    }
}
