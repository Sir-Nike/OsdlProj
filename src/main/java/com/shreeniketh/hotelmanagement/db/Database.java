package com.shreeniketh.hotelmanagement.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private static final Path DATABASE_FILE = Path.of(System.getProperty("user.dir"), "hotel-management.db");
    private static final String JDBC_URL = "jdbc:sqlite:" + DATABASE_FILE.toAbsolutePath().toString().replace('\\', '/');
    private static boolean initialized;

    private Database() {
    }

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
                        available INTEGER NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS customers (
                        customer_id TEXT PRIMARY KEY,
                        customer_name TEXT NOT NULL,
                        phone_number TEXT NOT NULL,
                        room_no INTEGER NOT NULL,
                        nights_bought INTEGER NOT NULL,
                        checked_in INTEGER NOT NULL,
                        checked_out INTEGER NOT NULL,
                        FOREIGN KEY (room_no) REFERENCES rooms(room_no)
                    )
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS bills (
                        bill_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        customer_id TEXT NOT NULL,
                        customer_name TEXT NOT NULL,
                        room_no INTEGER NOT NULL,
                        room_type TEXT NOT NULL,
                        nights_bought INTEGER NOT NULL,
                        price_per_day REAL NOT NULL,
                        total_amount REAL NOT NULL,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
                    )
                    """);

            seedRooms(statement);
            initialized = true;
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to initialize database", exception);
        }
    }

    private static void seedRooms(Statement statement) throws SQLException {
        try (var resultSet = statement.executeQuery("SELECT COUNT(*) AS room_count FROM rooms")) {
            if (resultSet.next() && resultSet.getInt("room_count") == 0) {
                statement.executeUpdate("INSERT INTO rooms(room_no, room_type, price_per_day, available) VALUES (101, 'Standard', 1200.0, 1)");
                statement.executeUpdate("INSERT INTO rooms(room_no, room_type, price_per_day, available) VALUES (102, 'Deluxe', 1800.0, 1)");
                statement.executeUpdate("INSERT INTO rooms(room_no, room_type, price_per_day, available) VALUES (201, 'Suite', 2500.0, 1)");
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
}