public class Main {
    public static void main(String[] args) {
        ResidentManager residentManager = new ResidentManager();
        WaitlistManager waitlistManager = new WaitlistManager(new NormalQueueStrategy());
        BookingManager bookingManager = new BookingManager(waitlistManager);

        ResidentMenuUI appUI = new ResidentMenuUI(residentManager, bookingManager);
        appUI.start();
    }
}
