import java.util.Scanner;
import Exception.*;

public class Main {
    public static void main(String[] args) {
<<<<<<< HEAD
        ResidentManager residentManager = new ResidentManager();
        WaitlistManager waitlistManager = new WaitlistManager(new NormalQueueStrategy());
        BookingManager bookingManager = new BookingManager(waitlistManager);

        ResidentMenuUI appUI = new ResidentMenuUI(residentManager, bookingManager);
        appUI.start();
=======
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("================================================");
        System.out.println("   Welcome to EV Charging Management System     ");
        System.out.println("================================================");
     
        WaitlistManager waitlistManager = new WaitlistManager(new NormalQueue()); 
    
        BookingManager bookingManager = new BookingManager(waitlistManager);
    
        //test
        Resident loggedInUser = new Resident("R001", "Hong Jia Bao", "IC123456", "0123456789", "pass123");

        while (true) {
            System.out.println("\n--- MAIN SYSTEM MENU ---");
            System.out.println("1. Resident & Vehicle Management [Iman]");
            System.out.println("2. Charging Station Management   [Firdaus]");
            System.out.println("3. Booking & Reservation         [Hong Jia Bao]");
            System.out.println("4. Waitlist Management           [Xin Yee]");
            System.out.println("0. Exit System");
            System.out.print("Select module to enter: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println(">>> Entering Resident Module...");
                    break;
                    
                case "2":
                    System.out.println(">>> Entering Charging Station Module...");
                    break;
                    
                case "3":
                    System.out.println(">>> Entering Booking Module...");
                    BookingMenu bookingUI = new BookingMenu(bookingManager, loggedInUser);
                    bookingUI.start();
                    break;
                    
                case "4":
                    System.out.println(">>> Entering Waitlist Module...");
                    break;
                    
                case "0":
                    System.out.println("Saving data... Exiting System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    
                default:
                    System.out.println("[ERROR] Invalid choice! Please select 0-4.");
            }
        }
>>>>>>> 23874fafd4e7b8a4ebcad886f559f49f917b91a6
    }
}