
package com.parking.model;

import com.parking.model.enums.SpotType;

public class ParkingSpot {

    private final int id;

    private final SpotType type;

    private boolean occupied;

    private String parkedVehicleNumber;

    public ParkingSpot(int id, SpotType type) {

        this.id = id;

        this.type = type;

        this.occupied = false;

    }

    public synchronized boolean assignVehicle(String parkedVehicleNumber) {

        if (occupied)
            return false;

        occupied = true;

        this.parkedVehicleNumber = parkedVehicleNumber;

        return true;

    }

    public synchronized void removeVehicle() {

        occupied = false;

        this.parkedVehicleNumber = null;

    }

    public synchronized boolean isOccupied() {

        return occupied;

    }

    public SpotType getType() {

        return type;

    }

    public int getId() {

        return id;

    }

    public synchronized String getParkedVehicleNumber() {
        return parkedVehicleNumber;
    }

}
