package com.shreeniketh.hotelmanagement.model;

public class Room {
    public static final String AVAILABLE_STATUS = "Available";
    public static final String BOOKED_STATUS = "Booked";

    private final int roomNo;
    private final String roomType;
    private final double pricePerDay;
    private final boolean available;

    public Room(int roomNo, String roomType, double pricePerDay, boolean available) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean getAvailable() {
        return available;
    }

    public String getAvailabilityStatus() {
        return available ? AVAILABLE_STATUS : BOOKED_STATUS;
    }
}