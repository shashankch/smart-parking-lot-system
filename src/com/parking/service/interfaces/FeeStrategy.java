package com.parking.service.interfaces;

import com.parking.model.Ticket;

public interface FeeStrategy {
    double calculate(Ticket ticket);
}
