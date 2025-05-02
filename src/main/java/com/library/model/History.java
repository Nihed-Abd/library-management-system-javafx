package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class History {
    private int id;
    private HistoryStatus status;
    private int bookId;
    private LocalDateTime dateTime;
    private String note;
    
    // References to related objects (for UI display)
    private Book book;
    private Biblio biblio;

    public enum HistoryStatus {
        LOAN("Loan"),
        RETURN("Return");
        
        private final String displayName;
        
        HistoryStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }

    public History() {
        this.dateTime = LocalDateTime.now();
    }

    public History(int id, HistoryStatus status, int bookId, LocalDateTime dateTime, String note) {
        this.id = id;
        this.status = status;
        this.bookId = bookId;
        this.dateTime = dateTime;
        this.note = note;
    }

    public History(HistoryStatus status, int bookId, String note) {
        this.status = status;
        this.bookId = bookId;
        this.dateTime = LocalDateTime.now();
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HistoryStatus getStatus() {
        return status;
    }

    public void setStatus(HistoryStatus status) {
        this.status = status;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
