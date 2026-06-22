package com.parking.service.interfaces;

import com.parking.model.Ticket;
import java.math.BigDecimal;

public interface FeeStrategy {

    BigDecimal calculate(Ticket ticket);

}
