# Hotel Management System

Minimal JavaFX hotel management app built with Java 25, Maven, modular Java, and JDBC-backed persistence using SQLite.

## Features

- Room management: add and remove rooms
- Customer management: create customer, check in, check out
- Billing management: generate and store bills based on the number of nights bought before check-in
- Modular JavaFX UI using FXML, which can be opened and edited in Scene Builder

## Run

```bash
mvn javafx:run
```

The app creates a local database file named `hotel-management.db` in the project root on first launch.

## Notes

- The UI is intentionally simple and uses only a small amount of CSS.
- All main screens are defined in FXML and are suitable for Scene Builder.