package com.parking;

import com.parking.model.ParkingFloor;
import com.parking.model.ParkingLot;
import com.parking.model.Ticket;
import com.parking.model.Vehicle;
import com.parking.model.enums.VehicleType;
import com.parking.service.ParkingService;
import com.parking.service.interfaces.FeeStrategy;
import com.parking.service.strategies.HourlyFeeStrategy;
import com.parking.service.strategies.NearestSpotAllocatorByVehType;
import com.parking.service.strategies.PeakHourSurgeFeeStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        ParkingLot parkingLot = ParkingLot.getInstance();

        parkingLot.reset();

        System.out.println("=================================================");

        System.out.println("      WELCOME TO SMART PARKING LOT SYSTEM        ");

        System.out.println("=================================================");

        System.out.println("\nInitialize Parking Lot configuration:");

        System.out.println("1. Use Default Configuration (2 Floors, 10 Small, 10 Medium, 5 Large spots per floor)");

        System.out.println("2. Custom Configuration");

        System.out.print("Enter choice (1-2): ");

        int configChoice = readInt(scanner, 1, 2);

        if (configChoice == 1) {

            parkingLot.addFloor(new ParkingFloor(1, 10, 10, 5));

            parkingLot.addFloor(new ParkingFloor(2, 10, 10, 5));

            System.out.println("Initialized with default configuration.");

        } else {

            System.out.print("Enter number of floors: ");

            int floors = readInt(scanner, 1, 100);

            for (int f = 1; f <= floors; f++) {

                System.out.println("\nConfiguring Floor #" + f + ":");

                System.out.print("  Number of Small spots (Motorcycles): ");

                int small = readInt(scanner, 0, 1000);

                System.out.print("  Number of Medium spots (Cars): ");

                int medium = readInt(scanner, 0, 1000);

                System.out.print("  Number of Large spots (Buses): ");

                int large = readInt(scanner, 0, 1000);

                parkingLot.addFloor(new ParkingFloor(f, small, medium, large));

            }

            System.out.println("Custom parking lot initialized successfully.");

        }

        System.out.println("\nSelect Pricing Strategy:");
        System.out.println("1. Standard Hourly Strategy");
        System.out.println("2. Peak Hour Surge Strategy (1.5x during peak hours: 09:00-12:00 & 17:00-20:00)");
        System.out.print("Enter choice (1-2): ");
        int pricingChoice = readInt(scanner, 1, 2);

        FeeStrategy feeStrategy = new HourlyFeeStrategy();
        if (pricingChoice == 2) {
            feeStrategy = new PeakHourSurgeFeeStrategy(feeStrategy);
            System.out.println("Peak Hour Surge pricing enabled.");
        } else {
            System.out.println("Standard Hourly pricing enabled.");
        }

        ParkingService service = new ParkingService(

                parkingLot,

                new NearestSpotAllocatorByVehType(),

                feeStrategy

        );

        boolean running = true;

        while (running) {

            System.out.println("\n=================================================");

            System.out.println("                   MAIN MENU                     ");

            System.out.println("=================================================");

            System.out.println("1. Check-In Vehicle");

            System.out.println("2. Check-Out Vehicle");

            System.out.println("3. View Parking Lot Availability");

            System.out.println("4. Run Concurrency Stress Test");

            System.out.println("5. Exit");

            System.out.print("Enter option (1-5): ");

            int option = readInt(scanner, 1, 5);

            switch (option) {

                case 1 -> handleCheckIn(scanner, service);

                case 2 -> handleCheckOut(scanner, service);

                case 3 -> {

                    System.out.println("\n--- Current Availability ---");

                    service.printAvailability();

                }

                case 4 -> runConcurrencyTest(service, parkingLot);

                case 5 -> {

                    running = false;

                    System.out.println("\nThank you for using Smart Parking Lot System!");

                }

            }

        }

        scanner.close();

    }

    private static void handleCheckIn(Scanner scanner, ParkingService service) {

        System.out.println("\n--- Vehicle Check-In ---");

        System.out.print("Enter Vehicle Registration Number (e.g. DL3CAF1234): ");

        String regNum = scanner.next().trim().toUpperCase();

        System.out.println("Select Vehicle Type:");

        System.out.println("1. MOTORCYCLE");

        System.out.println("2. CAR");

        System.out.println("3. BUS");

        System.out.print("Enter choice (1-3): ");

        int typeChoice = readInt(scanner, 1, 3);

        VehicleType type = switch (typeChoice) {
            case 1 -> VehicleType.MOTORCYCLE;
            case 2 -> VehicleType.CAR;
            case 3 -> VehicleType.BUS;
            default -> throw new IllegalStateException("Unexpected choice: " + typeChoice);
        };

        try {

            Vehicle vehicle = new Vehicle(regNum, type);

            Ticket ticket = service.checkIn(vehicle);

            System.out.println("\n[SUCCESS] Ticket generated: #" + ticket.getTicketId());

            System.out.println("          Spot ID: " + ticket.getSpot().getId() + " (Floor " + ticket.getSpot().getFloorNo() + ")");

        } catch (Exception e) {

            System.out.println("\n[ERROR] Check-In failed: " + e.getMessage());

        }

    }

    private static void handleCheckOut(Scanner scanner, ParkingService service) {

        System.out.println("\n--- Vehicle Check-Out ---");

        System.out.print("Enter Ticket ID (e.g. T-1): ");

        String ticketId = scanner.next().trim().toUpperCase();

        try {

            service.checkOut(ticketId);

        } catch (Exception e) {

            System.out.println("\n[ERROR] Check-Out failed: " + e.getMessage());

        }

    }

    private static int readInt(Scanner scanner, int min, int max) {

        while (true) {

            try {

                int val = scanner.nextInt();

                if (val >= min && val <= max) {

                    return val;

                }

                System.out.print("Please enter a value between " + min + " and " + max + ": ");

            } catch (Exception e) {

                System.out.print("Invalid input. Please enter a valid number: ");

                scanner.next(); // consume invalid token

            }

        }

    }

    private static void runConcurrencyTest(ParkingService service, ParkingLot lot) {

        System.out.println("\n=== RUNNING CONCURRENCY TEST ===");

        lot.reset();

        // Add exactly 1 floor with 3 small spots

        ParkingFloor testFloor = new ParkingFloor(1, 3, 0, 0);

        lot.addFloor(testFloor);

        System.out.println("Parking lot reset. Floor 1 initialized with exactly 3 small spots.");

        System.out.println("Simulating 5 simultaneous motorcycle check-ins...");

        int numThreads = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        CountDownLatch startSignal = new CountDownLatch(1);

        CountDownLatch doneSignal = new CountDownLatch(numThreads);

        List<Ticket> successfulTickets = Collections.synchronizedList(new ArrayList<>());

        List<String> failures = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= numThreads; i++) {

            final String vehicleNum = "MH12MC000" + i;

            executor.submit(() -> {

                try {

                    startSignal.await(); // wait for start signal to run concurrently

                    Vehicle motorcycle = new Vehicle(vehicleNum, VehicleType.MOTORCYCLE);

                    Ticket ticket = service.checkIn(motorcycle);

                    successfulTickets.add(ticket);

                } catch (Exception e) {

                    failures.add(e.getMessage());

                } finally {

                    doneSignal.countDown();

                }

            });

        }

        // Start all threads at once

        startSignal.countDown();

        try {

            doneSignal.await(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {

            System.out.println("Concurrency check-in test interrupted.");

        }

        executor.shutdown();

        System.out.println("\nConcurrency Check-in Results:");

        System.out.println("Successful Check-ins: " + successfulTickets.size() + " (Expected: 3)");

        System.out.println("Failed Check-ins: " + failures.size() + " (Expected: 2)");

        for (Ticket ticket : successfulTickets) {

            System.out.println(" - Assigned vehicle " + ticket.getVehicleNumber() + " to spot " + ticket.getSpot().getId());

        }

        for (String failMsg : failures) {

            System.out.println(" - Failure message: " + failMsg);

        }

        // Now checkout concurrently

        System.out.println("\nChecking out successful vehicles concurrently...");

        ExecutorService checkoutExecutor = Executors.newFixedThreadPool(successfulTickets.size());

        CountDownLatch checkoutStart = new CountDownLatch(1);

        CountDownLatch checkoutDone = new CountDownLatch(successfulTickets.size());

        for (Ticket ticket : successfulTickets) {

            checkoutExecutor.submit(() -> {

                try {

                    checkoutStart.await();

                    service.checkOut(ticket.getTicketId());

                } catch (Exception e) {

                    System.err.println("Checkout failed for " + ticket.getTicketId() + ": " + e.getMessage());

                } finally {

                    checkoutDone.countDown();

                }

            });

        }

        checkoutStart.countDown();

        try {

            checkoutDone.await(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {

            System.out.println("Concurrency checkout test interrupted.");

        }

        checkoutExecutor.shutdown();

        System.out.println("\nFinal Availability:");

        service.printAvailability();

    }

}
