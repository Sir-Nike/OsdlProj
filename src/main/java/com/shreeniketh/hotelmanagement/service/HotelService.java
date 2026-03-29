package com.shreeniketh.hotelmanagement.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.shreeniketh.hotelmanagement.model.Bill;
import com.shreeniketh.hotelmanagement.model.Customer;
import com.shreeniketh.hotelmanagement.model.Room;
import com.shreeniketh.hotelmanagement.repository.BillRepository;
import com.shreeniketh.hotelmanagement.repository.CustomerRepository;
import com.shreeniketh.hotelmanagement.repository.RoomRepository;

public class HotelService {
    public static final List<String> ROOM_TYPES = List.of(
            "Single bed",
            "Double bed",
            "Triple bed",
            "Deluxe",
            "Suite",
            "Penthouse",
            "Presidential suite"
    );

    private static final DateTimeFormatter BILL_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RoomRepository roomRepository = new RoomRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final BillRepository billRepository = new BillRepository();

    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    public List<Integer> listAvailableRoomNumbers() {
        return roomRepository.findAvailableRoomNumbers();
    }

    public List<String> listRoomTypes() {
        return ROOM_TYPES;
    }

    public void addRoom(int roomNo, String roomType, double pricePerDay, boolean available) {
        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException("Room type is required.");
        }
        if (pricePerDay <= 0) {
            throw new IllegalArgumentException("Price per day must be greater than zero.");
        }
        if (roomRepository.findByRoomNo(roomNo).isPresent()) {
            throw new IllegalArgumentException("That room number already exists.");
        }

        roomRepository.save(new Room(roomNo, roomType.trim(), pricePerDay, available));
    }

    public void removeRoom(int roomNo) {
        Room room = roomRepository.findByRoomNo(roomNo).orElseThrow(() -> new IllegalArgumentException("Room not found."));
        if (!room.getAvailable()) {
            throw new IllegalArgumentException("Only available rooms can be removed.");
        }
        roomRepository.deleteByRoomNo(roomNo);
    }

    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> listActiveCustomers() {
        return customerRepository.findActiveCustomers();
    }

    public String nextCustomerId() {
        return customerRepository.nextCustomerId();
    }

    public String createCustomer(String customerName, String phoneNumber, int roomNo, int nightsBought) {
        validateCustomerInput(customerName, phoneNumber, roomNo, nightsBought);
        roomRepository.findByRoomNo(roomNo).orElseThrow(() -> new IllegalArgumentException("Chosen room does not exist."));

        String customerId = nextCustomerId();
        customerRepository.save(new Customer(customerId, customerName.trim(), phoneNumber.trim(), roomNo, nightsBought, false, false));
        return customerId;
    }

    public void checkInCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        if (customer.getCheckedIn()) {
            throw new IllegalArgumentException("Customer is already checked in.");
        }
        if (customer.getCheckedOut()) {
            throw new IllegalArgumentException("Customer has already checked out.");
        }

        Room room = roomRepository.findByRoomNo(customer.getRoomNo()).orElseThrow(() -> new IllegalArgumentException("Room not found."));
        if (!room.getAvailable()) {
            throw new IllegalArgumentException("Selected room is not available.");
        }

        roomRepository.updateAvailability(room.getRoomNo(), false);
        customerRepository.updateCheckInStatus(customerId, true);
    }

    public Bill checkOutCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        if (!customer.getCheckedIn()) {
            throw new IllegalArgumentException("Customer must be checked in before checkout.");
        }
        if (customer.getCheckedOut()) {
            throw new IllegalArgumentException("Bill has already been produced for this customer.");
        }

        Room room = roomRepository.findByRoomNo(customer.getRoomNo()).orElseThrow(() -> new IllegalArgumentException("Room not found."));
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

        Bill savedBill = billRepository.save(bill);
        customerRepository.updateCheckOutStatus(customerId, true);
        roomRepository.updateAvailability(room.getRoomNo(), true);
        return savedBill;
    }

    public List<Bill> listBills() {
        return billRepository.findAll();
    }

    public void clearAllData() {
        billRepository.deleteAll();
        customerRepository.deleteAll();
        roomRepository.deleteAll();
    }

    private void validateCustomerInput(String customerName, String phoneNumber, int roomNo, int nightsBought) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (roomNo <= 0) {
            throw new IllegalArgumentException("Room number must be greater than zero.");
        }
        if (nightsBought <= 0) {
            throw new IllegalArgumentException("Nights bought must be greater than zero.");
        }
    }
}