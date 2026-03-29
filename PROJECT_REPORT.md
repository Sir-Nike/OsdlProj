# Hotel Management System Project Report

## 1. Project Overview

This project is a modular JavaFX hotel management system built with Maven, SQLite, FXML, and CSS. It supports room management, customer management, and billing generation from a single desktop interface.

The application starts by initializing the database, loading the main FXML layout, and applying the base stylesheet. The startup code is small, but it establishes the full app shell: database availability, UI loading, and styling in one place.

```java
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
}
```

## 2. Technology Stack

The application uses:

- JavaFX for the user interface
- FXML for declarative UI layout
- Scene Builder for visual layout editing
- JDBC with SQLite for data storage
- CSS for styling
- Maven for build and dependency management

## 3. Application Structure

The code is organized into clear layers:

- `controller` for JavaFX event handling
- `service` for business logic and transactions
- `repository` for database access
- `model` for data objects
- `db` for database initialization and connection handling

This separation keeps the UI, business logic, and persistence layers independent and easier to maintain.

## 4. FXML and Scene Builder

FXML is used to define the interface structure declaratively instead of building every control manually in Java. Scene Builder is the visual editor used to design that FXML layout, arrange controls, and connect them to controller methods.

The billing screen is a good example of that workflow. It is built from stacked containers and tables: a header section, a customer selection table, a bill generation action bar, and a billing history table.

The same structure appears directly in `billing-view.fxml`. The root `VBox` creates a vertical page layout, and each nested `VBox` groups a logical part of the screen:

```xml
<VBox spacing="14.0" xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.shreeniketh.hotelmanagement.controller.BillingController">
    <children>
        <VBox spacing="4.0" styleClass="screen-header">
            <children>
                <Label styleClass="screen-title" text="Billing Management" />
                <Label styleClass="helper-text" text="Select a customer, generate the bill here, and checkout happens at the same time." wrapText="true" />
            </children>
        </VBox>
        <VBox spacing="10.0" styleClass="surface-panel">
            <children>
                <Label styleClass="section-title" text="Select customer" />
                <TableView fx:id="customerTable" prefHeight="220.0">
                    <columns>
                        <TableColumn fx:id="customerIdColumn" prefWidth="120.0" text="Customer ID" />
                        <TableColumn fx:id="customerNameColumn" prefWidth="170.0" text="Name" />
                        <TableColumn fx:id="phoneNumberColumn" prefWidth="130.0" text="Phone" />
                    </columns>
                </TableView>
            </children>
        </VBox>
    </children>
</VBox>
```

The controller name is linked through `fx:controller`, and the buttons use `onAction` handlers such as `generateBill`, `refreshBills`, and `refreshCustomers`. In Scene Builder, that means the visual layout and the Java controller stay connected without hand-written UI construction code.

## 5. Billing Screen Walkthrough

The billing screen is designed to support checkout in one place. The user first selects an active customer, then generates a bill, and the system immediately marks the customer as checked out.

- The top panel describes the billing flow.
- The customer table shows active customers only.
- The action bar contains `Generate Bill`, `Refresh`, and `Reload Customers` buttons.
- The lower table displays billing history.

This layout is easy to build in Scene Builder because each region is a simple VBox or HBox, and the tables are inserted visually without manual pixel positioning. Scene Builder is useful here because the screen is naturally sectioned, so each panel can be assembled and adjusted visually before wiring it to the controller.

## 6. Controller Logic

The billing controller connects the table columns, refresh actions, and checkout behavior.

The `initialize()` method runs automatically after FXML loading. Its job is to map each table column to a property on the `Customer` or `Bill` model, then populate the tables immediately so the screen is ready when it appears.

```java
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
    refreshCustomers();
    refreshBills();
    updateActionStates();
}
```

The checkout action is handled in one method. It first checks whether a row is selected, then calls the service layer, then refreshes both tables so the screen stays consistent after checkout:

```java
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
```

