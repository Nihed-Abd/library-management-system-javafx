package com.library.app;

import com.library.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LibraryApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the database
        DatabaseUtil.initializeDatabase();
        
        // Load the main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/MainView.fxml"));
        Parent root = loader.load();
        
        // Set up the primary stage
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/com/library/css/styles.css").toExternalForm());
        
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        
        // Set application icon (optional)
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/library/images/library_icon.png")));
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
