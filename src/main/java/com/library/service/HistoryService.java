package com.library.service;

import com.library.dao.HistoryDAO;
import com.library.model.History;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryService {
    
    private final HistoryDAO historyDAO;
    private final BookService bookService;
    
    public HistoryService() {
        this.historyDAO = new HistoryDAO();
        this.bookService = new BookService();
    }
    
    public List<History> getAllHistory() {
        // If user is admin, return all history
        if (UserService.isAdmin()) {
            return historyDAO.getAllHistory();
        } else {
            // Otherwise, return only history for books owned by the current user
            return getHistoryByUserId(UserService.getCurrentUserId());
        }
    }
    
    public List<History> getHistoryByBookId(int bookId) {
        // Check if user has access to this book's history
        if (bookService.getBookById(bookId) == null) {
            return new ArrayList<>(); // No permission or book doesn't exist
        }
        
        return historyDAO.getHistoryByBookId(bookId);
    }
    
    public List<History> getHistoryByBiblioId(int biblioId) {
        return historyDAO.getHistoryByBiblioId(biblioId);
    }
    
    public List<History> getHistoryByUserId(int userId) {
        // Get all history
        List<History> allHistory = historyDAO.getAllHistory();
        
        // Filter history by user ID (compare against book's biblio owner)
        return allHistory.stream()
            .filter(history -> history.getBook() != null && 
                    history.getBook().getBiblio() != null && 
                    history.getBook().getBiblio().getUserId() == userId)
            .collect(Collectors.toList());
    }
    
    public History getHistoryById(int id) {
        History history = historyDAO.getHistoryById(id);
        
        // Check if user has access to this history
        if (history != null && history.getBook() != null && 
            history.getBook().getBiblio() != null) {
            
            int bookOwnerId = history.getBook().getBiblio().getUserId();
            if (!UserService.isAdmin() && bookOwnerId != UserService.getCurrentUserId()) {
                return null; // No permission
            }
        }
        
        return history;
    }
    
    public boolean addHistory(History history) {
        return historyDAO.addHistory(history);
    }
    
    public boolean updateHistory(History history) {
        return historyDAO.updateHistory(history);
    }
    
    public boolean deleteHistory(int id) {
        return historyDAO.deleteHistory(id);
    }
}
