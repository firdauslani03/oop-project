// [Shared entry point: initial scaffold predates task division; rebuilt by Iman (Member 1) to wire Resident/Booking/Queue modules; Admin Login and Station menu wiring added by Firdaus (Member 2)]
import Exception.*;
import java.util.Scanner;

public class BookingChargerApp {
    public static void main(String[] args) {

        final String ADMIN_USERNAME = "admin";
        final String ADMIN_PASSWORD = "admin123";

        ResidentManager manager = new ResidentManager();
        StationManager stationManager = new StationManager();       
        WaitlistManager waitlistManager = new WaitlistManager(new NormalQueueStrategy());
        BookingManager bookingManager = new BookingManager(waitlistManager,manager, stationManager); 
 
        Scanner scanner = new Scanner(System.in);

        stationManager.seedDefaultStationsIfEmpty();
        stationManager.setBookingManager(bookingManager);
        waitlistManager.setStationManager(stationManager);
        waitlistManager.setBookingManager(bookingManager);

        while (true) {
            System.out.println("\n=================================");
            System.out.println("  EV COMMUNITY MANAGEMENT SYSTEM  ");
            System.out.println("=================================");
            System.out.println("1. Register Account");
            System.out.println("2. Resident Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit Program");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleRegistration(scanner, manager);
                    break;
                case "2":
                    handleLogin(scanner, manager, bookingManager, stationManager, waitlistManager);
                    break;
                case "3":
                    handleAdminLogin(scanner, stationManager, ADMIN_USERNAME, ADMIN_PASSWORD);
                    break;
                case "4":
                    System.out.println("Thank you for using the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, 3, or 4.");
            }
        }
    }

    static void handleRegistration(Scanner scanner, ResidentManager manager) {

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
            System.out.println("Registration successful. You can now log in.");
        } catch (DuplicateAccountException | InvalidIcFormatException | InvalidRegistrationException e) {
            System.out.println("[REGISTRATION ERROR] " + e.getMessage());
        }
    }

    static void handleLogin(Scanner scanner, ResidentManager manager, BookingManager bookingManager, StationManager stationManager, WaitlistManager waitlistManager) {
        
        System.out.println("\n--- SYSTEM LOGIN ---");
        System.out.print("Enter Resident ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            Resident loggedInResident = manager.login(id, password);
            System.out.println("\nLogin successful! Welcome back, " + loggedInResident.getName() + ".");
            showResidentMenu(loggedInResident, scanner, manager, bookingManager, stationManager, waitlistManager);
        } catch (InvalidLoginException e) {
            System.out.println("[LOGIN ERROR] " + e.getMessage());
        }
    }

    static void handleAdminLogin(Scanner scanner, StationManager stationManager, String ADMIN_USERNAME, String ADMIN_PASSWORD) {

        System.out.println("\n--- ADMIN LOGIN ---");
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine().trim();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("\nAdmin login successful. Welcome, Administrator.");
            AdminMenu adminMenu = new AdminMenu(stationManager, scanner);
            adminMenu.start();
        } else {
            System.out.println("[LOGIN ERROR] Invalid admin username or password.");
        }
    }

    static void showResidentMenu(Resident resident, Scanner scanner, ResidentManager manager, BookingManager bookingManager, StationManager stationManager, WaitlistManager waitlistManager) {
        
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      RESIDENT DASHBOARD         ");
            System.out.println("=================================");
            System.out.println("1. Profile Management");
            System.out.println("2. Vehicle Management");
            System.out.println("3. Manage Bookings");
            System.out.println("4. Waitlist Queue");
            System.out.println("5. View Charging Stations");
            System.out.println("6. Log Out");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    ProfileMenu profileMenu = new ProfileMenu(manager, resident, scanner);
                    profileMenu.start();
                    break;
                case "2":
                    VehicleMenu vehicleMenu = new VehicleMenu(manager, resident, scanner);
                    vehicleMenu.start();
                    break;
                case "3":
                    if (bookingManager != null) {
                        BookingMenu bookingMenu = new BookingMenu(bookingManager, stationManager, resident, scanner, waitlistManager);
                        bookingMenu.start();
                    } else {
                        System.out.println("Booking features are not available.");
                    }
                    break;
                case "4":
                    QueueMenu queueMenu = new QueueMenu(waitlistManager, resident, scanner, stationManager, bookingManager);
                    queueMenu.start();
                    break;
                case "5":
                    StationMenu stationMenu = new StationMenu(stationManager, scanner);
                    stationMenu.start();
                    break;
                case "6":
                    System.out.println("Logging out of account...");
                    return;
                default:
                    System.out.println("Invalid option. Choose an option between 1 and 6.");
            }
        }
    }
}
