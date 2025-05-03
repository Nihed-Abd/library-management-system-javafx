package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
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
import java.time.LocalDate;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField fullNameField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private VBox formContainer;
    
    private final UserService userService = new UserService();
    
    // Email validation regex pattern
    private final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        
        // Add animations to enhance the UI experience
        Platform.runLater(() -> {
            // Fade in animation for the form
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), formContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            // Add animation to the register button when hovered
            registerButton.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), registerButton);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            });
            
            registerButton.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), registerButton);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            });
        });
    }
    
    @FXML
    private void handleRegister() {
        // Clear previous errors
        errorLabel.setVisible(false);
        
        // Get input values
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        
        // Validate input
        boolean valid = validateInput(username, password, confirmPassword, email, fullName);
        
        if (valid) {
            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);  // Service will hash the password
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            newUser.setAdmin(false);  // Default to regular user
            newUser.setDateCreated(LocalDate.now());
            
            // Attempt to register
            boolean success = userService.register(newUser);
            
            if (success) {
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText("Welcome to Library Management System");
                alert.setContentText("Your account has been created successfully. You can now log in with your credentials.");
                alert.showAndWait();
                
                // Navigate to login
                try {
                    navigateToLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("An error occurred while redirecting to the login page.");
                }
            } else {
                showError("Registration failed. Username may already exist.");
            }
        }
    }
    
    private boolean validateInput(String username, String password, String confirmPassword, String email, String fullName) {
        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || fullName.isEmpty()) {
            showError("All fields are required.");
            return false;
        }
        
        // Check username length
        if (username.length() < 3 || username.length() > 50) {
            showError("Username must be between 3 and 50 characters.");
            return false;
        }
        
        // Check password length
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return false;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return false;
        }
        
        // Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Please enter a valid email address.");
            return false;
        }
        
        // Check full name length
        if (fullName.length() < 2 || fullName.length() > 100) {
            showError("Full name must be between 2 and 100 characters.");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void navigateToLogin() throws IOException {
        // Load the login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/LoginView.fxml"));
        Parent loginView = loader.load();
        
        // Get the current stage
        Stage stage = (Stage) usernameField.getScene().getWindow();
        
        // Set the login scene
        Scene scene = new Scene(loginView);
        scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Library Management System - Login");
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
