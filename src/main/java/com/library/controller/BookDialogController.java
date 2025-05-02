package com.library.controller;

import com.library.model.Biblio;
import com.library.model.Book;
import com.library.service.BiblioService;
import com.library.service.BookService;
import com.library.util.DialogUtil;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BookDialogController implements Initializable {

    @FXML
    private TextField nameField;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextField authorField;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Label previewTitle;
    
    @FXML
    private Label previewAuthor;
    
    @FXML
    private Label previewName;
    
    @FXML
    private Label previewPrice;
    
    @FXML
    private Label previewDescription;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private ComboBox<Biblio> biblioComboBox;
    
    @FXML
    private Label biblioLabel;
    
    private Stage dialogStage;
    private Book book;
    private boolean addMode = true;
    private BookController bookController;
    private final BookService bookService = new BookService();
    private final BiblioService biblioService = new BiblioService();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the book object
        book = new Book();
        
        // Add listeners to update preview in real-time
        setupLivePreview();
        
        // Validate input
        setupValidation();
        
        // Load biblios for combobox
        loadBiblios();
    }
    
    private void setupLivePreview() {
        // Update preview when name changes
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            previewName.setText(newValue);
        });
        
        // Update preview when title changes
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            previewTitle.setText(newValue);
        });
        
        // Update preview when author changes
        authorField.textProperty().addListener((observable, oldValue, newValue) -> {
            previewAuthor.setText(newValue);
        });
        
        // Update preview when price changes
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            previewPrice.setText(newValue);
        });
        
        // Update preview when description changes
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            previewDescription.setText(newValue);
        });
    }
    
    private void setupValidation() {
        // Validate that price is a number
        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                priceField.setText(oldValue);
            }
        });
        
        // Disable save button if required fields are empty
        ChangeListener<String> validator = (observable, oldValue, newValue) -> {
            boolean nameValid = !nameField.getText().trim().isEmpty();
            boolean titleValid = !titleField.getText().trim().isEmpty();
            boolean authorValid = !authorField.getText().trim().isEmpty();
            boolean priceValid = !priceField.getText().trim().isEmpty();
            boolean biblioValid = biblioComboBox.getSelectionModel().getSelectedItem() != null;
            
            saveButton.setDisable(!nameValid || !titleValid || !authorValid || !priceValid || !biblioValid);
        };
        
        nameField.textProperty().addListener(validator);
        titleField.textProperty().addListener(validator);
        authorField.textProperty().addListener(validator);
        priceField.textProperty().addListener(validator);
        biblioComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean nameValid = !nameField.getText().trim().isEmpty();
            boolean titleValid = !titleField.getText().trim().isEmpty();
            boolean authorValid = !authorField.getText().trim().isEmpty();
            boolean priceValid = !priceField.getText().trim().isEmpty();
            boolean biblioValid = newValue != null;
            
            saveButton.setDisable(!nameValid || !titleValid || !authorValid || !priceValid || !biblioValid);
        });
        
        // Initial validation
        saveButton.setDisable(true);
    }
    
    private void loadBiblios() {
        List<Biblio> biblios = biblioService.getAllBiblios();
        biblioComboBox.setItems(FXCollections.observableArrayList(biblios));
    }
    
    public void setBook(Book book) {
        this.book = book;
        
        // Set the fields with the book data
        nameField.setText(book.getName());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        priceField.setText(String.valueOf(book.getPrice()));
        descriptionArea.setText(book.getDescription());
        
        // Set biblio
        Biblio biblio = biblioService.getBiblioById(book.getBiblioId());
        if (biblio != null) {
            biblioComboBox.getSelectionModel().select(biblio);
        }
        
        // Update preview
        previewName.setText(book.getName());
        previewTitle.setText(book.getTitle());
        previewAuthor.setText(book.getAuthor());
        previewPrice.setText(String.valueOf(book.getPrice()));
        previewDescription.setText(book.getDescription());
    }
    
    public void setPreSelectedBiblio(Biblio biblio) {
        biblioComboBox.getSelectionModel().select(biblio);
        
        // If this is for a specific biblio, hide the biblio selection
        biblioLabel.setVisible(false);
        biblioLabel.setManaged(false);
        biblioComboBox.setVisible(false);
        biblioComboBox.setManaged(false);
    }
    
    public void setAddMode(boolean addMode) {
        this.addMode = addMode;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }
    
    @FXML
    private void handleSave() {
        // Get values from fields
        String name = nameField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String priceText = priceField.getText().trim();
        String description = descriptionArea.getText().trim();
        Biblio selectedBiblio = biblioComboBox.getSelectionModel().getSelectedItem();
        
        // Validate input
        if (name.isEmpty() || title.isEmpty() || author.isEmpty() || priceText.isEmpty() || selectedBiblio == null) {
            DialogUtil.showWarning("Validation Error", "Missing Required Fields", 
                "Please fill in all required fields.");
            return;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            DialogUtil.showWarning("Validation Error", "Invalid Price", 
                "Please enter a valid number for price.");
            return;
        }
        
        // Update book object
        book.setName(name);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setDescription(description);
        book.setBiblioId(selectedBiblio.getId());
        
        boolean success;
        if (addMode) {
            // Add new book
            success = bookService.addBook(book);
        } else {
            // Update existing book
            success = bookService.updateBook(book);
        }
        
        if (success) {
            // Refresh the table in the parent controller
            if (bookController != null) {
                bookController.refreshTable();
            }
            
            // Close the dialog
            dialogStage.close();
        } else {
            DialogUtil.showError("Database Error", "Operation Failed", 
                "Failed to " + (addMode ? "add" : "update") + " the book. Please try again.");
        }
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
