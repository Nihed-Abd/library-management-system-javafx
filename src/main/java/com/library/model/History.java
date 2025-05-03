package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class History {
    private int id;
    private boolean isLoan; 
    private int bookId;
    private LocalDateTime date; 
    private String note;
    
    // References to related objects (for UI display)
    private Book book;
    private Biblio biblio;

    public History() {
        this.date = LocalDateTime.now();
    }

    public History(int id, boolean isLoan, int bookId, LocalDateTime date, String note) {
        this.id = id;
        this.isLoan = isLoan;
        this.bookId = bookId;
        this.date = date;
        this.note = note;
    }

    public History(boolean isLoan, int bookId, String note) {
        this.isLoan = isLoan;
        this.bookId = bookId;
        this.date = LocalDateTime.now();
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isLoan() {
        return isLoan;
    }

    public void setLoan(boolean loan) {
        isLoan = loan;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    // Keep these for backward compatibility
    public LocalDateTime getDateTime() {
        return getDate();
    }

    public void setDateTime(LocalDateTime dateTime) {
        setDate(dateTime);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
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
        History history = (History) o;
        return id == history.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
