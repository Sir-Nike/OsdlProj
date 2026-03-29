package com.shreeniketh.hotelmanagement.controller;

import com.shreeniketh.hotelmanagement.model.Customer;
import com.shreeniketh.hotelmanagement.service.HotelService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

@SuppressWarnings("unused")
public class CustomersController {
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, String> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;
    @FXML
    private TableColumn<Customer, Integer> roomNoColumn;
    @FXML
    private TableColumn<Customer, Integer> nightsBoughtColumn;
    @FXML
    private TableColumn<Customer, Boolean> checkedInColumn;
    @FXML
    private TableColumn<Customer, Boolean> checkedOutColumn;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField customerIdField;
    @FXML
    private ComboBox<Integer> roomNoChoiceBox;
    @FXML
    private TextField nightsField;
    @FXML
    private Label customerIdPreviewLabel;
    @FXML
    private Button createCustomerButton;
    @FXML
    private Button checkInCustomerButton;

    private final HotelService service = new HotelService();

    @FXML
    private void initialize() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        roomNoColumn.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        nightsBoughtColumn.setCellValueFactory(new PropertyValueFactory<>("nightsBought"));
        checkedInColumn.setCellValueFactory(new PropertyValueFactory<>("checkedIn"));
        checkedOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkedOut"));
        customerIdField.setEditable(false);
        customerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateActionStates());
        customerNameField.textProperty().addListener((observable, oldValue, newValue) -> updateActionStates());
        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> updateActionStates());
        nightsField.textProperty().addListener((observable, oldValue, newValue) -> updateActionStates());
        roomNoChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> updateActionStates());
        refreshRoomOptions();
        refreshNextCustomerId();
        refreshCustomers();
        updateActionStates();
    }

    @FXML
    private void createCustomer() {
        try {
            Integer roomNo = roomNoChoiceBox.getValue();
            if (roomNo == null) {
                throw new IllegalArgumentException("Select an available room.");
            }
            int nights = Integer.parseInt(nightsField.getText().trim());
            String customerId = service.createCustomer(customerNameField.getText(), phoneNumberField.getText(), roomNo, nights);
            customerIdField.setText(customerId);
            clearFields();
            refreshRoomOptions();
            refreshNextCustomerId();
            refreshCustomers();
        } catch (NumberFormatException exception) {
            showAlert(Alert.AlertType.ERROR, "Invalid input", "Room number and nights must be numeric.");
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Cannot create customer", exception.getMessage());
        }
    }

    @FXML
    private void checkInCustomer() {
        try {
            service.checkInCustomer(requireSelectedCustomer().getCustomerId());
            refreshRoomOptions();
            refreshCustomers();
            updateActionStates();
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Cannot check in", exception.getMessage());
        }
    }

    @FXML
    private void checkOutCustomer() {
        showAlert(Alert.AlertType.INFORMATION, "Checkout moved", "Checkout is now handled in Billing.");
    }

    @FXML
    private void refreshCustomers() {
        customerTable.setItems(FXCollections.observableArrayList(service.listCustomers()));
        updateActionStates();
    }

    @FXML
    private void refreshRoomOptions() {
        roomNoChoiceBox.setItems(FXCollections.observableArrayList(service.listAvailableRoomNumbers()));
        if (!roomNoChoiceBox.getItems().isEmpty()) {
            roomNoChoiceBox.setValue(roomNoChoiceBox.getItems().get(0));
        } else {
            roomNoChoiceBox.setValue(null);
        }
        updateActionStates();
    }

    @FXML
    private void refreshNextCustomerId() {
        customerIdField.setText(service.nextCustomerId());
        if (customerIdPreviewLabel != null) {
            customerIdPreviewLabel.setText("Next ID: " + customerIdField.getText());
        }
    }

    private void clearFields() {
        customerNameField.clear();
        phoneNumberField.clear();
        nightsField.clear();
        refreshNextCustomerId();
        updateActionStates();
    }

    private Customer requireSelectedCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            throw new IllegalArgumentException("Select a customer row first.");
        }
        return selectedCustomer;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateActionStates() {
        if (createCustomerButton != null) {
            boolean hasRoom = roomNoChoiceBox.getValue() != null;
            boolean hasFields = !customerNameField.getText().isBlank()
                    && !phoneNumberField.getText().isBlank()
                    && !nightsField.getText().isBlank();
            createCustomerButton.setDisable(!hasRoom || !hasFields);
        }

        if (checkInCustomerButton != null) {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            boolean canCheckIn = selectedCustomer != null && !selectedCustomer.getCheckedIn() && !selectedCustomer.getCheckedOut();
            checkInCustomerButton.setDisable(!canCheckIn);
        }
    }
}