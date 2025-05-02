package com.library.controller;

import com.library.model.Biblio;
import com.library.service.BiblioService;
import com.library.util.DialogUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BiblioDialogController implements Initializable {

    @FXML
    private TextField nameField;
    
    @FXML
    private TextField locationField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Label previewName;
    
    @FXML
    private Label previewLocation;
    
    @FXML
    private Label previewDescription;
    
    @FXML
    private Button saveButton;
    
    private Stage dialogStage;
    private Biblio biblio;
    private boolean addMode = true;
    private BiblioController biblioController;
    private final BiblioService biblioService = new BiblioService();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the biblio object
        biblio = new Biblio();
        
        // Add listeners to update preview in real-time
        setupLivePreview();
        
        // Validate input
        setupValidation();
    }
    
    private void setupLivePreview() {
        // Update preview when name changes
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            previewName.setText(newValue);
        });
        
        // Update preview when location changes
        locationField.textProperty().addListener((observable, oldValue, newValue) -> {
            previewLocation.setText(newValue);
        });
        
        // Update preview when description changes
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            previewDescription.setText(newValue);
        });
    }
    
    private void setupValidation() {
        // Disable save button if required fields are empty
        ChangeListener<String> validator = (observable, oldValue, newValue) -> {
            boolean nameValid = !nameField.getText().trim().isEmpty();
            boolean locationValid = !locationField.getText().trim().isEmpty();
            
            saveButton.setDisable(!nameValid || !locationValid);
        };
        
        nameField.textProperty().addListener(validator);
        locationField.textProperty().addListener(validator);
        
        // Initial validation
        saveButton.setDisable(true);
    }
    
    public void setBiblio(Biblio biblio) {
        this.biblio = biblio;
        
        // Set the fields with the biblio data
        nameField.setText(biblio.getName());
        locationField.setText(biblio.getLocation());
        descriptionArea.setText(biblio.getDescription());
        
        // Update preview
        previewName.setText(biblio.getName());
        previewLocation.setText(biblio.getLocation());
        previewDescription.setText(biblio.getDescription());
    }
    
    public void setAddMode(boolean addMode) {
        this.addMode = addMode;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setBiblioController(BiblioController biblioController) {
        this.biblioController = biblioController;
    }
    
    @FXML
    private void handleSave() {
        // Get values from fields
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        // Validate input
        if (name.isEmpty() || location.isEmpty()) {
            DialogUtil.showWarning("Validation Error", "Missing Required Fields", 
                "Please enter both name and location for the library.");
            return;
        }
        
        // Update biblio object
        biblio.setName(name);
        biblio.setLocation(location);
        biblio.setDescription(description);
        
        boolean success;
        if (addMode) {
            // Add new biblio
            success = biblioService.addBiblio(biblio);
        } else {
            // Update existing biblio
            success = biblioService.updateBiblio(biblio);
        }
        
        if (success) {
            // Refresh the table in the parent controller
            if (biblioController != null) {
                biblioController.refreshTable();
            }
            
            // Close the dialog
            dialogStage.close();
        } else {
            DialogUtil.showError("Database Error", "Operation Failed", 
                "Failed to " + (addMode ? "add" : "update") + " the library. Please try again.");
        }
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
