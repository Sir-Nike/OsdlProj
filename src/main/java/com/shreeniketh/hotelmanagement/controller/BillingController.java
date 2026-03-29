package com.shreeniketh.hotelmanagement.controller;

import com.shreeniketh.hotelmanagement.model.Bill;
import com.shreeniketh.hotelmanagement.model.Customer;
import com.shreeniketh.hotelmanagement.service.HotelService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class BillingController {
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
    private Button generateBillButton;
    @FXML
    private TableView<Bill> billTable;
    @FXML
    private TableColumn<Bill, Long> billIdColumn;
    @FXML
    private TableColumn<Bill, String> billCustomerIdColumn;
    @FXML
    private TableColumn<Bill, String> billCustomerNameColumn;
    @FXML
    private TableColumn<Bill, Integer> billRoomNoColumn;
    @FXML
    private TableColumn<Bill, String> billRoomTypeColumn;
    @FXML
    private TableColumn<Bill, Integer> billNightsColumn;
    @FXML
    private TableColumn<Bill, Double> billPriceColumn;
    @FXML
    private TableColumn<Bill, Double> billTotalColumn;
    @FXML
    private TableColumn<Bill, String> billCreatedAtColumn;
    @FXML
    private Label revenueEarnedLabel;

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
        billIdColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        billCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        billCustomerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        billRoomNoColumn.setCellValueFactory(new PropertyValueFactory<>("roomNo"));
        billRoomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        billNightsColumn.setCellValueFactory(new PropertyValueFactory<>("nightsBought"));
        billPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        billTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        billCreatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        customerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateActionStates());
        refreshCustomers();
        refreshBills();
        updateActionStates();
        updateRevenueEarned();
    }

    @FXML
    private void generateBill() {
        try {
            Customer selectedCustomer = requireSelectedCustomer();
            Bill bill = service.checkOutCustomer(selectedCustomer.getCustomerId());
            showBillWindow(bill);
            customerTable.getSelectionModel().clearSelection();
            refreshCustomers();
            refreshBills();
            updateActionStates();
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Cannot generate bill", exception.getMessage());
        }
    }

    @FXML
    private void refreshCustomers() {
        customerTable.setItems(FXCollections.observableArrayList(service.listActiveCustomers()));
        updateActionStates();
    }

    @FXML
    private void refreshBills() {
        billTable.setItems(FXCollections.observableArrayList(service.listBills()));
        updateRevenueEarned();
    }

    private void showBillWindow(Bill bill) {
        VBox receipt = new VBox(10);
        receipt.setAlignment(Pos.TOP_CENTER);
        receipt.setPadding(new Insets(18));
        receipt.getStyleClass().add("surface-panel");
        receipt.setStyle("-fx-background-radius: 10;");

        Label title = new Label("HOTEL BILL");
        title.getStyleClass().add("screen-title");

        Label dividerTop = new Label("--------------------------------------");
        Label dividerBottom = new Label("--------------------------------------");
        dividerTop.getStyleClass().add("helper-text");
        dividerBottom.getStyleClass().add("helper-text");

        VBox fields = new VBox(6,
                receiptLabel("Bill No", String.valueOf(bill.getBillId())),
                receiptLabel("Customer", bill.getCustomerName()),
                receiptLabel("Customer ID", bill.getCustomerId()),
                receiptLabel("Room No", String.valueOf(bill.getRoomNo())),
                receiptLabel("Room Type", bill.getRoomType()),
                receiptLabel("Nights", String.valueOf(bill.getNightsBought())),
                receiptLabel("Price/Day", String.format("%.2f", bill.getPricePerDay())),
                receiptLabel("Total", String.format("%.2f", bill.getTotalAmount())),
                receiptLabel("Date", bill.getCreatedAt()));
        fields.setAlignment(Pos.TOP_LEFT);

        receipt.getChildren().addAll(title, dividerTop, fields, dividerBottom);

        Scene scene = new Scene(receipt, 320, 520);
        Scene ownerScene = customerTable.getScene();
        if (ownerScene != null) {
            scene.getStylesheets().addAll(ownerScene.getStylesheets());
        }

        Stage stage = new Stage();
        stage.initModality(Modality.NONE);
        stage.setTitle("Bill Receipt");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void updateActionStates() {
        if (generateBillButton != null) {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            boolean canGenerateBill = selectedCustomer != null && selectedCustomer.getCheckedIn() && !selectedCustomer.getCheckedOut();
            generateBillButton.setDisable(!canGenerateBill);
        }
    }

    private void updateRevenueEarned() {
        if (revenueEarnedLabel == null) {
            return;
        }

        double totalRevenue = service.listBills().stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
        revenueEarnedLabel.setText(String.format("Revenue Earned: %.2f", totalRevenue));
    }

    private Label receiptLabel(String label, String value) {
        Label receiptLabel = new Label(label + ": " + value);
        receiptLabel.getStyleClass().add("helper-text");
        receiptLabel.setWrapText(true);
        return receiptLabel;
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
}