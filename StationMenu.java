// [Module: Charging Station Management — Firdaus, Member 2]
import java.util.Scanner;

/**
 * Resident-facing menu: lets residents view charging stations and check
 * availability, without admin privileges.
 */
public class StationMenu {
    private StationManager stationManager;
    private Scanner scanner;

    public StationMenu(StationManager stationManager, Scanner scanner) {
        this.stationManager = stationManager;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("       CHARGING STATIONS         ");
            System.out.println("=================================");
            System.out.println("1. View All Charging Stations");
            System.out.println("2. View Station Availability");
            System.out.println("3. Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    stationManager.viewAllStations();
                    break;
                case "2":
                    stationManager.viewStationAvailability();
                    break;
                case "3":
                    System.out.println("Returning to main menu...");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1-3.");
            }
        }
    }
}
