import java.util.Scanner;
import Exception.*;

public class QueueMenu {
    private WaitlistManager waitlistManager;
    private Resident resident;
    private Scanner scanner;

    public QueueMenu(WaitlistManager waitlistManager, Resident resident, Scanner scanner) {
        this.waitlistManager = waitlistManager;
        this.resident = resident;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      QUEUE MANAGEMENT          ");
            System.out.println("=================================");
            System.out.println("1. Join Waiting Queue");
            System.out.println("2. Leave Queue");
            System.out.println("3. View Queue Status");
            System.out.println("4. View Queue History");
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
                    waitlistManager.viewQueueHistory(resident);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option. Choose an option between 1 and 5.");
            }
        }
    }

    private void joinQueue() {
        System.out.print("Enter desired Station ID to join queue: ");
        String stationId = scanner.nextLine().trim();
        try {
            waitlistManager.joinQueue(resident, stationId);
        } catch (InvalidQueueSelectionException | ResidentAlreadyInQueueException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }

    private void leaveQueue() {
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

    private void viewQueueStatus() {
        System.out.print("Enter Station ID to view queue status: ");
        String stationId = scanner.nextLine().trim();
        try {
            waitlistManager.viewQueueStatus(resident, stationId);
        } catch (InvalidQueueSelectionException e) {
            System.out.println("[QUEUE ERROR] " + e.getMessage());
        }
    }
}
