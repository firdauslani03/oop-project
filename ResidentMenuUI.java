import java.util.Scanner;
import Exception.*;

public class ResidentMenuUI {
    private ResidentManager manager;
    private Scanner scanner;

    public ResidentMenuUI(ResidentManager manager) {
        this.manager = manager;
        this.scanner = new Scanner(System.in);
    }

    // Starts the main application loop for public access
    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("  EV COMMUNITY MANAGEMENT SYSTEM  ");
            System.out.println("=================================");
            System.out.println("1. Register Account");
            System.out.println("2. Login System");
            System.out.println("3. Exit Program");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleRegistration();
                    break;
                case "2":
                    handleLogin();
                    break;
                case "3":
                    System.out.println("Thank you for using the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    // Handles Feature 1: Register Account
    private void handleRegistration() {
        System.out.println("\n--- ACCOUNT REGISTRATION ---");
        System.out.print("Enter unique Resident ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter IC Number (12 digits, no hyphens): ");
        String ic = scanner.nextLine().trim();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine().trim();

        try {
            manager.registerResident(id, name, password, ic, phone);
        } catch (DuplicateAccountException | InvalidIcFormatException e) {
            System.out.println("[REGISTRATION ERROR] " + e.getMessage());
        }
    }

    // Handles Feature 2: Login System
    private void handleLogin() {
        System.out.println("\n--- SYSTEM LOGIN ---");
        System.out.print("Enter Resident ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            Resident loggedInResident = manager.login(id, password);
            System.out.println("\nLogin successful! Welcome back, " + loggedInResident.getName() + ".");
            showResidentMenu(loggedInResident);
        } catch (InvalidLoginException e) {
            System.out.println("[LOGIN ERROR] " + e.getMessage());
        }
    }

    // Authorized Sub-Menu once logged in successfully
    private void showResidentMenu(Resident resident) {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      RESIDENT DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("1. View Profile Details");
            System.out.println("2. Update Profile Contact info");
            System.out.println("3. Change Password");
            System.out.println("4. Add Registered Vehicle / EV");
            System.out.println("5. Remove Registered Vehicle");
            System.out.println("6. View Registered Vehicles");
            System.out.println("7. Log Out");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": // Feature 3: View Profile
                    System.out.println("\n--- Profile Information ---");
                    System.out.println("Resident ID  : " + resident.getResidentId());
                    System.out.println("Name         : " + resident.getName());
                    System.out.println("IC Number    : " + resident.getIcNumber());
                    System.out.println("Phone Number : " + resident.getPhoneNumber());
                    System.out.println("Registered Vehicles: " + resident.getVehicles().size());
                    if (!resident.getVehicles().isEmpty()) {
                        for (Vehicle v : resident.getVehicles()) {
                            System.out.println("  - " + v);
                        }
                    }
                    break;

                case "2": // Feature 4: Update Profile
                    System.out.print("Enter your new Phone Number: ");
                    String newPhone = scanner.nextLine().trim();
                    manager.updateProfile(resident, newPhone, null);
                    System.out.println("Success! Profile details updated.");
                    break;

                case "3": // Feature 5: Change Password
                    System.out.print("Enter current Password: ");
                    String current = scanner.nextLine().trim();
                    System.out.print("Enter new secure Password: ");
                    String newPass = scanner.nextLine().trim();
                    boolean changed = manager.changePassword(resident, current, newPass);
                    if (changed) System.out.println("Success! Password updated securely.");
                    else System.out.println("Error: password change failed (wrong current password or weak new password).");
                    break;

                case "4": // Feature 6 & 7: Add Vehicle & EV Profile Management
                    System.out.print("Enter EV / Vehicle Model: ");
                    String model = scanner.nextLine().trim();
                    System.out.print("Enter Battery Capacity (kWh): ");
                    try {
                        double capacity = Double.parseDouble(scanner.nextLine().trim());
                        manager.addVehicleToResident(resident, model, capacity);
                        System.out.println("Success! Vehicle added to profile.");
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid capacity format. Must be numeric numbers.");
                    }
                    break;

                case "5": // Feature 6: Remove Vehicle
                    System.out.print("Enter exact Vehicle Model to remove: ");
                    String modelToRemove = scanner.nextLine().trim();
                    boolean removed = manager.removeVehicleFromResident(resident, modelToRemove);
                    if (removed) {
                        System.out.println("Success! Vehicle removed cleanly.");
                    } else {
                        System.out.println("Error: Vehicle model not found on your profile.");
                    }
                    break;

                case "6": // Feature 6: View Registered Vehicles
                    System.out.println("\n--- Your Registered Vehicles ---");
                    if (resident.getVehicles().isEmpty()) {
                        System.out.println("(No vehicles registered yet)");
                    } else {
                        for (int i = 0; i < resident.getVehicles().size(); i++) {
                            System.out.println((i + 1) + ". " + resident.getVehicles().get(i));
                        }
                    }
                    break;

                case "7":
                    System.out.println("Logging out of account...");
                    return;

                default:
                    System.out.println("Invalid option. Choose an option between 1 and 7.");
            }
        }
    }
}