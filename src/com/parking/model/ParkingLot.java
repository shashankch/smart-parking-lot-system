package com.parking.model;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private static final ParkingLot INSTANCE = new ParkingLot();

    private final List<ParkingFloor> floors = new ArrayList<>();

    private ParkingLot() {
    }

    public static ParkingLot getInstance() {

        return INSTANCE;

    }

    public void addFloor(ParkingFloor floor) {

        floors.add(floor);

    }

    public List<ParkingFloor> getFloors() {

        return floors;

    }

    public synchronized void reset() {

        floors.clear();

    }

}
