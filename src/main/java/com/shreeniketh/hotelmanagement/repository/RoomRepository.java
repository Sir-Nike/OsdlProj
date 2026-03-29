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
        try (Connection connection = Database.getConnection()) {
            return findAll(connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load rooms", exception);
        }
    }

    public List<Room> findAll(Connection connection) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT room_no, room_type, price_per_day, status FROM rooms ORDER BY room_no"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rooms.add(new Room(
                        resultSet.getInt("room_no"),
                        resultSet.getString("room_type"),
                        resultSet.getDouble("price_per_day"),
                        resultSet.getString("status")
                ));
            }
        }
        return rooms;
    }

    public List<Integer> findAvailableRoomNumbers() {
        List<Integer> roomNumbers = new ArrayList<>();
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT room_no FROM rooms WHERE status = ? ORDER BY room_no")) {
            statement.setString(1, Room.AVAILABLE_STATUS);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    roomNumbers.add(resultSet.getInt("room_no"));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load available rooms", exception);
        }
        return roomNumbers;
    }

    public Optional<Room> findByRoomNo(int roomNo) {
        try (Connection connection = Database.getConnection()) {
            return findByRoomNo(connection, roomNo);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to find room", exception);
        }
    }

    public Optional<Room> findByRoomNo(Connection connection, int roomNo) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT room_no, room_type, price_per_day, status FROM rooms WHERE room_no = ?")) {
            statement.setInt(1, roomNo);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Room(
                            resultSet.getInt("room_no"),
                            resultSet.getString("room_type"),
                            resultSet.getDouble("price_per_day"),
                            resultSet.getString("status")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void save(Room room) {
        try (Connection connection = Database.getConnection()) {
            save(connection, room);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save room", exception);
        }
    }

    public void save(Connection connection, Room room) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO rooms(room_no, room_type, price_per_day, status) VALUES (?, ?, ?, ?)") ) {
            statement.setInt(1, room.getRoomNo());
            statement.setString(2, room.getRoomType());
            statement.setDouble(3, room.getPricePerDay());
            statement.setString(4, room.getStatus());
            statement.executeUpdate();
        }
    }

    public void deleteByRoomNo(int roomNo) {
        try (Connection connection = Database.getConnection()) {
            deleteByRoomNo(connection, roomNo);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to delete room", exception);
        }
    }

    public void deleteByRoomNo(Connection connection, int roomNo) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE room_no = ?")) {
            statement.setInt(1, roomNo);
            statement.executeUpdate();
        }
    }

    public int updateStatus(int roomNo, String status) {
        try (Connection connection = Database.getConnection()) {
            return updateStatus(connection, roomNo, status);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update room status", exception);
        }
    }

    public int updateStatus(Connection connection, int roomNo, String status) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET status = ? WHERE room_no = ?")) {
            statement.setString(1, status);
            statement.setInt(2, roomNo);
            return statement.executeUpdate();
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