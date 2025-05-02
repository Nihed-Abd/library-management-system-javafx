package com.library.controller;

import com.library.model.Biblio;
import com.library.model.Book;
import com.library.service.BiblioService;
import com.library.service.BookService;
import com.library.util.DialogUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BookController implements Initializable {

    @FXML
    private Label biblioNameLabel;
    
    @FXML
    private Label detailsNameLabel;
    
    @FXML
    private Label detailsLocationLabel;
    
    @FXML
    private Label detailsDescriptionLabel;
    
    @FXML
    private TableView<Book> bookTableView;
    
    @FXML
    private TableColumn<Book, Integer> idColumn;
    
    @FXML
    private TableColumn<Book, String> nameColumn;
    
    @FXML
    private TableColumn<Book, String> titleColumn;
    
    @FXML
    private TableColumn<Book, String> authorColumn;
    
    @FXML
    private TableColumn<Book, Double> priceColumn;
    
    @FXML
    private TableColumn<Book, String> statusColumn;
    
    @FXML
    private TableColumn<Book, String> dateCreationColumn;
    
    @FXML
    private TableColumn<Book, Void> actionsColumn;
    
    private final BookService bookService = new BookService();
    private final BiblioService biblioService = new BiblioService();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private Biblio currentBiblio;
    private boolean showAllBooks = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTable();
    }
    
    private void configureTable() {
        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        statusColumn.setCellValueFactory(cellData -> {
            boolean available = cellData.getValue().isAvailable();
            String status = available ? "Available" : "Loaned";
            SimpleStringProperty property = new SimpleStringProperty(status);
            
            // This will be used for CSS styling
            TableCell<Book, String> cell = new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        getStyleClass().removeAll("available", "not-available");
                        getStyleClass().add(item.equals("Available") ? "available" : "not-available");
                    }
                }
            };
            
            return property;
        });
        
        dateCreationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateCreation().format(dateFormatter)));
        
        // Configure the actions column
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button loanButton = new Button("Loan");
            private final Button returnButton = new Button("Return");
            private final HBox pane = new HBox(5);
            
            {
                editButton.getStyleClass().add("button-edit");
                deleteButton.getStyleClass().add("button-delete");
                loanButton.getStyleClass().add("button-loan");
                returnButton.getStyleClass().add("button-return");
                
                editButton.setOnAction(event -> {
                    Book book = getTableRow().getItem();
                    if (book != null) {
                        showEditBookDialog(book);
                    }
                });
                
                deleteButton.setOnAction(event -> {
                    Book book = getTableRow().getItem();
                    if (book != null) {
                        handleDeleteBook(book);
                    }
                });
                
                loanButton.setOnAction(event -> {
                    Book book = getTableRow().getItem();
                    if (book != null && book.isAvailable()) {
                        handleLoanBook(book);
                    }
                });
                
                returnButton.setOnAction(event -> {
                    Book book = getTableRow().getItem();
                    if (book != null && !book.isAvailable()) {
                        handleReturnBook(book);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableRow().getItem();
                    
                    if (book != null) {
                        pane.getChildren().clear();
                        pane.getChildren().addAll(editButton, deleteButton);
                        
                        if (book.isAvailable()) {
                            pane.getChildren().add(loanButton);
                        } else {
                            pane.getChildren().add(returnButton);
                        }
                        
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }
    
    public void setBiblio(Biblio biblio) {
        this.currentBiblio = biblio;
        this.showAllBooks = false;
        
        // Update the UI
        updateBiblioDetails();
        
        // Load books for this biblio
        loadBooks();
    }
    
    public void setShowAllBooks(boolean showAllBooks) {
        this.showAllBooks = showAllBooks;
        
        // Update UI for all books view
        biblioNameLabel.setText("All Books");
        
        // Hide biblio details section
        detailsNameLabel.getParent().getParent().setVisible(false);
        detailsNameLabel.getParent().getParent().setManaged(false);
        
        // Load all books
        loadBooks();
    }
    
    private void updateBiblioDetails() {
        if (currentBiblio != null) {
            biblioNameLabel.setText("Books in " + currentBiblio.getName());
            detailsNameLabel.setText(currentBiblio.getName());
            detailsLocationLabel.setText(currentBiblio.getLocation());
            detailsDescriptionLabel.setText(currentBiblio.getDescription());
            
            // Make sure biblio details are visible
            detailsNameLabel.getParent().getParent().setVisible(true);
            detailsNameLabel.getParent().getParent().setManaged(true);
        }
    }
    
    private void loadBooks() {
        bookList.clear();
        
        if (showAllBooks) {
            bookList.addAll(bookService.getAllBooks());
        } else if (currentBiblio != null) {
            bookList.addAll(bookService.getBooksByBiblioId(currentBiblio.getId()));
        }
        
        bookTableView.setItems(bookList);
    }
    
    @FXML
    private void showAddBookDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookDialogView.fxml"));
            Parent root = loader.load();
            
            BookDialogController controller = loader.getController();
            controller.setAddMode(true);
            
            // If we're in a specific biblio view, pre-select that biblio
            if (!showAllBooks && currentBiblio != null) {
                controller.setPreSelectedBiblio(currentBiblio);
            }
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Book");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(bookTableView.getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setBookController(this);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Error", "Dialog Error", 
                "Error loading the dialog: " + e.getMessage());
        }
    }
    
    private void showEditBookDialog(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookDialogView.fxml"));
            Parent root = loader.load();
            
            BookDialogController controller = loader.getController();
            controller.setAddMode(false);
            controller.setBook(book);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Book");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(bookTableView.getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setBookController(this);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Error", "Dialog Error", 
                "Error loading the dialog: " + e.getMessage());
        }
    }
    
    private void handleDeleteBook(Book book) {
        boolean confirmed = DialogUtil.showConfirmation("Confirm Delete", "Delete Book", 
            "Are you sure you want to delete the book: " + book.getTitle() + "?");
        
        if (confirmed) {
            boolean success = bookService.deleteBook(book.getId());
            
            if (success) {
                loadBooks();
                DialogUtil.showInformation("Success", "Book Deleted", 
                    "The book has been deleted successfully.");
            } else {
                DialogUtil.showError("Error", "Delete Failed", 
                    "Failed to delete the book. Please try again.");
            }
        }
    }
    
    private void handleLoanBook(Book book) {
        String note = DialogUtil.showTextInput("Loan Book", "Enter Loan Details", 
            "Please enter a note for this loan:", "");
        
        if (note != null) {
            boolean success = bookService.loanBook(book.getId(), note);
            
            if (success) {
                loadBooks();
                DialogUtil.showInformation("Success", "Book Loaned", 
                    "The book has been marked as loaned and the history has been updated.");
            } else {
                DialogUtil.showError("Error", "Loan Failed", 
                    "Failed to loan the book. Please try again.");
            }
        }
    }
    
    private void handleReturnBook(Book book) {
        String note = DialogUtil.showTextInput("Return Book", "Enter Return Details", 
            "Please enter a note for this return:", "");
        
        if (note != null) {
            boolean success = bookService.returnBook(book.getId(), note);
            
            if (success) {
                loadBooks();
                DialogUtil.showInformation("Success", "Book Returned", 
                    "The book has been marked as available and the history has been updated.");
            } else {
                DialogUtil.showError("Error", "Return Failed", 
                    "Failed to return the book. Please try again.");
            }
        }
    }
    
    @FXML
    private void backToBiblios() {
        try {
            // Get the main application's StackPane
            StackPane contentPane = (StackPane) bookTableView.getScene().getRoot().lookup("#contentPane");
            
            if (contentPane != null) {
                Parent view = FXMLLoader.load(getClass().getResource("/com/library/view/BiblioView.fxml"));
                contentPane.getChildren().clear();
                contentPane.getChildren().add(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Error", "Navigation Error", 
                "Error loading the libraries view: " + e.getMessage());
        }
    }
    
    public void refreshTable() {
        loadBooks();
    }
}
