package com.parking.service.strategies;

import com.parking.model.Ticket;
import com.parking.service.interfaces.FeeStrategy;
import java.time.Duration;

public class HourlyFeeStrategy implements FeeStrategy {
    @Override
    public double calculate(Ticket ticket) {

        long mins = Duration.between(

                ticket.getEntryTime(),

                ticket.getExitTime()

        ).toMinutes();

        long hours = Math.max(1, (mins + 59) / 60);

        return switch (ticket.getVehicleType()) {
            case MOTORCYCLE -> hours * 10;
            case CAR -> hours * 20;
            case BUS -> hours * 50;
            default -> 0;
        };

    }
}
