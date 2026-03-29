package com.shreeniketh.hotelmanagement.controller;

import com.shreeniketh.hotelmanagement.model.Room;
import com.shreeniketh.hotelmanagement.service.HotelService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@SuppressWarnings("unused")
public class RoomsController {
    @FXML
    private TableView<Room> roomTable;
    @FXML
    private TableColumn<Room, Integer> roomNoColumn;
    @FXML
    private TableColumn<Room, String> roomTypeColumn;
    @FXML
    private TableColumn<Room, Double> pricePerDayColumn;
    @FXML
    private TableColumn<Room, String> availabilityColumn;
    @FXML
    private TextField roomNoField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> roomTypeChoiceBox;

    private final HotelService service = new HotelService();

    @FXML
    private void initialize() {
        roomNoColumn.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        pricePerDayColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availabilityStatus"));
        roomTypeChoiceBox.setItems(FXCollections.observableArrayList(service.listRoomTypes()));
        roomTypeChoiceBox.setValue(service.listRoomTypes().get(0));
        refreshRooms();
    }

    @FXML
    private void addRoom() {
        try {
            int roomNo = Integer.parseInt(roomNoField.getText().trim());
            double pricePerDay = Double.parseDouble(priceField.getText().trim());
            service.addRoom(roomNo, roomTypeChoiceBox.getValue(), pricePerDay, true);
            clearFields();
            refreshRooms();
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Invalid input", "Room number and price must be numeric.");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Cannot add room", exception.getMessage());
        }
    }

    @FXML
    private void removeRoom() {
        try {
            int roomNo = Integer.parseInt(roomNoField.getText().trim());
            service.removeRoom(roomNo);
            clearFields();
            refreshRooms();
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Invalid input", "Room number must be numeric.");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Cannot remove room", exception.getMessage());
        }
    }

    @FXML
    private void refreshRooms() {
        roomTable.setItems(FXCollections.observableArrayList(service.listRooms()));
    }

    private void clearFields() {
        roomNoField.clear();
        priceField.clear();
        roomTypeChoiceBox.setValue(service.listRoomTypes().get(0));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}