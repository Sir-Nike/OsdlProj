package com.shreeniketh.hotelmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.shreeniketh.hotelmanagement.db.Database;
import com.shreeniketh.hotelmanagement.model.Bill;

public class BillRepository {
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT bill_id, customer_id, customer_name, room_no, room_type, nights_bought, price_per_day, total_amount, created_at FROM bills ORDER BY bill_id DESC"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                bills.add(map(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load bills", exception);
        }
        return bills;
    }

    public Bill save(Bill bill) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO bills(customer_id, customer_name, room_no, room_type, nights_bought, price_per_day, total_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, bill.getCustomerId());
            statement.setString(2, bill.getCustomerName());
            statement.setInt(3, bill.getRoomNo());
            statement.setString(4, bill.getRoomType());
            statement.setInt(5, bill.getNightsBought());
            statement.setDouble(6, bill.getPricePerDay());
            statement.setDouble(7, bill.getTotalAmount());
            statement.setString(8, bill.getCreatedAt());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Bill(
                            generatedKeys.getLong(1),
                            bill.getCustomerId(),
                            bill.getCustomerName(),
                            bill.getRoomNo(),
                            bill.getRoomType(),
                            bill.getNightsBought(),
                            bill.getPricePerDay(),
                            bill.getTotalAmount(),
                            bill.getCreatedAt()
                    );
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save bill", exception);
        }
        return bill;
    }

    public void deleteAll() {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM bills")) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to clear bills", exception);
        }
    }

    private Bill map(ResultSet resultSet) throws SQLException {
        return new Bill(
                resultSet.getLong("bill_id"),
                resultSet.getString("customer_id"),
                resultSet.getString("customer_name"),
                resultSet.getInt("room_no"),
                resultSet.getString("room_type"),
                resultSet.getInt("nights_bought"),
                resultSet.getDouble("price_per_day"),
                resultSet.getDouble("total_amount"),
                resultSet.getString("created_at")
        );
    }
}