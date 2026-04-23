# Smart Parking Lot System

A production-grade, thread-safe parking lot management system implementing low-level design (LLD) principles with pure Core Java. The system efficiently manages parking spots across multiple floors, allocates vehicles based on type and availability, and calculates dynamic parking fees.

## Architecture & Design Patterns

### Core Patterns

- **Singleton Pattern**: Single ParkingLot instance across the system
- **Strategy Pattern**: Pluggable allocation and fee calculation strategies
- **Builder Approach**: Flexible service configuration with injectable dependencies

## Concurrency & Thread Safety

- Atomic spot reservation via synchronized `ParkingSpot` methods.
- Concurrent ticket tracking using `ConcurrentHashMap`.
- Unique ticket generation using `AtomicInteger`.
- Designed to prevent double-booking during simultaneous check-ins.

## Features

- Vehicle-type-aware spot allocation (CAR, MOTORCYCLE, BUS)
- Multi-floor parking lot management with real-time availability
- Dynamic fee calculation with extensible strategies
- Thread-safe concurrent operations
- Exception handling for invalid operations
- Check-in/check-out with automatic fee computation

## Project Structure

```
src/com/parking/
├── Main.java                          # Entry point & example usage
├── exception/
│   ├── InvalidTicketException.java
│   └── ParkingSpotNotFoundException.java
├── model/
│   ├── ParkingLot.java               # Singleton lot manager
│   ├── ParkingFloor.java             # Floor with typed spots
│   ├── ParkingSpot.java              # Individual spot
│   ├── Ticket.java                   # Check-in/out record
│   ├── Vehicle.java                  # Vehicle entity
│   └── enums/
│       ├── VehicleType.java
│       └── SpotType.java
└── service/
    ├── ParkingService.java           # Core business logic
    ├── interfaces/
    │   ├── SpotAllocationStrategy.java
    │   └── FeeStrategy.java
    └── strategies/
        ├── NearestSpotAllocatorByVehType.java
        └── HourlyFeeStrategy.java
```

## Prerequisites

- Java 8 or higher
- No external dependencies (pure Core Java)

## Build & Run

**Compile:**

```bash
javac -d out src/com/parking/**/*.java
```

**Execute:**

```bash
java -cp out com.parking.Main
```

## Usage Example

```java
// Initialize parking lot with multiple floors
ParkingLot parkingLot = ParkingLot.getInstance();
parkingLot.addFloor(new ParkingFloor(1, 10, 10, 5)); // 10 small, 10 medium, 5 large spots
parkingLot.addFloor(new ParkingFloor(2, 10, 10, 5));

// Configure service with allocation & fee strategies
ParkingService service = new ParkingService(
    parkingLot,
    new NearestSpotAllocatorByVehType(),
    new HourlyFeeStrategy()
);

// Check-in vehicle
Ticket ticket = service.checkIn(new Vehicle("UP32AA1111", VehicleType.CAR));

// Later: Check-out and compute fee
service.checkOut(ticket.getTicketId());
```

## Key Design Decisions

1. **Singleton Pattern for ParkingLot**: Ensures centralized parking lot state with thread-safe access
2. **Strategy Pattern for Allocation**: Enables swapping allocation algorithms (distance-based, random, etc.) without modifying core logic
3. **Strategy Pattern for Fees**: Supports multiple fee models (hourly, daily, flat-rate) extensible
4. **Concurrent Collections**: AtomicInteger and ConcurrentHashMap provide lock-free operations for high-throughput scenarios
5. **Vehicle Type Awareness**: Spot allocation respects vehicle size constraints (CAR, MOTORCYCLE, BUS..)