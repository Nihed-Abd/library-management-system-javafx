package com.library.controller;

import com.library.model.Biblio;
import com.library.model.Book;
import com.library.service.BiblioService;
import com.library.service.BookService;
import com.library.service.UserService;
import com.library.util.DialogUtil;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private CheckBox availableOnlyCheckbox;
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private Label totalBooksLabel;
    
    private final int ITEMS_PER_PAGE = 10;
    private final BookService bookService = new BookService();
    private final BiblioService biblioService = new BiblioService();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private FilteredList<Book> filteredData;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private Biblio currentBiblio;
    private boolean showAllBooks = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTable();
        setupFiltering();
        setupPagination();
        
        // Default to showing all books when the view is first loaded
        setShowAllBooks(true);
        
        // Add animation to the table
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), bookTableView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void configureTable() {
        // Configure the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        statusColumn.setCellValueFactory(cellData -> {
            boolean available = cellData.getValue().isAvailable();
            String status = available ? "Available" : "Loaned";
            return new SimpleStringProperty(status);
        });
        
        // Add CSS class to status column based on value
        statusColumn.setCellFactory(column -> {
            return new TableCell<Book, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                        getStyleClass().removeAll("available-status", "loaned-status");
                    } else {
                        setText(item);
                        getStyleClass().removeAll("available-status", "loaned-status");
                        
                        // Apply appropriate style class based on status
                        if ("Available".equals(item)) {
                            getStyleClass().add("available-status");
                        } else {
                            getStyleClass().add("loaned-status");
                        }
                    }
                }
            };
        });
        
        dateCreationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateCreation().format(dateFormatter)));
        
        // Configure the actions column
        setupActionsColumn();
    }
    
    private void setupFiltering() {
        // Initialize filter combo box
        filterComboBox.getItems().addAll("All Books", "Fiction", "Science", "History", "Educational");
        filterComboBox.getSelectionModel().selectFirst();
        
        // Initialize filtered list
        filteredData = new FilteredList<>(bookList, p -> true);
        
        // Add listener for search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createPredicate(newValue, filterComboBox.getValue(), availableOnlyCheckbox.isSelected()));
            updatePagination();
        });
    }
    
    private void setupPagination() {
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(0);
        pagination.setMaxPageIndicatorCount(5);
        
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            updatePageContent(newIndex.intValue());
        });
    }
    
    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) filteredData.size() / ITEMS_PER_PAGE);
        totalPages = totalPages == 0 ? 1 : totalPages;
        pagination.setPageCount(totalPages);
        
        // Update the current page content
        updatePageContent(pagination.getCurrentPageIndex());
        
        // Update the total books label
        totalBooksLabel.setText(String.format("Total: %d books", filteredData.size()));
    }
    
    private void updatePageContent(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredData.size());
        
        // Create a sublist for the current page
        ObservableList<Book> pageItems;
        
        if (filteredData.isEmpty()) {
            pageItems = FXCollections.observableArrayList();
        } else {
            pageItems = FXCollections.observableArrayList(
                    filteredData.subList(fromIndex, toIndex));
        }
        
        // Set the items to the table
        bookTableView.setItems(pageItems);
    }
    
    private Predicate<Book> createPredicate(String searchText, String filterOption, boolean availableOnly) {
        return book -> {
            // Always apply search text filter
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    book.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    book.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(searchText.toLowerCase()));
            
            // Apply availability filter if needed
            boolean matchesAvailability = !availableOnly || book.isAvailable();
            
            // Apply category filter based on combo box selection
            boolean matchesCategory = true;
            
            if (filterOption != null && !filterOption.equals("All Books")) {
                // This is a simple implementation - in a real app, you'd have a proper category field
                matchesCategory = book.getTitle().toLowerCase().contains(filterOption.toLowerCase()) ||
                                 (book.getDescription() != null && 
                                  book.getDescription().toLowerCase().contains(filterOption.toLowerCase()));
            }
            
            return matchesSearch && matchesAvailability && matchesCategory;
        };
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        filteredData.setPredicate(createPredicate(searchText, filterComboBox.getValue(), availableOnlyCheckbox.isSelected()));
        updatePagination();
    }
    
    @FXML
    private void handleFilter() {
        String filterOption = filterComboBox.getValue();
        boolean availableOnly = availableOnlyCheckbox.isSelected();
        filteredData.setPredicate(createPredicate(searchField.getText().trim(), filterOption, availableOnly));
        updatePagination();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button loanButton = new Button("Loan");
            private final Button returnButton = new Button("Return");
            private final HBox buttonContainer = new HBox(8); // Increased spacing between buttons
            
            {
                // Configure buttons
                viewButton.getStyleClass().add("action-button");
                editButton.getStyleClass().add("edit-button");
                deleteButton.getStyleClass().add("delete-button");
                loanButton.getStyleClass().add("loan-button");
                returnButton.getStyleClass().add("return-button");
                
                // Set button icons or text
                viewButton.setPrefWidth(80);
                editButton.setPrefWidth(80);
                deleteButton.setPrefWidth(80);
                loanButton.setPrefWidth(80);
                returnButton.setPrefWidth(80);
                
                buttonContainer.setAlignment(Pos.CENTER);
                
                loanButton.setOnAction(event -> {
                    Book book = getTableRow().getItem();
                    if (book != null) {
                        handleLoanBook(book);
                    }
                });
                
                returnButton.setOnAction(event -> {
                    Book book = getTableRow().getItem();
                    if (book != null) {
                        handleReturnBook(book);
                    }
                });
                
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
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableRow().getItem();
                    if (book != null) {
                        buttonContainer.getChildren().clear();
                        
                        // Only show edit/delete for user's own libraries or admin
                        boolean canManage = UserService.isAdmin() || 
                            (book.getBiblio() != null && book.getBiblio().getUserId() == UserService.getCurrentUserId());
                        
                        if (canManage) {
                            buttonContainer.getChildren().addAll(editButton, deleteButton);
                        }
                        
                        // Show loan/return buttons based on book availability
                        if (book.isAvailable()) {
                            buttonContainer.getChildren().add(loanButton);
                        } else {
                            buttonContainer.getChildren().add(returnButton);
                        }
                        
                        setGraphic(buttonContainer);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }
    
    public void setBiblio(Biblio biblio) {
        this.currentBiblio = biblio;
        this.showAllBooks = (biblio == null);
        
        if (biblio != null) {
            biblioNameLabel.setText("Books in " + biblio.getName());
            updateBiblioDetails();
        } else {
            biblioNameLabel.setText("All Books");
            
            // Hide biblio details for "All Books" view
            detailsNameLabel.setText("");
            detailsLocationLabel.setText("");
            detailsDescriptionLabel.setText("");
        }
        
        loadBooks();
    }
    
    public void setShowAllBooks(boolean showAllBooks) {
        this.showAllBooks = showAllBooks;
        
        if (showAllBooks) {
            this.currentBiblio = null;
            biblioNameLabel.setText("All Books");
            
            // Hide biblio details for "All Books" view
            detailsNameLabel.setText("");
            detailsLocationLabel.setText("");
            detailsDescriptionLabel.setText("");
        }
        
        loadBooks();
    }
    
    private void updateBiblioDetails() {
        if (currentBiblio != null) {
            detailsNameLabel.setText(currentBiblio.getName());
            detailsLocationLabel.setText(currentBiblio.getLocation());
            detailsDescriptionLabel.setText(currentBiblio.getDescription());
        }
    }
    
    private void loadBooks() {
        bookList.clear();
        
        // Always show all books regardless of whether a specific library is selected
        // This ensures users can see all books in the system
        if (showAllBooks) {
            // For "All Books" view, get all books in the system
            bookList.addAll(bookService.getAllBooks());
        } else if (currentBiblio != null) {
            // For a specific library view, only show books for that library
            bookList.addAll(bookService.getBooksByBiblioId(currentBiblio.getId()));
        }
        
        // Apply the default predicate
        filteredData.setPredicate(createPredicate("", filterComboBox.getValue(), availableOnlyCheckbox.isSelected()));
        
        // Update pagination
        updatePagination();
    }
    
    @FXML
    public void showAddBookDialog() {
        // Can't add a book without selecting a library
        if (currentBiblio == null && !showAllBooks) {
            DialogUtil.showWarning("No Library Selected", "Cannot Add Book", 
                "Please select a library first to add a book.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookDialogView.fxml"));
            Parent root = loader.load();
            
            BookDialogController controller = loader.getController();
            controller.setAddMode(true);
            
            if (currentBiblio != null) {
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
        // Check if user can edit this book
        if (book.getBiblio() != null && !biblioService.canUserAccessBiblio(
                UserService.getCurrentUserId(), book.getBiblio().getId())) {
            DialogUtil.showError("Permission Denied", "Cannot Edit", 
                "You don't have permission to edit this book.");
            return;
        }
        
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
        // Check if user can delete this book
        if (book.getBiblio() != null && !biblioService.canUserAccessBiblio(
                UserService.getCurrentUserId(), book.getBiblio().getId())) {
            DialogUtil.showError("Permission Denied", "Cannot Delete", 
                "You don't have permission to delete this book.");
            return;
        }
        
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
        // Check if the book is already loaned
        if (!book.isAvailable()) {
            DialogUtil.showWarning("Book Unavailable", "Cannot Loan Book", 
                "This book is already loaned out.");
            return;
        }
        
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
        // Check if the book is available (can't return a book that's not loaned)
        if (book.isAvailable()) {
            DialogUtil.showWarning("Book Available", "Cannot Return Book", 
                "This book is not currently loaned out.");
            return;
        }
        
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioView.fxml"));
                Parent view = loader.load();
                
                // Add fade transition for smooth view change
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), view);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                
                contentPane.getChildren().clear();
                contentPane.getChildren().add(view);
                
                fadeIn.play();
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
