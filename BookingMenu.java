// [Module: Booking & Reservation Management — Hong Jia Bao, Member 3; extended by Firdaus, Member 2, to look up real stations and share the Scanner instance]
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class BookingMenu {
    private BookingManager bookingManager;
    private StationManager stationManager;
    private Scanner scanner;
    private Resident loggedInResident;

    public BookingMenu(BookingManager bookingManager, StationManager stationManager, Resident loggedInResident,
                        Scanner scanner) {
        this.bookingManager = bookingManager;
        this.stationManager = stationManager;
        this.scanner = scanner;
        this.loggedInResident = loggedInResident;
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
        stationManager.viewStationAvailability();
        System.out.println("---------------------------------");
        String bookingId = bookingManager.generateNextBookingId();
        System.out.println("Your Booking ID: " + bookingId);

        System.out.print("\nEnter Charging Station ID (exp. FAST-01, NORMAL-02): ");
        String stationId = scanner.nextLine();
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

    public void handleCancelBooking() {
        System.out.println("\n___ CANCEL BOOKING___");
        System.out.print("\nEnter the Booking ID you want to cancel: ");
        String bookingId = scanner.nextLine();

        bookingManager.cancelBooking(bookingId);
    }
}
