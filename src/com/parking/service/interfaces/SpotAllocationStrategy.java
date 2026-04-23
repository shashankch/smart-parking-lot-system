package com.parking.service.interfaces;

import com.parking.model.ParkingLot;
import com.parking.model.ParkingSpot;
import com.parking.model.Vehicle;
import java.util.Optional;

public interface SpotAllocationStrategy {
    Optional<ParkingSpot> allocate(Vehicle vehicle, ParkingLot lot);
}
