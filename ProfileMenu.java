// [Module: Resident & Vehicle Management — Iman, Member 1]
import java.util.Scanner;

public class ProfileMenu {
    private ResidentManager manager;
    private Resident resident;
    private Scanner scanner;

    public ProfileMenu(ResidentManager manager, Resident resident, Scanner scanner) {
        this.manager = manager;
        this.resident = resident;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      PROFILE MANAGEMENT        ");
            System.out.println("=================================");
            System.out.println("1. View Profile Details");
            System.out.println("2. Update Profile Contact info");
            System.out.println("3. Change Password");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showProfileDetails();
                    break;
                case "2":
                    updateContactInfo();
                    break;
                case "3":
                    changePassword();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option. Choose an option between 1 and 4.");
            }
        }
    }

    private void showProfileDetails() {
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
    }

    private void updateContactInfo() {
        System.out.print("Enter your new Phone Number: ");
        String newPhone = scanner.nextLine().trim();
        manager.updateProfile(resident, newPhone, null);
        System.out.println("Success! Profile details updated.");
    }

    private void changePassword() {
        System.out.print("Enter current Password: ");
        String current = scanner.nextLine().trim();
        System.out.print("Enter new secure Password: ");
        String newPass = scanner.nextLine().trim();
        boolean changed = manager.changePassword(resident, current, newPass);
        if (changed) {
            System.out.println("Success! Password updated securely.");
        } else {
            System.out.println("Error: password change failed (wrong current password or weak new password).");
        }
    }
}
