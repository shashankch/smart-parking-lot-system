package com.parking.service.strategies;

import com.parking.model.Ticket;
import com.parking.service.interfaces.FeeStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class HourlyFeeStrategy implements FeeStrategy {

    @Override
    public BigDecimal calculate(Ticket ticket) {

        long mins = Duration.between(

                ticket.getEntryTime(),

                ticket.getExitTime()

        ).toMinutes();

        long hours = Math.max(1, (mins + 59) / 60);

        double hourlyRate = switch (ticket.getVehicleType()) {
            case MOTORCYCLE -> 10.0;
            case CAR -> 20.0;
            case BUS -> 50.0;
        };

        return BigDecimal.valueOf(hours)
                .multiply(BigDecimal.valueOf(hourlyRate))
                .setScale(2, RoundingMode.HALF_UP);

    }

}
