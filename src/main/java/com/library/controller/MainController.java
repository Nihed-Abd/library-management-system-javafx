package com.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML
    private StackPane contentPane;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Show Biblio page by default when application starts
        showBiblioPage();
    }
    
    @FXML
    private void showBiblioPage() {
        loadView("/com/library/view/BiblioView.fxml");
    }
    
    @FXML
    private void showAllBooksPage() {
        // Load the book view without a specific biblio ID
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/library/view/BookView.fxml"));
            Parent view = loader.load();
            
            BookController controller = loader.getController();
            controller.setShowAllBooks(true);
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showAllHistoryPage() {
        loadView("/com/library/view/HistoryView.fxml");
    }
    
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
