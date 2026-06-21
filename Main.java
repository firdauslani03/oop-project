import java.util.Scanner;
import Exception.*;

public class Main {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private ResidentManager manager;
    private StationManager stationManager;
    private BookingManager bookingManager;
    private WaitlistManager waitlistManager;
    private Scanner scanner;

    public Main() {
        this.manager = new ResidentManager();
        this.scanner = new Scanner(System.in);

        // Station data must load before bookings, since bookings reference stations by ID.
        this.stationManager = new StationManager();
        this.stationManager.seedDefaultStationsIfEmpty();

        this.waitlistManager = new WaitlistManager(new NormalQueueStrategy());
        this.bookingManager = new BookingManager(this.waitlistManager, this.stationManager);
        this.stationManager.setBookingManager(this.bookingManager);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public void start() {
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
                    handleRegistration();
                    break;
                case "2":
                    handleLogin();
                    break;
                case "3":
                    handleAdminLogin();
                    break;
                case "4":
                    System.out.println("Thank you for using the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, 3, or 4.");
            }
        }
    }

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
            System.out.println("Registration successful. You can now log in.");
        } catch (DuplicateAccountException | InvalidIcFormatException e) {
            System.out.println("[REGISTRATION ERROR] " + e.getMessage());
        }
    }

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

    private void handleAdminLogin() {
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

    private void showResidentMenu(Resident resident) {
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
                        BookingMenu bookingMenu = new BookingMenu(bookingManager, stationManager, resident, scanner);
                        bookingMenu.start();
                    } else {
                        System.out.println("Booking features are not available.");
                    }
                    break;
                case "4":
                    QueueMenu queueMenu = new QueueMenu(waitlistManager, resident, scanner);
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
