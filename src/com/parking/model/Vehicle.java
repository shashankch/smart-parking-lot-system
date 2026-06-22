package com.parking.model;

import com.parking.model.enums.VehicleType;
import java.util.Objects;
import java.util.regex.Pattern;

public class Vehicle {

    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z0-9 -]{2,15}$", Pattern.CASE_INSENSITIVE);

    private final String number;

    private final VehicleType type;

    public Vehicle(String number, VehicleType type) {

        this.number = Objects.requireNonNull(number, "Vehicle number cannot be null").trim();

        this.type = Objects.requireNonNull(type, "Vehicle type cannot be null");

        if (this.number.isEmpty()) {

            throw new IllegalArgumentException("Vehicle number cannot be empty");

        }

        if (!PLATE_PATTERN.matcher(this.number).matches()) {

            throw new IllegalArgumentException("Invalid vehicle plate number format");

        }

    }

    public String getNumber() {

        return number;

    }

    public VehicleType getType() {

        return type;

    }

}
