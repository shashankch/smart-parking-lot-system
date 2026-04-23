
package com.parking.model;

import java.time.LocalDateTime;

import com.parking.model.enums.VehicleType;

public class Ticket {

    private final String ticketId;

    private final Vehicle vehicle;

    private final ParkingSpot spot;

    private final LocalDateTime entryTime;

    private LocalDateTime exitTime;

    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot) {

        this.ticketId = ticketId;

        this.vehicle = vehicle;

        this.spot = spot;

        this.entryTime = LocalDateTime.now();

    }

    public String getTicketId() {

        return ticketId;

    }

    public String getVehicleNumber() {

        return vehicle.getNumber();

    }

    public VehicleType getVehicleType() {

        return vehicle.getType();

    }

    public ParkingSpot getSpot() {

        return spot;

    }

    public LocalDateTime getEntryTime() {

        return entryTime;

    }

    public LocalDateTime getExitTime() {

        return exitTime;

    }

    public void close() {
        this.exitTime = LocalDateTime.now();

    }
}
