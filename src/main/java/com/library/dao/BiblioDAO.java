package com.library.dao;

import com.library.model.Biblio;
import com.library.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BiblioDAO {
    
    public List<Biblio> getAllBiblios() {
        List<Biblio> biblios = new ArrayList<>();
        String query = "SELECT * FROM biblio ORDER BY name";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Biblio biblio = new Biblio();
                biblio.setId(rs.getInt("id"));
                biblio.setLocation(rs.getString("location"));
                biblio.setDateCreation(rs.getDate("date_creation").toLocalDate());
                biblio.setName(rs.getString("name"));
                biblio.setDescription(rs.getString("description"));
                biblios.add(biblio);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all biblios: " + e.getMessage());
        }

        return biblios;
    }

    public Biblio getBiblioById(int id) {
        String query = "SELECT * FROM biblio WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Biblio biblio = new Biblio();
                    biblio.setId(rs.getInt("id"));
                    biblio.setLocation(rs.getString("location"));
                    biblio.setDateCreation(rs.getDate("date_creation").toLocalDate());
                    biblio.setName(rs.getString("name"));
                    biblio.setDescription(rs.getString("description"));
                    return biblio;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting biblio by ID: " + e.getMessage());
        }
        
        return null;
    }

    public boolean addBiblio(Biblio biblio) {
        String query = "INSERT INTO biblio (location, date_creation, name, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, biblio.getLocation());
            pstmt.setDate(2, Date.valueOf(biblio.getDateCreation()));
            pstmt.setString(3, biblio.getName());
            pstmt.setString(4, biblio.getDescription());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        biblio.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding biblio: " + e.getMessage());
        }
        
        return false;
    }

    public boolean updateBiblio(Biblio biblio) {
        String query = "UPDATE biblio SET location = ?, date_creation = ?, name = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, biblio.getLocation());
            pstmt.setDate(2, Date.valueOf(biblio.getDateCreation()));
            pstmt.setString(3, biblio.getName());
            pstmt.setString(4, biblio.getDescription());
            pstmt.setInt(5, biblio.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating biblio: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBiblio(int id) {
        String query = "DELETE FROM biblio WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting biblio: " + e.getMessage());
            return false;
        }
    }
    
    public int getBookCountByBiblioId(int biblioId) {
        String query = "SELECT COUNT(*) FROM book WHERE biblio_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, biblioId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book count: " + e.getMessage());
        }
        
        return 0;
    }
}
