import java.util.Scanner;

public class ResidentConsoleUI {

    public static void showProfileMenu(Resident loggedInResident, ResidentManager manager) {
        Scanner scanner = new Scanner(System.in);
        boolean inProfileMenu = true;

        while (inProfileMenu) {
            System.out.println("\n===== PROFILE MANAGEMENT =====");
            System.out.println("1. View Current Profile");
            System.out.println("2. Update Phone Number");
            System.out.println("3. Change Password");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (choice) {
                case 1:
                    // Feature 3: View Profile
                    System.out.println("\n--- Current Profile Details ---");
                    System.out.println("Resident ID   : " + loggedInResident.getResidentId());
                    System.out.println("Full Name     : " + loggedInResident.getName());
                    System.out.println("IC Number     : " + loggedInResident.getIcNumber());
                    System.out.println("Phone Number  : " + loggedInResident.getPhoneNumber());
                    break;

                case 2:
                    // Feature 4: Update Profile (Phone Number)
                    System.out.print("Enter your new phone number: ");
                    String newPhone = scanner.nextLine();
                    
                    // Pass null to password so only the phone number is updated
                    manager.updateProfile(loggedInResident, newPhone, null); 
                    System.out.println("Success! Phone number updated to: " + loggedInResident.getPhoneNumber());
                    break;

                case 3:
                    // Feature 5: Change Password
                    System.out.print("Enter your current password for security: ");
                    String currentPassword = scanner.nextLine();

                    if (loggedInResident.authenticate(currentPassword)) {
                        System.out.print("Enter your new password: ");
                        String newPassword = scanner.nextLine();
                        
                        // Pass null to phone so only the password is changed
                        manager.updateProfile(loggedInResident, null, newPassword);
                        System.out.println("Success! Password updated securely.");
                    } else {
                        System.out.println("Error: Current password incorrect. Access denied.");
                    }
                    break;

                case 4:
                    inProfileMenu = false;
                    break;

                default:
                    System.out.println("Invalid option. Please choose between 1 and 4.");
            }
        }
    }
}