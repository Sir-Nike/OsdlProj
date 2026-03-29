package com.shreeniketh.hotelmanagement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shreeniketh.hotelmanagement.db.Database;
import com.shreeniketh.hotelmanagement.model.Customer;

public class CustomerRepository {
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT customer_id, customer_name, phone_number, room_no, nights_bought, checked_in, checked_out FROM customers WHERE checked_out = 0 ORDER BY customer_name"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(map(resultSet));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to load customers", exception);
        }
        return customers;
    }

    public String nextCustomerId() {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT MAX(CAST(SUBSTR(customer_id, 2) AS INTEGER)) AS max_customer_id FROM customers WHERE customer_id LIKE 'C%'"); ResultSet resultSet = statement.executeQuery()) {
            int nextId = 1;
            if (resultSet.next()) {
                int currentMax = resultSet.getInt("max_customer_id");
                if (!resultSet.wasNull()) {
                    nextId = currentMax + 1;
                }
            }
            return String.format("C%03d", nextId);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to generate next customer ID", exception);
        }
    }

    public Optional<Customer> findById(String customerId) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT customer_id, customer_name, phone_number, room_no, nights_bought, checked_in, checked_out FROM customers WHERE customer_id = ?")) {
            statement.setString(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(map(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to find customer", exception);
        }
        return Optional.empty();
    }

    public void save(Customer customer) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO customers(customer_id, customer_name, phone_number, room_no, nights_bought, checked_in, checked_out) VALUES (?, ?, ?, ?, ?, ?, ?)");) {
            statement.setString(1, customer.getCustomerId());
            statement.setString(2, customer.getCustomerName());
            statement.setString(3, customer.getPhoneNumber());
            statement.setInt(4, customer.getRoomNo());
            statement.setInt(5, customer.getNightsBought());
            statement.setInt(6, customer.getCheckedIn() ? 1 : 0);
            statement.setInt(7, customer.getCheckedOut() ? 1 : 0);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to save customer", exception);
        }
    }

    public void updateCheckInStatus(String customerId, boolean checkedIn) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE customers SET checked_in = ? WHERE customer_id = ?")) {
            statement.setInt(1, checkedIn ? 1 : 0);
            statement.setString(2, customerId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update customer check-in status", exception);
        }
    }

    public void updateCheckOutStatus(String customerId, boolean checkedOut) {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE customers SET checked_out = ?, checked_in = 0 WHERE customer_id = ?")) {
            statement.setInt(1, checkedOut ? 1 : 0);
            statement.setString(2, customerId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to update customer check-out status", exception);
        }
    }

    public void deleteAll() {
        try (Connection connection = Database.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM customers")) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to clear customers", exception);
        }
    }

    public List<Customer> findActiveCustomers() {
        return findAll();
    }

    private Customer map(ResultSet resultSet) throws SQLException {
        return new Customer(
                resultSet.getString("customer_id"),
                resultSet.getString("customer_name"),
                resultSet.getString("phone_number"),
                resultSet.getInt("room_no"),
                resultSet.getInt("nights_bought"),
                resultSet.getInt("checked_in") == 1,
                resultSet.getInt("checked_out") == 1
        );
    }
}