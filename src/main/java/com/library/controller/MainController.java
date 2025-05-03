package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML
    private StackPane contentPane;
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Button logoutButton;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if user is logged in
        if (!UserService.isAuthenticated()) {
            // If not logged in, redirect to login page
            Platform.runLater(this::redirectToLogin);
            return;
        }
        
        // Display current user name
        User currentUser = UserService.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText("Welcome, " + currentUser.getFullName() + 
                (UserService.isAdmin() ? " (Admin)" : ""));
        }
        
        // Load the default view (Biblio View)
        showBiblioPage();
        
        // Set full screen after a short delay to ensure the scene is ready
        Platform.runLater(() -> {
            if (contentPane.getScene() != null && contentPane.getScene().getWindow() != null) {
                Stage stage = (Stage) contentPane.getScene().getWindow();
                stage.setMaximized(true);
            }
        });
    }
    
    @FXML
    private void handleLogout() {
        // Clear the current user session
        UserService.logout();
        
        // Redirect to login page
        redirectToLogin();
    }
    
    private void redirectToLogin() {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/LoginView.fxml"));
            Parent loginView = loader.load();
            
            // Get the current stage
            if (contentPane.getScene() == null) return;
            Stage stage = (Stage) contentPane.getScene().getWindow();
            
            // Set the login scene
            Scene scene = new Scene(loginView);
            scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Library Management System - Login");
            stage.setMaximized(false);
            stage.setFullScreen(false);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login view: " + e.getMessage());
        }
    }
    
    @FXML
    private void showBiblioPage() {
        loadView("/com/library/view/BiblioView.fxml");
    }
    
    @FXML
    private void showAllBooksPage() {
        // Load the book view for all books
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookView.fxml"));
            Parent view = loader.load();
            
            BookController controller = loader.getController();
            controller.setShowAllBooks(true);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading all books view: " + e.getMessage());
        }
    }
    
    @FXML
    private void showAllHistoryPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/HistoryView.fxml"));
            Parent view = loader.load();
            
            HistoryController controller = loader.getController();
            controller.setShowAllHistory(true);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading all history view: " + e.getMessage());
        }
    }
    
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading view " + fxmlPath + ": " + e.getMessage());
        }
    }
}
