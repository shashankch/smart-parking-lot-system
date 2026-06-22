package com.parking.model;

import com.parking.model.enums.SpotType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParkingFloor {

    private final int floorNo;

    private final List<ParkingSpot> spots = new ArrayList<>();

    private final Map<SpotType, Queue<ParkingSpot>> availableSpots = new ConcurrentHashMap<>();

    public ParkingFloor(int floorNo, int smallSpots, int mediumSpots, int largeSpots) {

        this.floorNo = floorNo;

        availableSpots.put(SpotType.SMALL, new ConcurrentLinkedQueue<>());

        availableSpots.put(SpotType.MEDIUM, new ConcurrentLinkedQueue<>());

        availableSpots.put(SpotType.LARGE, new ConcurrentLinkedQueue<>());

        int id = floorNo * 1000;

        for (int i = 0; i < smallSpots; i++) {

            ParkingSpot spot = new ParkingSpot(++id, floorNo, SpotType.SMALL);

            spots.add(spot);

            availableSpots.get(SpotType.SMALL).add(spot);

        }

        for (int i = 0; i < mediumSpots; i++) {

            ParkingSpot spot = new ParkingSpot(++id, floorNo, SpotType.MEDIUM);

            spots.add(spot);

            availableSpots.get(SpotType.MEDIUM).add(spot);

        }

        for (int i = 0; i < largeSpots; i++) {

            ParkingSpot spot = new ParkingSpot(++id, floorNo, SpotType.LARGE);

            spots.add(spot);

            availableSpots.get(SpotType.LARGE).add(spot);

        }

    }

    public int getFloorNo() {

        return floorNo;

    }

    public List<ParkingSpot> getSpots() {

        return spots;

    }

    public Map<SpotType, Queue<ParkingSpot>> getAvailableSpotsMap() {

        return availableSpots;

    }

    public void releaseSpot(ParkingSpot spot) {

        spot.removeVehicle();

        availableSpots.get(spot.getType()).offer(spot);

    }

}
