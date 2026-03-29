package com.shreeniketh.hotelmanagement.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
                        status TEXT NOT NULL
                    )
                    """);
            ensureRoomStatusColumn(statement);
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

    private static void ensureRoomStatusColumn(Statement statement) throws SQLException {
        boolean hasStatusColumn = false;
        boolean hasAvailableColumn = false;

        try (ResultSet resultSet = statement.executeQuery("PRAGMA table_info(rooms)")) {
            while (resultSet.next()) {
                String columnName = resultSet.getString("name");
                if ("status".equalsIgnoreCase(columnName)) {
                    hasStatusColumn = true;
                }
                if ("available".equalsIgnoreCase(columnName)) {
                    hasAvailableColumn = true;
                }
            }
        }

        if (hasAvailableColumn) {
            // One-time migration for old schema where `available` is NOT NULL.
            // Rebuild the table so inserts only require the new `status` column.
            statement.execute("PRAGMA foreign_keys = OFF");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS rooms_new (room_no INTEGER PRIMARY KEY, room_type TEXT NOT NULL, price_per_day REAL NOT NULL, status TEXT NOT NULL)");
            statement.executeUpdate("""
                    INSERT INTO rooms_new(room_no, room_type, price_per_day, status)
                    SELECT room_no,
                           room_type,
                           price_per_day,
                           CASE
                               WHEN status IS NOT NULL AND TRIM(status) <> '' THEN status
                               WHEN available = 1 THEN 'Available'
                               ELSE 'Booked'
                           END
                    FROM rooms
                    """);
            statement.executeUpdate("DROP TABLE rooms");
            statement.executeUpdate("ALTER TABLE rooms_new RENAME TO rooms");
            statement.execute("PRAGMA foreign_keys = ON");
        } else if (!hasStatusColumn) {
            statement.executeUpdate("ALTER TABLE rooms ADD COLUMN status TEXT NOT NULL DEFAULT 'Available'");
        }

        statement.executeUpdate("UPDATE rooms SET room_type = 'Single bed' WHERE lower(room_type) = 'standard'");
    }

    private static void seedRooms(Statement statement) throws SQLException {
        try (var resultSet = statement.executeQuery("SELECT COUNT(*) AS room_count FROM rooms")) {
            if (resultSet.next() && resultSet.getInt("room_count") == 0) {
                statement.executeUpdate("INSERT INTO rooms(room_no, room_type, price_per_day, status) VALUES (101, 'Single bed', 1200.0, 'Available')");
                statement.executeUpdate("INSERT INTO rooms(room_no, room_type, price_per_day, status) VALUES (102, 'Deluxe', 1800.0, 'Available')");
                statement.executeUpdate("INSERT INTO rooms(room_no, room_type, price_per_day, status) VALUES (201, 'Suite', 2500.0, 'Available')");
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }
}