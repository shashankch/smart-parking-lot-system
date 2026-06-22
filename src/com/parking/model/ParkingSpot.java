package com.parking.model;

import com.parking.model.enums.SpotType;
import java.util.concurrent.atomic.AtomicReference;

public class ParkingSpot {

    private final int id;

    private final int floorNo;

    private final SpotType type;

    private final AtomicReference<String> parkedVehicleNumber;

    public ParkingSpot(int id, int floorNo, SpotType type) {

        this.id = id;

        this.floorNo = floorNo;

        this.type = type;

        this.parkedVehicleNumber = new AtomicReference<>(null);

    }

    public boolean assignVehicle(String vehicleNumber) {

        return parkedVehicleNumber.compareAndSet(null, vehicleNumber);

    }

    public void removeVehicle() {

        parkedVehicleNumber.set(null);

    }

    public boolean isOccupied() {

        return parkedVehicleNumber.get() != null;

    }

    public SpotType getType() {

        return type;

    }

    public int getId() {

        return id;

    }

    public int getFloorNo() {

        return floorNo;

    }

    public String getParkedVehicleNumber() {

        return parkedVehicleNumber.get();

    }

}
