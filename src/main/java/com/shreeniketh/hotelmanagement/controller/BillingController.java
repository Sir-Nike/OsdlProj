package com.shreeniketh.hotelmanagement.controller;

import com.shreeniketh.hotelmanagement.model.Bill;
import com.shreeniketh.hotelmanagement.model.Customer;
import com.shreeniketh.hotelmanagement.service.HotelService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TextArea billSummaryArea;
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
        billSummaryArea.setEditable(false);
        refreshCustomers();
        refreshBills();
    }

    @FXML
    private void generateBill() {
        try {
            Customer selectedCustomer = requireSelectedCustomer();
            Bill bill = service.checkOutCustomer(selectedCustomer.getCustomerId());
            billSummaryArea.setText(buildSummary(bill));
            customerTable.getSelectionModel().clearSelection();
            refreshCustomers();
            refreshBills();
        } catch (IllegalArgumentException exception) {
            showAlert(Alert.AlertType.ERROR, "Cannot generate bill", exception.getMessage());
        }
    }

    @FXML
    private void refreshCustomers() {
        customerTable.setItems(FXCollections.observableArrayList(service.listActiveCustomers()));
    }

    @FXML
    private void refreshBills() {
        billTable.setItems(FXCollections.observableArrayList(service.listBills()));
    }

    private String buildSummary(Bill bill) {
        String line = "--------------------------------------";
        return line + System.lineSeparator()
                + "           HOTEL BILL" + System.lineSeparator()
                + line + System.lineSeparator()
                + formatReceiptLine("Bill No", String.valueOf(bill.getBillId())) + System.lineSeparator()
                + formatReceiptLine("Customer", bill.getCustomerName()) + System.lineSeparator()
                + formatReceiptLine("Customer ID", bill.getCustomerId()) + System.lineSeparator()
                + formatReceiptLine("Room No", String.valueOf(bill.getRoomNo())) + System.lineSeparator()
                + formatReceiptLine("Room Type", bill.getRoomType()) + System.lineSeparator()
                + formatReceiptLine("Nights", String.valueOf(bill.getNightsBought())) + System.lineSeparator()
                + formatReceiptLine("Price/Day", String.format("%.2f", bill.getPricePerDay())) + System.lineSeparator()
                + formatReceiptLine("Total", String.format("%.2f", bill.getTotalAmount())) + System.lineSeparator()
                + formatReceiptLine("Date", bill.getCreatedAt()) + System.lineSeparator()
                + line;
    }

    private String formatReceiptLine(String label, String value) {
        return String.format("%-14s : %s", label, value);
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