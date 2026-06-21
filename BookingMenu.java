// [Module: Booking & Reservation Management — Hong Jia Bao, Member 3; extended by Firdaus, Member 2, to look up real stations and share the Scanner instance]
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import Exception.*;

public class BookingMenu {
    private BookingManager bookingManager;
    private StationManager stationManager;
    private Scanner scanner;
    private Resident loggedInResident;
    private WaitlistManager waitlistManager;

    public BookingMenu(BookingManager bookingManager, StationManager stationManager, Resident loggedInResident,
                        Scanner scanner, WaitlistManager waitlistManager) {
        this.bookingManager = bookingManager;
        this.stationManager = stationManager;
        this.scanner = scanner;
        this.loggedInResident = loggedInResident;
        this.waitlistManager = waitlistManager;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("     CHARGING SLOT BOOKING       ");
            System.out.println("=================================");
            System.out.println("1. Book a Charging Slot");
            System.out.println("2. Cancel a Booking");
            System.out.println("3. View all Booking History");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":   handleBookSlot();
                            break;
                    
                case "2":   handleCancelBooking();
                            break;
                    
                case "3":   bookingManager.viewMyBookings(loggedInResident);
                            break;
                    
                case "4":   System.out.println("Returning to main menu...");
                            return;
                    
                default:    System.out.println("Invalid option. Please enter 1-4.");          
            }
        }
    }

    public void handleBookSlot() {
        System.out.println("\n___BOOK CHARGING SLOT___");
        System.out.println("\nList of Charger :");
        stationManager.viewAllStations();
        System.out.println("---------------------------------");
        String bookingId = bookingManager.generateNextBookingId();
        System.out.println("Your Booking ID: " + bookingId);

        System.out.print("\nEnter Charging Station ID (exp. FAST-01, NORMAL-02), or 'Q' to view a station's queue: ");
        String stationId = scanner.nextLine().trim();

        if (stationId.equalsIgnoreCase("Q")) {
            System.out.print("Enter Station ID to view its queue: ");
            String queueStationId = scanner.nextLine().trim();
            try {
                waitlistManager.validateStationId(queueStationId);
                waitlistManager.printCurrentQueue(queueStationId);
            } catch (InvalidQueueSelectionException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
            System.out.print("\nEnter Charging Station ID (exp. FAST-01, NORMAL-02): ");
            stationId = scanner.nextLine().trim();
        }

        ChargingStation station = stationManager.findStation(stationId);

        if (station == null) {
            System.out.println("ERROR: Station '" + stationId + "' does not exist.");
            return;
        }
        if (!station.isAvailable()) {
            System.out.println("ERROR: Station '" + stationId + "' is currently " + station.getStatus()
                    + " and cannot be booked.");
            return;
        }

        Booking currentBooking = bookingManager.getActiveBooking(stationId);
        if (currentBooking != null) {
            System.out.println("\nStation '" + stationId + "' currently has a booking:");
            System.out.println(currentBooking);
            System.out.print("\nWould you like to be added to this station's waiting list instead? (Y/N): ");
            String joinChoice = scanner.nextLine().trim();

            if (joinChoice.equalsIgnoreCase("Y")) {
                handleJoinWaitlistWithDetails(stationId);
            } else {
                System.out.println("Returning to the booking menu...");
            }
            return;
        }

        try {
            System.out.print("\nEnter Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            System.out.print("\nEnter Start Time (HH:MM, 24-hour): ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine());

            System.out.print("\nEnter End Time (HH:MM, 24-hour): ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine());

            bookingManager.createBooking(bookingId, loggedInResident, station, date, startTime, endTime);
            station.addBookingRecord(bookingId);
            stationManager.saveStationsToFile();

        } catch (DateTimeParseException e) {
            System.out.println("ERROR: Invalid date or time format! Please follow YYYY-MM-DD and HH:MM.");
        }
    }

    public void handleJoinWaitlistWithDetails(String stationId) {
        try {
            System.out.print("\nEnter Desired Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            System.out.print("\nEnter Desired Start Time (HH:MM, 24-hour): ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine());

            System.out.print("\nEnter Desired End Time (HH:MM, 24-hour): ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine());

            TimeSlot requestedSlot = new TimeSlot(date, startTime, endTime);

            System.out.println("\nRequested slot: " + date + " " + startTime + " to " + endTime + " at Station " + stationId);

            waitlistManager.joinQueue(loggedInResident, stationId, requestedSlot);

        } catch (DateTimeParseException e) {
            System.out.println("ERROR: Invalid date or time format! Please follow YYYY-MM-DD and HH:MM.");
        } catch (InvalidQueueSelectionException | ResidentAlreadyInQueueException | BookingConflictException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }

    public void handleCancelBooking() {
        System.out.println("\n___ CANCEL BOOKING___");

        ArrayList<Booking> activeBookings = bookingManager.getActiveBookingsForResident(loggedInResident);
        if (activeBookings.isEmpty()) {
            System.out.println("You have no active bookings to cancel.");
            return;
        }

        System.out.println("Your Active Bookings:");
        for (Booking b : activeBookings) {
            System.out.println(b);
            System.out.println("---------------------------------");
        }

        System.out.print("\nEnter the Booking ID you want to cancel: ");
        String bookingId = scanner.nextLine().trim();

        bookingManager.cancelBooking(bookingId);
    }
}
