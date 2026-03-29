package com.shreeniketh.hotelmanagement.model;

public class Customer {
    private final String customerId;
    private final String customerName;
    private final String phoneNumber;
    private final int roomNo;
    private final int nightsBought;
    private final boolean checkedIn;
    private final boolean checkedOut;

    public Customer(String customerId, String customerName, String phoneNumber, int roomNo, int nightsBought, boolean checkedIn, boolean checkedOut) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.roomNo = roomNo;
        this.nightsBought = nightsBought;
        this.checkedIn = checkedIn;
        this.checkedOut = checkedOut;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public int getNightsBought() {
        return nightsBought;
    }

    public boolean getCheckedIn() {
        return checkedIn;
    }

    public boolean getCheckedOut() {
        return checkedOut;
    }
}