The helper methods are straightforward but important. `requireSelectedCustomer()` prevents accidental checkout with no row selected, `updateActionStates()` disables the button unless the customer is checked in and not already checked out, and `showBillWindow()` formats a receipt-like popup for the user.

## 7. Business Logic and Transactions

The service layer handles core operations such as room booking, check-in, checkout, and bill generation. It also keeps each multi-step action in a database transaction, which means related database changes succeed or fail together.

The `checkOutCustomer()` method is the most important business rule in the billing flow. It verifies that the customer exists, checks that the customer has already checked in, blocks duplicate checkout, calculates the total amount from nights multiplied by room price, saves the bill, and finally releases the room back to `Available`.

```java
public Bill checkOutCustomer(String customerId) {
    return runInTransaction(connection -> {
        Customer customer = customerRepository.findById(connection, customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        if (!customer.getCheckedIn()) {
            throw new IllegalArgumentException("Customer must be checked in before checkout.");
        }
        if (customer.getCheckedOut()) {
            throw new IllegalArgumentException("Bill has already been produced for this customer.");
        }

        Room room = roomRepository.findByRoomNo(connection, customer.getRoomNo()).orElseThrow(() -> new IllegalArgumentException("Room not found."));
        double totalAmount = customer.getNightsBought() * room.getPricePerDay();
        Bill bill = new Bill(
                0,
                customer.getCustomerId(),
                customer.getCustomerName(),
                room.getRoomNo(),
                room.getRoomType(),
                customer.getNightsBought(),
                room.getPricePerDay(),
                totalAmount,
                LocalDateTime.now().format(BILL_TIME_FORMAT)
        );

        Bill savedBill = billRepository.save(connection, bill);
        customerRepository.updateCheckOutStatus(connection, customerId, true);
        roomRepository.updateStatus(connection, room.getRoomNo(), Room.AVAILABLE_STATUS);
        return savedBill;
    });
}
```

This method is the reason the billing screen is more than just a form. It coordinates three different records: the customer row, the room row, and the bill row.

## 8. Database Initialization

The database layer creates the SQLite file on first launch, enables foreign keys, creates the tables, and seeds sample room data. The `Database` class is responsible for making the app self-contained, so the user does not need to create the SQLite file manually.

The initialization flow is simple:

- Open or create the SQLite database file in the project root.
- Enable foreign-key checks.
- Create the `rooms`, `customers`, and `bills` tables if they do not already exist.
- Seed the app with a few rooms when the database is empty.

```java
public static synchronized void initialize() {
    if (initialized) {
        return;
    }

    try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
        statement.execute("PRAGMA foreign_keys = ON");
        statement.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    room_no INTEGER PRIMARY KEY,
                    room_type TEXT NOT NULL,
                    price_per_day REAL NOT NULL,
                    status TEXT NOT NULL
                )
                """);
        seedRooms(statement);
        initialized = true;
    } catch (SQLException exception) {
        throw new IllegalStateException("Unable to initialize database", exception);
    }
}
```

## 9. Module and Build Setup

The project is modular and uses JavaFX plus SQLite through Maven. The module descriptor keeps JavaFX controller access explicit, which is required because FXML uses reflection to inject controls and invoke handler methods.

```java
module com.shreeniketh.hotelmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;

    exports com.shreeniketh.hotelmanagement;
    exports com.shreeniketh.hotelmanagement.model;

    opens com.shreeniketh.hotelmanagement.controller to javafx.fxml;
}
```

The Maven configuration includes the JavaFX Windows classifiers and SQLite JDBC dependency. The JavaFX classifier is important on Windows because the UI libraries are platform-specific, while SQLite gives the application a lightweight local database with no external server setup.

## 10. Summary

This project demonstrates a clean JavaFX desktop application with a layered architecture. FXML and Scene Builder keep the UI layout readable and maintainable, while the service and repository layers handle the database-backed hotel workflows.

The code is easy to explain because each layer has one job: the FXML file defines structure, the controller handles user interaction, the service enforces rules, and the repositories talk to the database.