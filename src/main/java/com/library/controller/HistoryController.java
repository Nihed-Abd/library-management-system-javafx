package com.library.controller;

import com.library.model.Book;
import com.library.model.History;
import com.library.service.BookService;
import com.library.service.HistoryService;
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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class HistoryController implements Initializable {

    @FXML
    private Label historyTitleLabel;
    
    @FXML
    private TableView<History> historyTableView;
    
    @FXML
    private TableColumn<History, String> bookColumn;
    
    @FXML
    private TableColumn<History, String> biblioColumn;
    
    @FXML
    private TableColumn<History, String> typeColumn;
    
    @FXML
    private TableColumn<History, String> dateColumn;
    
    @FXML
    private TableColumn<History, String> noteColumn;

    @FXML
    private TableColumn<History, String> ownerColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private CheckBox loanOnlyCheckbox;
    
    @FXML
    private CheckBox returnOnlyCheckbox;
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private Label totalHistoryLabel;
    
    private final int ITEMS_PER_PAGE = 10;
    private final HistoryService historyService = new HistoryService();
    private final BookService bookService = new BookService();
    private final ObservableList<History> historyList = FXCollections.observableArrayList();
    private FilteredList<History> filteredData;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private Book currentBook;
    private int currentBiblioId;
    private boolean showAllHistory = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTable();
        setupFiltering();
        setupPagination();
        
        // Add animation to the table
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), historyTableView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void configureTable() {
        // Configure the table columns
        bookColumn.setCellValueFactory(cellData -> {
            History history = cellData.getValue();
            String bookName = history.getBook() != null ? history.getBook().getTitle() : "Unknown";
            return new SimpleStringProperty(bookName);
        });
        
        biblioColumn.setCellValueFactory(cellData -> {
            History history = cellData.getValue();
            String biblioName = history.getBook() != null && history.getBook().getBiblio() != null ? 
                history.getBook().getBiblio().getName() : "Unknown";
            return new SimpleStringProperty(biblioName);
        });
        
        typeColumn.setCellValueFactory(cellData -> {
            String type = cellData.getValue().isLoan() ? "Loan" : "Return";
            return new SimpleStringProperty(type);
        });
        
        // Add CSS class to type column based on value
        typeColumn.setCellFactory(column -> {
            return new TableCell<History, String>() {
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
                        
                        // Apply appropriate style class based on type
                        if ("Return".equals(item)) {
                            getStyleClass().add("available-status");
                        } else {
                            getStyleClass().add("loaned-status");
                        }
                    }
                }
            };
        });
        
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate().format(dateFormatter)));
        
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        
        // Make the note column wider to show more text
        noteColumn.setPrefWidth(200);
        
        // Only show the owner column for all history view
        if (ownerColumn != null) {
            ownerColumn.setCellValueFactory(cellData -> {
                History history = cellData.getValue();
                String ownerName = history.getBook() != null && history.getBook().getBiblio() != null && 
                                  history.getBook().getBiblio().getUser() != null ? 
                    history.getBook().getBiblio().getUser().getUsername() : "Unknown";
                return new SimpleStringProperty(ownerName);
            });
            
            // Hide the owner column if not admin or not showing all history
            ownerColumn.setVisible(UserService.isAdmin() && showAllHistory);
        }

        // Enable row selection
        historyTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Make the table resizable and fill available space
        historyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setupFiltering() {
        // Initialize filter combo box
        filterComboBox.getItems().addAll("All History", "Recent (Last 7 Days)", "Recent (Last 30 Days)", "Recent (Last Year)");
        filterComboBox.getSelectionModel().selectFirst();
        
        // Add mutual exclusivity for the checkboxes
        loanOnlyCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && returnOnlyCheckbox.isSelected()) {
                returnOnlyCheckbox.setSelected(false);
            }
        });
        
        returnOnlyCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && loanOnlyCheckbox.isSelected()) {
                loanOnlyCheckbox.setSelected(false);
            }
        });
        
        // Initialize filtered list
        filteredData = new FilteredList<>(historyList, p -> true);
        
        // Add listener for search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createPredicate(
                newValue, 
                filterComboBox.getValue(), 
                loanOnlyCheckbox.isSelected(),
                returnOnlyCheckbox.isSelected()
            ));
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
        
        // Update the total history label
        totalHistoryLabel.setText(String.format("Total: %d records", filteredData.size()));
    }
    
    private void updatePageContent(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredData.size());
        
        // Create a sublist for the current page
        ObservableList<History> pageItems;
        
        if (filteredData.isEmpty()) {
            pageItems = FXCollections.observableArrayList();
        } else {
            pageItems = FXCollections.observableArrayList(
                    filteredData.subList(fromIndex, toIndex));
        }
        
        // Set the items to the table
        historyTableView.setItems(pageItems);
    }
    
    private Predicate<History> createPredicate(String searchText, String filterOption, boolean loanOnly, boolean returnOnly) {
        return history -> {
            // Always apply search text filter
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    (history.getBook() != null && history.getBook().getTitle().toLowerCase().contains(searchText.toLowerCase())) ||
                    (history.getNote() != null && history.getNote().toLowerCase().contains(searchText.toLowerCase())) ||
                    (history.getBook() != null && history.getBook().getBiblio() != null && 
                     history.getBook().getBiblio().getName().toLowerCase().contains(searchText.toLowerCase()));
            
            // Apply loan/return filter if needed
            boolean matchesType = true;
            if (loanOnly) {
                matchesType = history.isLoan();
            } else if (returnOnly) {
                matchesType = !history.isLoan();
            }
            
            // Apply date filter based on combo box selection
            boolean matchesDateFilter = true;
            
            if (filterOption != null) {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                switch (filterOption) {
                    case "Recent (Last 7 Days)":
                        matchesDateFilter = history.getDate().isAfter(now.minusDays(7));
                        break;
                    case "Recent (Last 30 Days)":
                        matchesDateFilter = history.getDate().isAfter(now.minusDays(30));
                        break;
                    case "Recent (Last Year)":
                        matchesDateFilter = history.getDate().isAfter(now.minusYears(1));
                        break;
                    default:
                        // "All History" - no additional filtering
                        break;
                }
            }
            
            return matchesSearch && matchesType && matchesDateFilter;
        };
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        filteredData.setPredicate(createPredicate(
            searchText, 
            filterComboBox.getValue(), 
            loanOnlyCheckbox.isSelected(),
            returnOnlyCheckbox.isSelected()
        ));
        updatePagination();
    }
    
    @FXML
    private void handleFilter() {
        String filterOption = filterComboBox.getValue();
        boolean loanOnly = loanOnlyCheckbox.isSelected();
        boolean returnOnly = returnOnlyCheckbox.isSelected();
        
        filteredData.setPredicate(createPredicate(
            searchField.getText().trim(), 
            filterOption, 
            loanOnly,
            returnOnly
        ));
        updatePagination();
    }
    
    public void setBook(Book book) {
        this.currentBook = book;
        this.showAllHistory = false;
        
        // Check if user has access to this book
        if (book.getBiblio() != null && !UserService.isAdmin() && 
            book.getBiblio().getUserId() != UserService.getCurrentUserId()) {
            DialogUtil.showError("Access Denied", "Permission Denied", 
                "You don't have permission to view this book's history.");
            goBack();
            return;
        }
        
        historyTitleLabel.setText("History for: " + book.getTitle());
        
        // Load history for this book
        historyList.clear();
        historyList.addAll(historyService.getHistoryByBookId(book.getId()));
        
        // Apply the default predicate
        filteredData.setPredicate(createPredicate(
            "", 
            filterComboBox.getValue(), 
            loanOnlyCheckbox.isSelected(),
            returnOnlyCheckbox.isSelected()
        ));
        
        // Update pagination
        updatePagination();
    }
    
    public void setBiblioId(int biblioId) {
        this.currentBiblioId = biblioId;
        this.showAllHistory = false;
        
        historyTitleLabel.setText("History for Library ID: " + biblioId);
        
        // Load history for this biblio (only if user has access)
        historyList.clear();
        historyList.addAll(historyService.getHistoryByBiblioId(biblioId));
        
        // Apply the default predicate
        filteredData.setPredicate(createPredicate(
            "", 
            filterComboBox.getValue(), 
            loanOnlyCheckbox.isSelected(),
            returnOnlyCheckbox.isSelected()
        ));
        
        // Update pagination
        updatePagination();
    }
    
    public void setShowAllHistory(boolean showAllHistory) {
        this.showAllHistory = showAllHistory;
        
        historyTitleLabel.setText("All History");
        
        // Reconfigure table to show owner column for admin
        configureTable();
        
        // Load all history (filtered by user permissions)
        historyList.clear();
        historyList.addAll(historyService.getAllHistory());
        
        // Apply the default predicate
        filteredData.setPredicate(createPredicate(
            "", 
            filterComboBox.getValue(), 
            loanOnlyCheckbox.isSelected(),
            returnOnlyCheckbox.isSelected()
        ));
        
        // Update pagination
        updatePagination();
    }
    
    @FXML
    private void goBack() {
        try {
            Parent view;
            StackPane contentPane = (StackPane) historyTableView.getScene().getRoot().lookup("#contentPane");
            
            if (contentPane != null) {
                FXMLLoader loader;
                
                if (currentBook != null) {
                    // Go back to the book view and pass the current book's biblio
                    loader = new FXMLLoader(getClass().getResource("/com/library/view/BookView.fxml"));
                    view = loader.load();
                    
                    BookController controller = loader.getController();
                    controller.setBiblio(currentBook.getBiblio());
                } else if (currentBiblioId > 0) {
                    // Go back to the biblio view
                    loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioView.fxml"));
                    view = loader.load();
                } else {
                    // Default back to biblio view
                    loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioView.fxml"));
                    view = loader.load();
                }
                
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
                "Error loading the previous view: " + e.getMessage());
        }
    }
}
