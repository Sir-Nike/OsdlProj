package com.shreeniketh.hotelmanagement.model;

public class Bill {
    private final long billId;
    private final String customerId;
    private final String customerName;
    private final int roomNo;
    private final String roomType;
    private final int nightsBought;
    private final double pricePerDay;
    private final double totalAmount;
    private final String createdAt;

    public Bill(long billId, String customerId, String customerName, int roomNo, String roomType, int nightsBought, double pricePerDay, double totalAmount, String createdAt) {
        this.billId = billId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.nightsBought = nightsBought;
        this.pricePerDay = pricePerDay;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public long getBillId() {
        return billId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNightsBought() {
        return nightsBought;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}