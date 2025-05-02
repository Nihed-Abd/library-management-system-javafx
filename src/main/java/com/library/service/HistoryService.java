package com.library.service;

import com.library.dao.HistoryDAO;
import com.library.model.History;
import java.util.List;

public class HistoryService {
    private final HistoryDAO historyDAO;
    
    public HistoryService() {
        this.historyDAO = new HistoryDAO();
    }
    
    public List<History> getAllHistory() {
        return historyDAO.getAllHistory();
    }
    
    public List<History> getHistoryByBookId(int bookId) {
        return historyDAO.getHistoryByBookId(bookId);
    }
    
    public List<History> getHistoryByBiblioId(int biblioId) {
        return historyDAO.getHistoryByBiblioId(biblioId);
    }
    
    public History getHistoryById(int id) {
        return historyDAO.getHistoryById(id);
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
