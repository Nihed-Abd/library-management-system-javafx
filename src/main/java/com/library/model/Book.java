package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private int id;
    private String name;
    private int biblioId;
    private boolean isAvailable;
    private String title;
    private String author;
    private String description;
    private double price;
    private LocalDate dateCreation;
    
    // Reference to biblio object (for UI display)
    private Biblio biblio;

    public Book() {
        this.dateCreation = LocalDate.now();
        this.isAvailable = true;
    }

    public Book(int id, String name, int biblioId, boolean isAvailable, String title, 
                String author, String description, double price, LocalDate dateCreation) {
        this.id = id;
        this.name = name;
        this.biblioId = biblioId;
        this.isAvailable = isAvailable;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.dateCreation = dateCreation;
    }

    public Book(String name, int biblioId, String title, String author, String description, double price) {
        this.name = name;
        this.biblioId = biblioId;
        this.isAvailable = true;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.dateCreation = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBiblioId() {
        return biblioId;
    }

    public void setBiblioId(int biblioId) {
        this.biblioId = biblioId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public Biblio getBiblio() {
        return biblio;
    }
    
    public void setBiblio(Biblio biblio) {
        this.biblio = biblio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}
