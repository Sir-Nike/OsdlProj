package com.shreeniketh.hotelmanagement.controller;

import java.io.IOException;

import com.shreeniketh.hotelmanagement.service.HotelService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

@SuppressWarnings("unused")
public class MainController {
    private static final String LIGHT_THEME = "/com/shreeniketh/hotelmanagement/styles/app.css";
    private static final String DARK_THEME = "/com/shreeniketh/hotelmanagement/styles/dark.css";

    private final HotelService service = new HotelService();

    @FXML
    private StackPane contentPane;
    @FXML
    private Button themeToggleButton;
    @FXML
    private Button clearDataButton;

    private boolean darkMode;
    private String currentViewPath;

    @FXML
    private void initialize() {
        showRooms();
        Platform.runLater(this::applyThemeLabels);
    }

    @FXML
    private void showRooms() {
        currentViewPath = "/com/shreeniketh/hotelmanagement/view/rooms-view.fxml";
        loadView(currentViewPath);
    }

    @FXML
    private void showCustomers() {
        currentViewPath = "/com/shreeniketh/hotelmanagement/view/customers-view.fxml";
        loadView(currentViewPath);
    }

    @FXML
    private void showBilling() {
        currentViewPath = "/com/shreeniketh/hotelmanagement/view/billing-view.fxml";
        loadView(currentViewPath);
    }

    @FXML
    private void toggleTheme() {
        darkMode = !darkMode;
        Scene scene = contentPane.getScene();
        if (scene == null) {
            return;
        }

        scene.getStylesheets().removeIf(stylesheet -> stylesheet.endsWith("app.css") || stylesheet.endsWith("dark.css"));
        scene.getStylesheets().add(getClass().getResource(darkMode ? DARK_THEME : LIGHT_THEME).toExternalForm());
        applyThemeLabels();
    }

    @FXML
    private void clearAllData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear all data");
        alert.setHeaderText(null);
        alert.setContentText("Delete all rooms, customers, and billing data? This cannot be undone.");

        if (alert.showAndWait().filter(ButtonType.OK::equals).isEmpty()) {
            return;
        }

        service.clearAllData();
        if (currentViewPath != null) {
            loadView(currentViewPath);
        }
    }

    private void loadView(String resourcePath) {
        try {
            Node view = FXMLLoader.load(MainController.class.getResource(resourcePath));
            contentPane.getChildren().setAll(view);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load view: " + resourcePath, exception);
        }
    }

    private void applyThemeLabels() {
        if (themeToggleButton != null) {
            themeToggleButton.setText(darkMode ? "☀" : "☾");
        }
        if (clearDataButton != null) {
            clearDataButton.setText("🗑");
        }
    }
}