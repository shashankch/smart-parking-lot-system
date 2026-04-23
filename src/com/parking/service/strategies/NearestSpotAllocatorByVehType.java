package com.parking.service.strategies;

import com.parking.model.ParkingFloor;
import com.parking.model.ParkingLot;
import com.parking.model.ParkingSpot;
import com.parking.model.Vehicle;
import com.parking.model.enums.SpotType;
import com.parking.service.interfaces.SpotAllocationStrategy;
import java.util.List;
import java.util.Optional;

public class NearestSpotAllocatorByVehType implements SpotAllocationStrategy {
    @Override
    public Optional<ParkingSpot> allocate(Vehicle vehicle, ParkingLot lot) {

        List<SpotType> preferred = getPreferred(vehicle);

        for (ParkingFloor floor : lot.getFloors()) {

            for (SpotType type : preferred) {

                for (ParkingSpot spot : floor.getSpots()) {

                    if (spot.getType() == type && spot.assignVehicle(vehicle.getNumber())) {
                        return Optional.of(spot);

                    }

                }

            }

        }

        return Optional.empty();

    }

    private List<SpotType> getPreferred(Vehicle vehicle) {

        return switch (vehicle.getType()) {
            case MOTORCYCLE -> List.of(SpotType.SMALL);
            case CAR -> List.of(SpotType.MEDIUM, SpotType.LARGE);
            case BUS -> List.of(SpotType.LARGE);
            default -> List.of();
        };

    }

}
