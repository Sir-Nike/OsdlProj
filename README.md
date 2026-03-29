# Hotel Management System
 A hotel amangement system built using JavaFX, Scene Builder, JDBC (sqlite3), and CSS 

## Features

- Room management: Add and remove rooms, Drop down choice for types of rooms. Filter rooms by available and occupied. 
- Customer management: create customer,  automatically increases customer ID, drop down for room number
- Billing management: generate and store bills based on the number of nights bought before check-in
- Modular JavaFX UI using FXML, which can be opened and edited in Scene Builder

## Run

```bash
mvn javafx:run
```

The app creates a local database file named `hotel-management.db` in the project root on first launch.

## Notes

- This is a project for OSDL, a lab course in college.
- Used scene builder to make the general layout for the UI of the application
- used SQLite3 to store data to utilize the JDBC
- Maven for Javafx used to  build the project.
