// [Module: Resident & Vehicle Management — Iman, Member 1]
import java.util.Scanner;

public class VehicleMenu {
    private ResidentManager manager;
    private Resident resident;
    private Scanner scanner;

    public VehicleMenu(ResidentManager manager, Resident resident, Scanner scanner) {
        this.manager = manager;
        this.resident = resident;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      VEHICLE MANAGEMENT       ");
            System.out.println("=================================");
            System.out.println("1. Add Registered Vehicle / EV");
            System.out.println("2. Remove Registered Vehicle");
            System.out.println("3. View Registered Vehicles");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addVehicle();
                    break;
                case "2":
                    removeVehicle();
                    break;
                case "3":
                    viewVehicles();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option. Choose an option between 1 and 4.");
            }
        }
    }

    // [TAMPERED by Claude: now also asks for a plate number, which is the unique
    // identifier used to remove a specific vehicle later.]
    public void addVehicle() {
        System.out.print("Enter Vehicle Plate Number: ");
        String plateNumber = scanner.nextLine().trim();
        System.out.print("Enter EV / Vehicle Model: ");
        String model = scanner.nextLine().trim();
        System.out.print("Enter Battery Capacity (kWh): ");
        try {
            double capacity = Double.parseDouble(scanner.nextLine().trim());
            manager.addVehicleToResident(resident, plateNumber, model, capacity);
            System.out.println("Success! Vehicle added to profile.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid capacity format. Must be numeric.");
        }
    }

    // [TAMPERED by Claude: removal is now done by plate number instead of model,
    // since multiple vehicles can share the same model but never the same plate.]
    public void removeVehicle() {
        System.out.print("Enter exact Vehicle Plate Number to remove: ");
        String plateToRemove = scanner.nextLine().trim();
        boolean removed = manager.removeVehicleFromResident(resident, plateToRemove);
        if (removed) {
            System.out.println("Success! Vehicle removed cleanly.");
        } else {
            System.out.println("Error: Vehicle with that plate number not found on your profile.");
        }
    }

    public void viewVehicles() {
        System.out.println("\n--- Your Registered Vehicles ---");
        if (resident.getVehicles().isEmpty()) {
            System.out.println("(No vehicles registered yet)");
        } else {
            for (int i = 0; i < resident.getVehicles().size(); i++) {
                System.out.println((i + 1) + ". " + resident.getVehicles().get(i));
            }
        }
    }
}
