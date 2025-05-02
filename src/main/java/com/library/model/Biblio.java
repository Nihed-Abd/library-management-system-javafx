package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Biblio {
    private int id;
    private String location;
    private LocalDate dateCreation;
    private String name;
    private String description;
    private int userId;
    
    // Reference to user object (for UI display)
    private User user;

    public Biblio() {
        this.dateCreation = LocalDate.now();
    }

    public Biblio(int id, String location, LocalDate dateCreation, String name, String description, int userId) {
        this.id = id;
        this.location = location;
        this.dateCreation = dateCreation;
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    public Biblio(String location, String name, String description, int userId) {
        this.location = location;
        this.dateCreation = LocalDate.now();
        this.name = name;
        this.description = description;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Biblio biblio = (Biblio) o;
        return id == biblio.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
