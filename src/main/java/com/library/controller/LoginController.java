package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import com.library.util.DialogUtil;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private VBox formContainer;
    
    private final UserService userService = new UserService();
    
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        
        // Add some subtle animations to enhance the UI experience
        Platform.runLater(() -> {
            // Fade in animation for the form
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), formContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            // Add animation to the login button when hovered
            loginButton.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginButton);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            });
            
            loginButton.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginButton);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            });
        });
    }
    
    @FXML
    private void handleLogin() {
        // Clear previous errors
        errorLabel.setVisible(false);
        
        // Get input values
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }
        
        // Attempt to authenticate
        User authenticatedUser = userService.authenticate(username, password);
        
        if (authenticatedUser != null) {
            try {
                // Load the main view
                navigateToMainView();
            } catch (Exception e) {
                e.printStackTrace();
                showError("An error occurred while loading the main screen.");
            }
        } else {
            // Show a proper dialog for invalid credentials
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Authentication Failed");
            alert.setHeaderText("Invalid Username or Password");
            alert.setContentText("The username and password combination you entered is not valid. Please try again.");
            alert.showAndWait();
            
            // Also show inline error for redundancy
            showError("Invalid username or password.");
            
            // Clear the password field for security
            passwordField.clear();
            
            // Focus on the username field
            usernameField.requestFocus();
        }
    }
    
    @FXML
    private void navigateToRegister() {
        try {
            // Load the registration view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/RegisterView.fxml"));
            Parent registerView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) usernameField.getScene().getWindow();
            
            // Set the register scene
            Scene scene = new Scene(registerView);
            scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Library Management System - Register");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("An error occurred while loading the registration screen.");
        }
    }
    
    private void navigateToMainView() throws IOException {
        // Load the main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/MainView.fxml"));
        Parent mainView = loader.load();
        
        // Get the current stage
        Stage stage = (Stage) usernameField.getScene().getWindow();
        
        // Set the main scene
        Scene scene = new Scene(mainView);
        scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management System");
        stage.setMaximized(true);
        stage.show();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        // Add animation to highlight the error
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(100), errorLabel);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();
    }
}
