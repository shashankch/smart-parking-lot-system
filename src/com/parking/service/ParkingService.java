package com.parking.service;

import com.parking.exception.InvalidTicketException;
import com.parking.exception.ParkingSpotNotFoundException;
import com.parking.model.ParkingFloor;
import com.parking.model.ParkingLot;
import com.parking.model.ParkingSpot;
import com.parking.model.Ticket;
import com.parking.model.Vehicle;
import com.parking.service.interfaces.FeeStrategy;
import com.parking.service.interfaces.SpotAllocationStrategy;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingService {

    private final ParkingLot lot;

    private final SpotAllocationStrategy allocator;

    private final FeeStrategy feeStrategy;

    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();

    private final AtomicInteger counter = new AtomicInteger(1);

    public ParkingService(

            ParkingLot lot,

            SpotAllocationStrategy allocator,

            FeeStrategy feeStrategy

    ) {

        this.lot = lot;

        this.allocator = allocator;

        this.feeStrategy = feeStrategy;

    }

    public Ticket checkIn(Vehicle vehicle) {

        ParkingSpot spot = allocator.allocate(vehicle, lot)
                .orElseThrow(() -> new ParkingSpotNotFoundException(
                        "Sorry, no spot available for " + vehicle.getType()));

        String ticketId = "T-" + counter.getAndIncrement();

        Ticket ticket = new Ticket(ticketId, vehicle, spot);

        activeTickets.put(ticketId, ticket);

        System.out.println(
                "Allocated Spot for vehicle " + vehicle.getNumber() + " at " + spot.getId() + " Ticket= #" + ticketId);

        return ticket;

    }

    public void checkOut(String ticketId) {

        Ticket ticket = activeTickets.remove(ticketId);

        if (ticket == null) {

            throw new InvalidTicketException("Ticket not found: #" + ticketId);

        }

        ticket.close();

        ParkingSpot spot = ticket.getSpot();

        int floorNo = spot.getFloorNo();

        ParkingFloor floor = lot.getFloors().stream()
                .filter(f -> f.getFloorNo() == floorNo)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Floor " + floorNo + " not found"));

        floor.releaseSpot(spot);

        BigDecimal fee = feeStrategy.calculate(ticket);

        System.out.println("Vehicle " + ticket.getVehicleNumber() + " checked out. Ticket #" + ticketId + " Fee = ₹" + fee);

    }

    public void printAvailability() {

        for (ParkingFloor floor : lot.getFloors()) {

            long free = floor.getSpots()

                    .stream()

                    .filter(s -> !s.isOccupied())

                    .count();

            System.out.println(

                    "Floor " + floor.getFloorNo() +

                            " Free Spots = " + free

            );

        }

    }

}
