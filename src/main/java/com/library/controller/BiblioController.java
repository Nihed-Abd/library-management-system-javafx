package com.library.controller;

import com.library.model.Biblio;
import com.library.service.BiblioService;
import com.library.util.DialogUtil;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class BiblioController implements Initializable {

    @FXML
    private TableView<Biblio> biblioTableView;
    
    @FXML
    private TableColumn<Biblio, Integer> idColumn;
    
    @FXML
    private TableColumn<Biblio, String> nameColumn;
    
    @FXML
    private TableColumn<Biblio, String> locationColumn;
    
    @FXML
    private TableColumn<Biblio, String> dateCreationColumn;
    
    @FXML
    private TableColumn<Biblio, String> descriptionColumn;
    
    @FXML
    private TableColumn<Biblio, Integer> bookCountColumn;
    
    @FXML
    private TableColumn<Biblio, Void> actionsColumn;
    
    private final BiblioService biblioService = new BiblioService();
    private final ObservableList<Biblio> biblioList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTable();
        loadBiblios();
    }
    
    private void configureTable() {
        // Configure the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        dateCreationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateCreation().format(dateFormatter)));
        
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        bookCountColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(biblioService.getBookCountByBiblioId(
                cellData.getValue().getId())).asObject());
        
        // Configure the actions column
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        Callback<TableColumn<Biblio, Void>, TableCell<Biblio, Void>> cellFactory = 
            param -> new TableCell<>() {
                private final Button detailsButton = new Button("Details");
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                private final HBox pane = new HBox(5, detailsButton, editButton, deleteButton);
                
                {
                    detailsButton.getStyleClass().add("button-details");
                    editButton.getStyleClass().add("button-edit");
                    deleteButton.getStyleClass().add("button-delete");
                    
                    detailsButton.setOnAction(event -> {
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
                    setGraphic(empty ? null : pane);
                }
            };
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    private void loadBiblios() {
        biblioList.clear();
        biblioList.addAll(biblioService.getAllBiblios());
        biblioTableView.setItems(biblioList);
    }
    
    @FXML
    private void showAddBiblioDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioDialogView.fxml"));
            Parent root = loader.load();
            
            BiblioDialogController controller = loader.getController();
            controller.setAddMode(true);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Library");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(biblioTableView.getScene().getWindow());
            
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BiblioDialogView.fxml"));
            Parent root = loader.load();
            
            BiblioDialogController controller = loader.getController();
            controller.setAddMode(false);
            controller.setBiblio(biblio);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Library");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(biblioTableView.getScene().getWindow());
            
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
            StackPane contentPane = (StackPane) biblioTableView.getScene().getRoot().lookup("#contentPane");
            
            if (contentPane != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookView.fxml"));
                Parent view = loader.load();
                
                BookController controller = loader.getController();
                controller.setBiblio(biblio);
                
                contentPane.getChildren().clear();
                contentPane.getChildren().add(view);
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
