package com.library.service;

import com.library.dao.BiblioDAO;
import com.library.model.Biblio;
import java.util.List;

public class BiblioService {
    private final BiblioDAO biblioDAO;
    
    public BiblioService() {
        this.biblioDAO = new BiblioDAO();
    }
    
    public List<Biblio> getAllBiblios() {
        // Return all libraries for all users, regardless of permissions
        // This allows everyone to see all libraries in the system
        return biblioDAO.getAllBiblios();
    }
    
    public List<Biblio> getBibliosByUserId(int userId) {
        return biblioDAO.getBibliosByUserId(userId);
    }
    
    public Biblio getBiblioById(int id) {
        Biblio biblio = biblioDAO.getBiblioById(id);
        
        // Check permission
        if (biblio != null && !canUserAccessBiblio(UserService.getCurrentUserId(), id)) {
            return null; // No permission
        }
        
        return biblio;
    }
    
    public boolean canUserAccessBiblio(int userId, int biblioId) {
        if (UserService.isAdmin()) {
            return true;
        }
        return biblioDAO.canUserAccessBiblio(userId, biblioId);
    }
    
    public boolean addBiblio(Biblio biblio) {
        // Set the current user as the owner if not specified
        if (biblio.getUserId() <= 0) {
            biblio.setUserId(UserService.getCurrentUserId());
        }
        
        return biblioDAO.addBiblio(biblio);
    }
    
    public boolean updateBiblio(Biblio biblio) {
        // Check permission
        if (!canUserAccessBiblio(UserService.getCurrentUserId(), biblio.getId())) {
            return false; // No permission
        }
        
        return biblioDAO.updateBiblio(biblio);
    }
    
    public boolean deleteBiblio(int id) {
        // Check permission
        if (!canUserAccessBiblio(UserService.getCurrentUserId(), id)) {
            return false; // No permission
        }
        
        return biblioDAO.deleteBiblio(id);
    }
    
    public int getBookCountByBiblioId(int biblioId) {
        return biblioDAO.getBookCountByBiblioId(biblioId);
    }
}
