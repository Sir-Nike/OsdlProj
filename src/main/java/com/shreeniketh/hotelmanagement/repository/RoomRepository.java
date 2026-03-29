package com.shreeniketh.hotelmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shreeniketh.hotelmanagement.db.Database;
import com.shreeniketh.hotelmanagement.model.Room;

public class RoomRepository {
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT room_no, room_type, price_per_day, available FROM rooms ORDER BY room_no"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rooms.add(new Room(
                        resultSet.getInt("room_no"),
                        resultSet.getString("room_type"),
                        resultSet.getDouble("price_per_day"),
                        resultSet.getInt("available") == 1
                ));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load rooms", exception);
        }
        return rooms;
    }

    public List<Integer> findAvailableRoomNumbers() {
        List<Integer> roomNumbers = new ArrayList<>();
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT room_no FROM rooms WHERE available = 1 ORDER BY room_no"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                roomNumbers.add(resultSet.getInt("room_no"));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load available rooms", exception);
        }
        return roomNumbers;
    }

    public Optional<Room> findByRoomNo(int roomNo) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT room_no, room_type, price_per_day, available FROM rooms WHERE room_no = ?")) {
            statement.setInt(1, roomNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Room(
                            resultSet.getInt("room_no"),
                            resultSet.getString("room_type"),
                            resultSet.getDouble("price_per_day"),
                            resultSet.getInt("available") == 1
                    ));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to find room", exception);
        }
        return Optional.empty();
    }

    public void save(Room room) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rooms(room_no, room_type, price_per_day, available) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, room.getRoomNo());
            statement.setString(2, room.getRoomType());
            statement.setDouble(3, room.getPricePerDay());
            statement.setInt(4, room.getAvailable() ? 1 : 0);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save room", exception);
        }
    }

    public void deleteByRoomNo(int roomNo) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE room_no = ?")) {
            statement.setInt(1, roomNo);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to delete room", exception);
        }
    }

    public void updateAvailability(int roomNo, boolean available) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET available = ? WHERE room_no = ?")) {
            statement.setInt(1, available ? 1 : 0);
            statement.setInt(2, roomNo);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update room availability", exception);
        }
    }

    public void deleteAll() {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms")) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to clear rooms", exception);
        }
    }
}