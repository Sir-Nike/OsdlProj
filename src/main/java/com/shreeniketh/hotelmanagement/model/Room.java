package com.shreeniketh.hotelmanagement.model;

public class Room {
    public static final String AVAILABLE_STATUS = "Available";
    public static final String BOOKED_STATUS = "Booked";
    public static final String OCCUPIED_STATUS = "Occupied";
    public static final String MAINTENANCE_STATUS = "Maintenance";

    private final int roomNo;
    private final String roomType;
    private final double pricePerDay;
    private final String status;

    public Room(int roomNo, String roomType, double pricePerDay, String status) {
        this.roomNo = roomNo;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.status = normalizeStatus(status);
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
        return AVAILABLE_STATUS.equals(status);
    }

    public String getStatus() {
        return status;
    }

    public String getAvailabilityStatus() {
        return status;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return AVAILABLE_STATUS;
        }
        return status.trim();
    }
}