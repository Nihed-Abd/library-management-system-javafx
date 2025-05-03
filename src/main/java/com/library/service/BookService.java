package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.HistoryDAO;
import com.library.model.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final BookDAO bookDAO;
    private final HistoryDAO historyDAO;
    private final BiblioService biblioService;
    
    public BookService() {
        this.bookDAO = new BookDAO();
        this.historyDAO = new HistoryDAO();
        this.biblioService = new BiblioService();
    }
    
    public List<Book> getAllBooks() {
        // Return all books for all users regardless of permissions
        // This allows everyone to see all books in the system
        return bookDAO.getAllBooks();
    }
    
    public List<Book> getBooksByBiblioId(int biblioId) {
        // Check if user has access to this biblio
        if (!biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), biblioId)) {
            return new ArrayList<>();
        }
        return bookDAO.getBooksByBiblioId(biblioId);
    }
    
    public List<Book> getBooksByUserId(int userId) {
        // Get all books
        List<Book> allBooks = bookDAO.getAllBooks();
        
        // Filter books by user ID (compare against biblio owner)
        return allBooks.stream()
            .filter(book -> book.getBiblio() != null && book.getBiblio().getUserId() == userId)
            .collect(Collectors.toList());
    }
    
    public Book getBookById(int id) {
        Book book = bookDAO.getBookById(id);
        
        // Check permission
        if (book != null && book.getBiblio() != null &&
            !biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), book.getBiblio().getId())) {
            return null; // No permission
        }
        
        return book;
    }
    
    public boolean addBook(Book book) {
        // Check if user has access to the biblio
        if (!biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), book.getBiblioId())) {
            return false; // No permission
        }
        
        return bookDAO.addBook(book);
    }
    
    public boolean updateBook(Book book) {
        // Check permission
        if (book.getBiblio() != null && 
            !biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), book.getBiblio().getId())) {
            return false; // No permission
        }
        
        return bookDAO.updateBook(book);
    }
    
    public boolean deleteBook(int id) {
        Book book = bookDAO.getBookById(id);
        
        // Check permission
        if (book != null && book.getBiblio() != null && 
            !biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), book.getBiblio().getId())) {
            return false; // No permission
        }
        
        return bookDAO.deleteBook(id);
    }
    
    public boolean loanBook(int bookId, String note) {
        Book book = bookDAO.getBookById(bookId);
        
        // No need to check permission for loaning - anyone can loan a book
        // First update the book status
        boolean bookUpdated = bookDAO.loanBook(bookId);
        
        // Then record the loan in history
        if (bookUpdated) {
            return historyDAO.recordLoan(bookId, note);
        }
        return false;
    }
    
    public boolean returnBook(int bookId, String note) {
        Book book = bookDAO.getBookById(bookId);
        
        // No need to check permission for returning - anyone can return a book
        // First update the book status
        boolean bookUpdated = bookDAO.returnBook(bookId);
        
        // Then record the return in history
        if (bookUpdated) {
            return historyDAO.recordReturn(bookId, note);
        }
        return false;
    }
}
