package com.library.controller;

import com.library.model.Biblio;
import com.library.service.BiblioService;
import com.library.service.UserService;
import com.library.util.DialogUtil;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class BiblioController implements Initializable {

    @FXML
    private TableView<Biblio> biblioTable;
    
    @FXML
    private TableColumn<Biblio, String> nameColumn;
    
    @FXML
    private TableColumn<Biblio, String> locationColumn;
    
    @FXML
    private TableColumn<Biblio, String> descriptionColumn;
    
    @FXML
    private TableColumn<Biblio, String> dateCreationColumn;
    
    @FXML
    private TableColumn<Biblio, String> ownerColumn;
    
    @FXML
    private TableColumn<Biblio, Void> actionsColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private Label totalLibrariesLabel;
    
    private final int ITEMS_PER_PAGE = 10;
    private final BiblioService biblioService = new BiblioService();
    private final ObservableList<Biblio> biblioList = FXCollections.observableArrayList();
    private FilteredList<Biblio> filteredData;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTable();
        setupFiltering();
        setupPagination();
        loadBiblios();
        
        // Add animation to the table
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), biblioTable);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void configureTable() {
        // Configure the table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        dateCreationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateCreation().format(dateFormatter)));
        
        // Configure the owner column if available
        if (ownerColumn != null) {
            ownerColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getUser() != null ? 
                    cellData.getValue().getUser().getUsername() : "Unknown"));
        }
        
        // Configure the actions column
        setupActionsColumn();
    }
    
    private void setupFiltering() {
        // Initialize filter combo box
        filterComboBox.getItems().addAll("All Libraries", "My Libraries", "Recent Libraries");
        filterComboBox.getSelectionModel().selectFirst();
        
        // Initialize filtered list
        filteredData = new FilteredList<>(biblioList, p -> true);
        
        // Add listener for search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createPredicate(newValue, filterComboBox.getValue()));
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
        
        // Update the total libraries label
        totalLibrariesLabel.setText(String.format("Total: %d libraries", filteredData.size()));
    }
    
    private void updatePageContent(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredData.size());
        
        // Create a sublist for the current page
        ObservableList<Biblio> pageItems;
        
        if (filteredData.isEmpty()) {
            pageItems = FXCollections.observableArrayList();
        } else {
            pageItems = FXCollections.observableArrayList(
                    filteredData.subList(fromIndex, toIndex));
        }
        
        // Set the items to the table
        biblioTable.setItems(pageItems);
    }
    
    private Predicate<Biblio> createPredicate(String searchText, String filterOption) {
        return biblio -> {
            // Always apply search text filter
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    biblio.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    biblio.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                    biblio.getLocation().toLowerCase().contains(searchText.toLowerCase());
            
            // Apply additional filter based on combo box selection
            boolean matchesFilter = true;
            
            if (filterOption != null) {
                switch (filterOption) {
                    case "My Libraries":
                        matchesFilter = biblio.getUserId() == UserService.getCurrentUserId();
                        break;
                    case "Recent Libraries":
                        // Consider libraries created in the last 30 days as "recent"
                        matchesFilter = biblio.getDateCreation().plusDays(30).isAfter(java.time.LocalDate.now());
                        break;
                    default:
                        // "All Libraries" - no additional filtering
                        break;
                }
            }
            
            return matchesSearch && matchesFilter;
        };
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        filteredData.setPredicate(createPredicate(searchText, filterComboBox.getValue()));
        updatePagination();
    }
    
    @FXML
    private void handleFilter() {
        String filterOption = filterComboBox.getValue();
        filteredData.setPredicate(createPredicate(searchField.getText().trim(), filterOption));
        updatePagination();
    }
    
    @FXML
    private void handleAddBiblio() {
        showAddBiblioDialog();
    }
    
    private void setupActionsColumn() {
        Callback<TableColumn<Biblio, Void>, TableCell<Biblio, Void>> cellFactory = 
            param -> new TableCell<>() {
                private final Button viewButton = new Button("View");
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox buttonContainer = new HBox(5);
                
                {
                    // Configure buttons
                    viewButton.getStyleClass().add("action-button");
                    editButton.getStyleClass().add("edit-button");
                    deleteButton.getStyleClass().add("delete-button");
                    
                    // Add buttons to container
                    buttonContainer.getChildren().addAll(viewButton, editButton, deleteButton);
                    
                    // Set up action handlers
                    viewButton.setOnAction(event -> {
                        Biblio biblio = getTableRow().getItem();
                        if (biblio != null) {
                            showBookView(biblio);
                        }
                    });
                    
                    editButton.setOnAction(event -> {
                        Biblio biblio = getTableRow().getItem();
                        if (biblio != null) {
                            showEditBiblioDialog(biblio);
                        }
                    });
                    
                    deleteButton.setOnAction(event -> {
                        Biblio biblio = getTableRow().getItem();
                        if (biblio != null) {
                            handleDeleteBiblio(biblio);
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Biblio biblio = getTableRow().getItem();
                        if (biblio != null) {
                            // Only enable edit/delete if admin or owner
                            boolean canEdit = UserService.isAdmin() || biblio.getUserId() == UserService.getCurrentUserId();
                            editButton.setDisable(!canEdit);
                            deleteButton.setDisable(!canEdit);
                            
                            setGraphic(buttonContainer);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    private void loadBiblios() {
        biblioList.clear();
        biblioList.addAll(biblioService.getAllBiblios());
        
        // Apply the default predicate
        filteredData.setPredicate(createPredicate("", filterComboBox.getValue()));
        
        // Update pagination
        updatePagination();
    }
    
    private void showAddBiblioDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioDialogView.fxml"));
            Parent root = loader.load();
            
            BiblioDialogController controller = loader.getController();
            controller.setAddMode(true);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Library");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(biblioTable.getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setBiblioController(this);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Error", "Dialog Error", 
                "Error loading the dialog: " + e.getMessage());
        }
    }
    
    private void showEditBiblioDialog(Biblio biblio) {
        // Check if user can edit this biblio
        if (!UserService.isAdmin() && biblio.getUserId() != UserService.getCurrentUserId()) {
            DialogUtil.showError("Permission Denied", "Cannot Edit", 
                "You don't have permission to edit this library.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioDialogView.fxml"));
            Parent root = loader.load();
            
            BiblioDialogController controller = loader.getController();
            controller.setAddMode(false);
            controller.setBiblio(biblio);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Library");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(biblioTable.getScene().getWindow());
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
            dialogStage.setScene(scene);
            
            controller.setDialogStage(dialogStage);
            controller.setBiblioController(this);
            
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showError("Error", "Dialog Error", 
                "Error loading the dialog: " + e.getMessage());
        }
    }
    
    private void handleDeleteBiblio(Biblio biblio) {
        // Check if user can delete this biblio
        if (!UserService.isAdmin() && biblio.getUserId() != UserService.getCurrentUserId()) {
            DialogUtil.showError("Permission Denied", "Cannot Delete", 
                "You don't have permission to delete this library.");
            return;
        }
        
        // Check if the library has any books
        int bookCount = biblioService.getBookCountByBiblioId(biblio.getId());
        
        if (bookCount > 0) {
            DialogUtil.showWarning("Cannot Delete", "Library Contains Books", 
                "This library contains " + bookCount + " books. You must delete or move the books before deleting the library.");
            return;
        }
        
        boolean confirmed = DialogUtil.showConfirmation("Confirm Delete", "Delete Library", 
            "Are you sure you want to delete the library: " + biblio.getName() + "?");
        
        if (confirmed) {
            boolean success = biblioService.deleteBiblio(biblio.getId());
            
            if (success) {
                loadBiblios();
                DialogUtil.showInformation("Success", "Library Deleted", 
                    "The library has been deleted successfully.");
            } else {
                DialogUtil.showError("Error", "Delete Failed", 
                    "Failed to delete the library. Please try again.");
            }
        }
    }
    
    private void showBookView(Biblio biblio) {
        try {
            // Get the main application's StackPane
            StackPane contentPane = (StackPane) biblioTable.getScene().getRoot().lookup("#contentPane");
            
            if (contentPane != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookView.fxml"));
                Parent view = loader.load();
                
                BookController controller = loader.getController();
                controller.setBiblio(biblio);
                
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
                "Error loading the books view: " + e.getMessage());
        }
    }
    
    public void refreshTable() {
        loadBiblios();
    }
}
