// [Module: Smart Queue & Waitlist Management — menu wrapper written by Iman, Member 1, around Teoh Xin Yee's WaitlistManager]
import java.util.ArrayList;
import java.util.Scanner;
import Exception.*;

public class QueueMenu {
    private WaitlistManager waitlistManager;
    private Resident resident;
    private Scanner scanner;
    private StationManager stationManager;
    private BookingManager bookingManager;

    public QueueMenu(WaitlistManager waitlistManager, Resident resident, Scanner scanner, StationManager stationManager, BookingManager bookingManager) {
        this.waitlistManager = waitlistManager;
        this.resident = resident;
        this.scanner = scanner;
        this.stationManager = stationManager;
        this.bookingManager = bookingManager;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      QUEUE MANAGEMENT          ");
            System.out.println("=================================");
            System.out.println("1. Join Waiting Queue");
            System.out.println("2. Leave Queue");
            System.out.println("3. View Queue Status");
            System.out.println("4. View a Station's Queue");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    joinQueue();
                    break;
                case "2":
                    leaveQueue();
                    break;
                case "3":
                    viewQueueStatus();
                    break;
                case "4":
                    viewStationQueue();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option. Choose an option between 1 and 5.");
            }
        }
    }

    public void viewStationQueue() {
        System.out.println("\nList of Charging Stations:");
        stationManager.viewAllStations();
        System.out.println("---------------------------------");
        System.out.print("Enter Station ID to view its queue: ");
        String stationId = scanner.nextLine().trim();
        try {
            waitlistManager.validateStationId(stationId);
            waitlistManager.printCurrentQueue(stationId);
        } catch (InvalidQueueSelectionException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }

    public void joinQueue() {
        System.out.println("\nList of Charging Stations:");
        stationManager.viewAllStations();
        System.out.println("---------------------------------");
        System.out.print("Enter desired Station ID to join queue: ");
        String stationId = scanner.nextLine().trim();
        try {
            waitlistManager.joinQueue(resident, stationId);
        } catch (InvalidQueueSelectionException | ResidentAlreadyInQueueException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }

    private void printMyQueuedStations() {
        ArrayList<String> stationIds = waitlistManager.getQueuedStationIds(resident);
        if (stationIds.isEmpty()) {
            System.out.println("You are not currently in any queue.");
            return;
        }
        System.out.println("\nStations you are currently queued at:");
        for (String stationId : stationIds) {
            System.out.println("---------------------------------");
            System.out.println("Station: " + stationId);
            Booking currentBooking = bookingManager.getActiveBooking(stationId);
            if (currentBooking != null) {
                System.out.println(currentBooking);
            } else {
                System.out.println("(No active booking on this station right now.)");
            }
        }
        System.out.println("---------------------------------");
    }

    public void leaveQueue() {
        printMyQueuedStations();
        System.out.print("Enter Station ID to leave queue: ");
        String stationId = scanner.nextLine().trim();
        try {
            if (waitlistManager.leaveQueue(resident, stationId)) {
                System.out.println("You have left the queue for station " + stationId + ".");
            }
        } catch (InvalidQueueSelectionException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }

    public void viewQueueStatus() {
        printMyQueuedStations();
        System.out.print("Enter Station ID to view queue status: ");
        String stationId = scanner.nextLine().trim();
        try {
            waitlistManager.viewQueueStatus(resident, stationId);
        } catch (InvalidQueueSelectionException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }
}
