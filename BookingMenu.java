import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Exception.*;

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
            System.out.println("3. View My Booking History");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleBookSlot();
                    break;
                case "2":
                    handleCancelBooking();
                    break;
                case "3":
                    bookingManager.viewAllBookings();
                    break;
                case "4":
                    System.out.println("Returning to main menu...");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1-4.");
            }
        }
    }

    private void handleBookSlot() {
        System.out.println("\n___BOOK CHARGING SLOT___");
        System.out.print("Enter a New Booking ID (exp. B001): ");
        String bookingId = scanner.nextLine().trim();

        System.out.print("Enter Charging Station ID (exp. FAST-01, NORMAL-02): ");
        String stationId = scanner.nextLine().trim();
        ChargingStation station = stationManager.findStation(stationId);

        if (station == null) {
            System.out.println("[ERROR] Station '" + stationId + "' does not exist.");
            return;
        }
        if (!station.isAvailable()) {
            System.out.println("[ERROR] Station '" + stationId + "' is currently " + station.getStatus()
                    + " and cannot be booked.");
            return;
        }

        try {
            System.out.print("Enter Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Enter Start Time (HH:MM, 24-hour): ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine().trim());

            System.out.print("Enter End Time (HH:MM, 24-hour): ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine().trim());

            bookingManager.createBooking(bookingId, loggedInResident, station, date, startTime, endTime);
            station.addBookingRecord(bookingId);
            stationManager.saveStationsToFile();

        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] Invalid date or time format! Please follow YYYY-MM-DD and HH:MM.");
        }
    }

    private void handleCancelBooking() {
        System.out.println("\n___ CANCEL BOOKING___");
        System.out.print("Enter the Booking ID you want to cancel: ");
        String bookingId = scanner.nextLine().trim();

        bookingManager.cancelBooking(bookingId);
    }
}
