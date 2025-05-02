package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.HistoryDAO;
import com.library.model.Book;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO;
    private final HistoryDAO historyDAO;
    
    public BookService() {
        this.bookDAO = new BookDAO();
        this.historyDAO = new HistoryDAO();
    }
    
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
    
    public List<Book> getBooksByBiblioId(int biblioId) {
        return bookDAO.getBooksByBiblioId(biblioId);
    }
    
    public Book getBookById(int id) {
        return bookDAO.getBookById(id);
    }
    
    public boolean addBook(Book book) {
        return bookDAO.addBook(book);
    }
    
    public boolean updateBook(Book book) {
        return bookDAO.updateBook(book);
    }
    
    public boolean deleteBook(int id) {
        return bookDAO.deleteBook(id);
    }
    
    public boolean loanBook(int bookId, String note) {
        // First update the book status
        boolean bookUpdated = bookDAO.loanBook(bookId);
        
        // Then record the loan in history
        if (bookUpdated) {
            return historyDAO.recordLoan(bookId, note);
        }
        return false;
    }
    
    public boolean returnBook(int bookId, String note) {
        // First update the book status
        boolean bookUpdated = bookDAO.returnBook(bookId);
        
        // Then record the return in history
        if (bookUpdated) {
            return historyDAO.recordReturn(bookId, note);
        }
        return false;
    }
}
