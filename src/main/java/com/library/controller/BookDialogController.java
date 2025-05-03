package com.library.controller;

import com.library.model.Biblio;
import com.library.model.Book;
import com.library.service.BiblioService;
import com.library.service.BookService;
import com.library.service.UserService;
import com.library.util.DialogUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class BookDialogController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameField;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField authorField;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private ComboBox<Biblio> biblioComboBox;
    
    @FXML
    private Label errorLabel;

    private Stage dialogStage;
    private Book book;
    private boolean isAddMode;
    private BookController bookController;
    private final BookService bookService = new BookService();
    private final BiblioService biblioService = new BiblioService();
    private Biblio preSelectedBiblio;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLabel.setVisible(false);
        loadBiblioComboBox();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void setAddMode(boolean addMode) {
        this.isAddMode = addMode;
        titleLabel.setText(addMode ? "Add New Book" : "Edit Book");
    }

    public void setBook(Book book) {
        this.book = book;
        nameField.setText(book.getName());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        priceField.setText(String.valueOf(book.getPrice()));
        
        // Select the biblio in the combo box
        if (book.getBiblio() != null) {
            for (Biblio biblio : biblioComboBox.getItems()) {
                if (biblio.getId() == book.getBiblioId()) {
                    biblioComboBox.setValue(biblio);
                    break;
                }
            }
        }
    }

    public void setPreSelectedBiblio(Biblio biblio) {
        this.preSelectedBiblio = biblio;
        
        // If the combo box is already loaded, select the biblio
        if (biblioComboBox.getItems() != null && !biblioComboBox.getItems().isEmpty()) {
            for (Biblio b : biblioComboBox.getItems()) {
                if (b.getId() == biblio.getId()) {
                    biblioComboBox.setValue(b);
                    break;
                }
            }
        }
    }

    private void loadBiblioComboBox() {
        List<Biblio> biblios;
        
        // If user is admin, load all biblios, otherwise load only user's biblios
        if (UserService.isAdmin()) {
            biblios = biblioService.getAllBiblios();
        } else {
            biblios = biblioService.getBibliosByUserId(UserService.getCurrentUserId());
        }
        
        ObservableList<Biblio> biblioObservableList = FXCollections.observableArrayList(biblios);
        biblioComboBox.setItems(biblioObservableList);
        
        // If a biblio was pre-selected, select it in the combo box
        if (preSelectedBiblio != null) {
            for (Biblio biblio : biblioComboBox.getItems()) {
                if (biblio.getId() == preSelectedBiblio.getId()) {
                    biblioComboBox.setValue(biblio);
                    break;
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        if (validateInputs()) {
            String name = nameField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            Biblio selectedBiblio = biblioComboBox.getValue();
            
            if (isAddMode) {
                Book newBook = new Book();
                newBook.setName(name);
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setPrice(price);
                newBook.setBiblioId(selectedBiblio.getId());
                newBook.setAvailable(true);
                newBook.setDateCreation(LocalDate.now());
                
                // Check if user can add book to this biblio
                if (!biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), selectedBiblio.getId())) {
                    showError("Permission Denied. You don't have access to add books to this library.");
                    return;
                }
                
                boolean success = bookService.addBook(newBook);
                
                if (success) {
                    bookController.refreshTable();
                    dialogStage.close();
                } else {
                    showError("Failed to add the book. Please try again.");
                }
            } else {
                // Check if user can edit this book
                if (book.getBiblio() != null && 
                    !biblioService.canUserAccessBiblio(UserService.getCurrentUserId(), book.getBiblio().getId())) {
                    showError("Permission Denied. You don't have access to edit this book.");
                    return;
                }
                
                book.setName(name);
                book.setTitle(title);
                book.setAuthor(author);
                book.setPrice(price);
                book.setBiblioId(selectedBiblio.getId());
                
                boolean success = bookService.updateBook(book);
                
                if (success) {
                    bookController.refreshTable();
                    dialogStage.close();
                } else {
                    showError("Failed to update the book. Please try again.");
                }
            }
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean validateInputs() {
        String errorMessage = "";
        
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Name is required.\n";
        }
        
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage += "Title is required.\n";
        }
        
        if (authorField.getText() == null || authorField.getText().trim().isEmpty()) {
            errorMessage += "Author is required.\n";
        }
        
        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            errorMessage += "Price is required.\n";
        } else {
            try {
                Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage += "Price must be a valid number.\n";
            }
        }
        
        if (biblioComboBox.getValue() == null) {
            errorMessage += "Please select a library.\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showError(errorMessage);
            return false;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
