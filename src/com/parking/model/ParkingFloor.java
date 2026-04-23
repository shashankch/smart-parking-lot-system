package com.parking.model;

import com.parking.model.enums.SpotType;
import java.util.ArrayList;
import java.util.List;

public class ParkingFloor {
    private final int floorNo;

    private final List<ParkingSpot> spots = new ArrayList<>();

    public ParkingFloor(int floorNo, int smallSpots, int mediumSpots, int largeSpots) {

        this.floorNo = floorNo;

        int id = floorNo * 1000;

        for (int i = 0; i < smallSpots; i++)

            spots.add(new ParkingSpot(++id, SpotType.SMALL));

        for (int i = 0; i < mediumSpots; i++)

            spots.add(new ParkingSpot(++id, SpotType.MEDIUM));

        for (int i = 0; i < largeSpots; i++)

            spots.add(new ParkingSpot(++id, SpotType.LARGE));

    }

    public int getFloorNo() {

        return floorNo;

    }

    public List<ParkingSpot> getSpots() {

        return spots;

    }
}
