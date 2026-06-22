package com.parking.service.strategies;

import com.parking.model.Ticket;
import com.parking.service.interfaces.FeeStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

public class PeakHourSurgeFeeStrategy implements FeeStrategy {

    private final FeeStrategy baseStrategy;

    public PeakHourSurgeFeeStrategy(FeeStrategy baseStrategy) {

        this.baseStrategy = baseStrategy;

    }

    @Override
    public BigDecimal calculate(Ticket ticket) {

        BigDecimal baseFee = baseStrategy.calculate(ticket);

        LocalTime entryTime = ticket.getEntryTime().toLocalTime();

        // Peak hours: 09:00 - 12:00 and 17:00 - 20:00

        boolean isPeak = (entryTime.isAfter(LocalTime.of(8, 59)) && entryTime.isBefore(LocalTime.of(12, 1))) ||

                         (entryTime.isAfter(LocalTime.of(16, 59)) && entryTime.isBefore(LocalTime.of(20, 1)));

        if (isPeak) {

            System.out.println("[INFO] Peak hour detected. Applying 1.5x surge multiplier.");

            return baseFee.multiply(BigDecimal.valueOf(1.5)).setScale(2, RoundingMode.HALF_UP);

        }

        return baseFee;

    }

}
