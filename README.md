# Smart Parking Lot System

A production-grade, thread-safe, and lock-free parking lot management system implementing low-level design (LLD) principles with Core Java. The system efficiently manages parking spots across multiple floors, allocates vehicles based on type and availability using concurrent queues, and calculates parking fees with arbitrary precision.

---

## Core Features & Architecture

- **Lock-Free Concurrency (CAS)**: Thread safety in `ParkingSpot` is implemented using CPU-level atomic Compare-And-Swap (CAS) operations via `AtomicReference`. This avoids standard monitor lock overhead (`synchronized` blocks) and eliminates thread blocking.
- **O(1) Concurrent Queues**: Spot allocation is managed using type-specific concurrent queues (`ConcurrentLinkedQueue`) per floor. This eliminates $O(N)$ linear scans of all spots, preventing lock contention under high-volume parallel check-ins.
- **BigDecimal Precision Calculations**: The system utilizes `BigDecimal` (`RoundingMode.HALF_UP` scale 2) for fee calculation to prevent floating-point binary rounding errors standard in primitive `double` or `float` types.
- **Advanced Dynamic Pricing**: Features a Peak Hour Surge Strategy which applies a `1.5x` surge multiplier to the base fee when check-in times fall within peak rush hours (09:00 - 12:00 and 17:00 - 20:00).
- **Defensive Programming**: Basic input validation and license plate validation are built directly into domain models to enforce business constraints at the class boundary.
- **Interactive CLI & Concurrency Tester**: Includes a text-based terminal menu to interactively configure floors, select pricing models, perform check-ins/check-outs, view real-time floor availability, or execute parallel stress-tests.

---

## Design Patterns

- **Singleton Pattern**: Centralized, thread-safe `ParkingLot` manager.
- **Strategy Pattern**: Pluggable spot allocation (`SpotAllocationStrategy`) and fee calculation (`FeeStrategy`) algorithms.
- **Decorator Pattern**: A structural pattern used to dynamically extend the base `HourlyFeeStrategy` with a `PeakHourSurgeFeeStrategy` wrapper without modifying the base strategy logic.
- **Queue-Pool Management**: Structured available queues per spot type to handle rapid allocations in a non-blocking way.

---

## Project Structure

```
src/com/parking/
├── Main.java                          # Entry point & Interactive CLI
├── exception/
│   ├── InvalidTicketException.java
│   └── ParkingSpotNotFoundException.java
├── model/
│   ├── ParkingLot.java               # Singleton lot manager with reset capabilities
│   ├── ParkingFloor.java             # Floor with ConcurrentLinkedQueue pools
│   ├── ParkingSpot.java              # Individual spot with lock-free atomic references
│   ├── Ticket.java                   # Check-in/out record
│   ├── Vehicle.java                  # Vehicle entity with regex plate validation
│   └── enums/
│       ├── VehicleType.java
│       └── SpotType.java
└── service/
    ├── ParkingService.java           # Core business logic orchestrating flows
    ├── interfaces/
    │   ├── SpotAllocationStrategy.java
    │   └── FeeStrategy.java
    └── strategies/
        ├── NearestSpotAllocatorByVehType.java
        ├── HourlyFeeStrategy.java
        └── PeakHourSurgeFeeStrategy.java # Decorator for dynamic pricing
```

---

## Prerequisites

- Java 8 or higher
- No external dependencies (pure Core Java SE)

---

## Build & Run

**Compile:**
```bash
javac -d out src/com/parking/**/*.java src/com/parking/*.java
```

**Execute:**
```bash
java -cp out com.parking.Main
```

---

## Usage Example

```java
// Initialize parking lot with multiple floors
ParkingLot parkingLot = ParkingLot.getInstance();
parkingLot.addFloor(new ParkingFloor(1, 10, 10, 5)); // 10 small, 10 medium, 5 large spots

// Base strategy
FeeStrategy hourlyStrategy = new HourlyFeeStrategy();

// Wrap with Peak Hour Surge (Decorator pattern)
FeeStrategy peakHourStrategy = new PeakHourSurgeFeeStrategy(hourlyStrategy);

// Configure service with allocation & dynamic fee strategies
ParkingService service = new ParkingService(
    parkingLot,
    new NearestSpotAllocatorByVehType(),
    peakHourStrategy
);

// Check-in vehicle
Ticket ticket = service.checkIn(new Vehicle("DL3CAF1234", VehicleType.CAR));

// Later: Check-out and compute fee (BigDecimal output with surge factor if applicable)
service.checkOut(ticket.getTicketId());
```

---

## Thread Safety Verification

To prove concurrency safety, run the program and select **Option 4** (`Run Concurrency Stress Test`). It will:
1. Reset the lot and initialize 1 floor with exactly **3 small spots**.
2. Spawn **5 concurrent threads** checking in 5 motorcycles simultaneously using a `CountDownLatch`.
3. Verify that exactly **3 check-ins succeed** and **2 fail** with standard custom exceptions.
4. Concurrently check out the 3 parked vehicles and verify all spots are returned to availability correctly.