
package com.parking;

import com.parking.model.ParkingFloor;
import com.parking.model.ParkingLot;
import com.parking.model.Ticket;
import com.parking.model.Vehicle;
import com.parking.model.enums.VehicleType;
import com.parking.service.ParkingService;
import com.parking.service.strategies.HourlyFeeStrategy;
import com.parking.service.strategies.NearestSpotAllocatorByVehType;

public class Main {
    public static void main(String[] args) throws Exception {

        ParkingLot parkingLot = ParkingLot.getInstance();

        parkingLot.addFloor(new ParkingFloor(1, 10, 10, 5));

        parkingLot.addFloor(new ParkingFloor(2, 10, 10, 5));

        ParkingService service = new ParkingService(

                parkingLot,

                new NearestSpotAllocatorByVehType(),

                new HourlyFeeStrategy()

        );

        Ticket t1 = service.checkIn(new Vehicle("UP32AA1111", VehicleType.CAR));

        Ticket t2 = service.checkIn(new Vehicle("UP32BB2222", VehicleType.MOTORCYCLE));

        Ticket t3 = service.checkIn(new Vehicle("UP32CC3333", VehicleType.BUS));


        service.printAvailability();

        Thread.sleep(3000); //simulate some time passing

        service.checkOut(t1.getTicketId());

        service.checkOut(t2.getTicketId());

        service.printAvailability();

        Thread.sleep(2000);

        service.checkOut(t3.getTicketId());

        service.printAvailability();
    }
}
