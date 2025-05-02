package com.library.controller;

import com.library.model.Biblio;
import com.library.model.Book;
import com.library.model.History;
import com.library.service.BiblioService;
import com.library.service.BookService;
import com.library.service.HistoryService;
import com.library.util.DialogUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    @FXML
    private TableView<History> historyTableView;
    
    @FXML
    private TableColumn<History, Integer> idColumn;
    
    @FXML
    private TableColumn<History, String> dateTimeColumn;
    
    @FXML
    private TableColumn<History, String> statusColumn;
    
    @FXML
    private TableColumn<History, String> bookColumn;
    
    @FXML
    private TableColumn<History, String> biblioColumn;
    
    @FXML
    private TableColumn<History, String> noteColumn;
    
    @FXML
    private TableColumn<History, Void> actionsColumn;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private ComboBox<Biblio> biblioComboBox;
    
    @FXML
    private ComboBox<Book> bookComboBox;
    
    private final HistoryService historyService = new HistoryService();
    private final BiblioService biblioService = new BiblioService();
    private final BookService bookService = new BookService();
    private final ObservableList<History> historyList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTable();
        setupFilters();
        loadAllHistory();
    }
    
    private void configureTable() {
        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        dateTimeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateTime().format(dateTimeFormatter)));
        
        statusColumn.setCellValueFactory(cellData -> {
            History.HistoryStatus status = cellData.getValue().getStatus();
            SimpleStringProperty property = new SimpleStringProperty(status.getDisplayName());
            
            // This will be used for CSS styling
            TableCell<History, String> cell = new TableCell<History, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        getStyleClass().removeAll("loan-status", "return-status");
                        getStyleClass().add(item.equals("Loan") ? "loan-status" : "return-status");
                    }
                }
            };
            
            return property;
        });
        
        bookColumn.setCellValueFactory(cellData -> {
            Book book = cellData.getValue().getBook();
            return new SimpleStringProperty(book != null ? book.getTitle() : "Unknown");
        });
        
        biblioColumn.setCellValueFactory(cellData -> {
            Biblio biblio = cellData.getValue().getBiblio();
            return new SimpleStringProperty(biblio != null ? biblio.getName() : "Unknown");
        });
        
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        
        // Configure the actions column
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            
            {
                deleteButton.getStyleClass().add("button-delete");
                
                deleteButton.setOnAction(event -> {
                    History history = getTableRow().getItem();
                    if (history != null) {
                        handleDeleteHistory(history);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }
    
    private void setupFilters() {
        // Set up filter type combo box
        filterComboBox.setItems(FXCollections.observableArrayList("All History", "By Library", "By Book"));
        filterComboBox.getSelectionModel().selectFirst();
        
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateFilterVisibility(newValue);
            }
        });
        
        // Set up biblio combo box
        List<Biblio> biblios = biblioService.getAllBiblios();
        biblioComboBox.setItems(FXCollections.observableArrayList(biblios));
        
        biblioComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadHistoryByBiblio(newValue.getId());
                
                // Update book combo box with books from this biblio
                List<Book> books = bookService.getBooksByBiblioId(newValue.getId());
                bookComboBox.setItems(FXCollections.observableArrayList(books));
                bookComboBox.getSelectionModel().clearSelection();
            }
        });
        
        // Set up book combo box (initially disabled)
        bookComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadHistoryByBook(newValue.getId());
            }
        });
        
        // Initially hide filters
        biblioComboBox.setVisible(false);
        biblioComboBox.setManaged(false);
        bookComboBox.setVisible(false);
        bookComboBox.setManaged(false);
    }
    
    private void updateFilterVisibility(String filterType) {
        switch (filterType) {
            case "All History":
                biblioComboBox.setVisible(false);
                biblioComboBox.setManaged(false);
                bookComboBox.setVisible(false);
                bookComboBox.setManaged(false);
                loadAllHistory();
                break;
            case "By Library":
                biblioComboBox.setVisible(true);
                biblioComboBox.setManaged(true);
                bookComboBox.setVisible(false);
                bookComboBox.setManaged(false);
                biblioComboBox.getSelectionModel().clearSelection();
                break;
            case "By Book":
                biblioComboBox.setVisible(true);
                biblioComboBox.setManaged(true);
                bookComboBox.setVisible(true);
                bookComboBox.setManaged(true);
                biblioComboBox.getSelectionModel().clearSelection();
                bookComboBox.getSelectionModel().clearSelection();
                break;
        }
    }
    
    private void loadAllHistory() {
        historyList.clear();
        historyList.addAll(historyService.getAllHistory());
        historyTableView.setItems(historyList);
    }
    
    private void loadHistoryByBiblio(int biblioId) {
        historyList.clear();
        historyList.addAll(historyService.getHistoryByBiblioId(biblioId));
        historyTableView.setItems(historyList);
    }
    
    private void loadHistoryByBook(int bookId) {
        historyList.clear();
        historyList.addAll(historyService.getHistoryByBookId(bookId));
        historyTableView.setItems(historyList);
    }
    
    private void handleDeleteHistory(History history) {
        boolean confirmed = DialogUtil.showConfirmation("Confirm Delete", "Delete History Record", 
            "Are you sure you want to delete this history record?");
        
        if (confirmed) {
            boolean success = historyService.deleteHistory(history.getId());
            
            if (success) {
                refreshTable();
                DialogUtil.showInformation("Success", "History Record Deleted", 
                    "The history record has been deleted successfully.");
            } else {
                DialogUtil.showError("Error", "Delete Failed", 
                    "Failed to delete the history record. Please try again.");
            }
        }
    }
    
    @FXML
    private void clearFilters() {
        filterComboBox.getSelectionModel().selectFirst();
        biblioComboBox.getSelectionModel().clearSelection();
        bookComboBox.getSelectionModel().clearSelection();
        loadAllHistory();
    }
    
    public void refreshTable() {
        String filterType = filterComboBox.getSelectionModel().getSelectedItem();
        
        if (filterType.equals("All History")) {
            loadAllHistory();
        } else if (filterType.equals("By Library")) {
            Biblio selectedBiblio = biblioComboBox.getSelectionModel().getSelectedItem();
            if (selectedBiblio != null) {
                loadHistoryByBiblio(selectedBiblio.getId());
            }
        } else if (filterType.equals("By Book")) {
            Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                loadHistoryByBook(selectedBook.getId());
            }
        }
    }
}
