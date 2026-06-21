// [Module: Charging Station Management — Firdaus, Member 2]
import Exception.*;
import java.util.Scanner;

public class AdminMenu {
    private StationManager stationManager;
    private Scanner scanner;

    public AdminMenu(StationManager stationManager, Scanner scanner) {
        this.stationManager = stationManager;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("         ADMIN DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("1. View All Charging Stations");
            System.out.println("2. Add Charging Station");
            System.out.println("3. Remove Charging Station");
            System.out.println("4. Update Station Status (Active/Inactive)");
            System.out.println("5. Mark Station Under Maintenance");
            System.out.println("6. Resolve Maintenance");
            System.out.println("7. View Maintenance Records");
            System.out.println("8. View Usage Statistics");
            System.out.println("9. Manually Complete Charging (Demo)");
            System.out.println("10. Log Out");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    stationManager.viewAllStations();
                    break;
                case "2":
                    handleAddStation();
                    break;
                case "3":
                    handleRemoveStation();
                    break;
                case "4":
                    handleUpdateStatus();
                    break;
                case "5":
                    handleMarkMaintenance();
                    break;
                case "6":
                    handleResolveMaintenance();
                    break;
                case "7":
                    stationManager.viewMaintenanceRecords();
                    break;
                case "8":
                    stationManager.viewUsageStatistics();
                    break;
                case "9":
                    handleCompleteCharging();
                    break;
                case "10":
                    System.out.println("Logging out of Admin account...");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1-10.");
            }
        }
    }

    public void handleAddStation() {
        System.out.println("\n___ ADD CHARGING STATION ___");
        System.out.print("Enter Station ID (exp. FAST-06): ");
        String stationId = scanner.nextLine().trim();

        System.out.print("Enter Station Type (Fast/Normal): ");
        String type = scanner.nextLine().trim();

        System.out.print("Enter Location: ");
        String location = scanner.nextLine().trim();

        System.out.print("Enter Power Output (kW): ");
        double power;
        try {
            power = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Power output must be a number.");
            return;
        }

        try {
            stationManager.addStation(stationId, type, location, power);
        } catch (DuplicateStationException | InvalidStationTypeException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void handleRemoveStation() {
        System.out.println("\n___ REMOVE CHARGING STATION ___");
        stationManager.viewAllStations();
        System.out.println("---------------------------------");
        System.out.print("Enter Station ID to remove: ");
        String stationId = scanner.nextLine().trim();

        try {
            stationManager.removeStation(stationId);
        } catch (StationNotFoundException | InvalidStatusUpdateException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void handleUpdateStatus() {
        System.out.println("\n___ UPDATE STATION STATUS ___");
        System.out.print("Enter Station ID: ");
        String stationId = scanner.nextLine().trim();
        System.out.print("Enter New Status (Active/Inactive): ");
        String newStatus = scanner.nextLine().trim();

        try {
            stationManager.updateStationStatus(stationId, newStatus);
        } catch (StationNotFoundException | InvalidStatusUpdateException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void handleMarkMaintenance() {
        System.out.println("\n___ MARK STATION UNDER MAINTENANCE ___");
        System.out.print("Enter Station ID: ");
        String stationId = scanner.nextLine().trim();
        System.out.print("Enter Reason: ");
        String reason = scanner.nextLine().trim();

        try {
            stationManager.markUnderMaintenance(stationId, reason);
        } catch (StationNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void handleResolveMaintenance() {
        System.out.println("\n___ RESOLVE MAINTENANCE ___");
        System.out.print("Enter Station ID: ");
        String stationId = scanner.nextLine().trim();

        try {
            stationManager.resolveMaintenance(stationId);
        } catch (StationNotFoundException | InvalidStatusUpdateException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void handleCompleteCharging() {
        System.out.println("\n___ MANUALLY COMPLETE CHARGING (DEMO) ___");
        System.out.print("Enter Booking ID to mark as completed: ");
        String bookingId = scanner.nextLine().trim();

        try {
            stationManager.completeCharging(bookingId);
        } catch (StationNotFoundException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }
}
