package com.shreeniketh.hotelmanagement;

import com.shreeniketh.hotelmanagement.db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HotelManagementApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database.initialize();

        FXMLLoader loader = new FXMLLoader(HotelManagementApp.class.getResource("/com/shreeniketh/hotelmanagement/view/main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(HotelManagementApp.class.getResource("/com/shreeniketh/hotelmanagement/styles/app.css").toExternalForm());

        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}