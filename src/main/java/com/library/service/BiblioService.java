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
        return biblioDAO.getAllBiblios();
    }
    
    public Biblio getBiblioById(int id) {
        return biblioDAO.getBiblioById(id);
    }
    
    public boolean addBiblio(Biblio biblio) {
        return biblioDAO.addBiblio(biblio);
    }
    
    public boolean updateBiblio(Biblio biblio) {
        return biblioDAO.updateBiblio(biblio);
    }
    
    public boolean deleteBiblio(int id) {
        return biblioDAO.deleteBiblio(id);
    }
    
    public int getBookCountByBiblioId(int biblioId) {
        return biblioDAO.getBookCountByBiblioId(biblioId);
    }
}
