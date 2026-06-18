import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingManager{

    private List<Booking> bookingList;

    public BookingManager() {
        this.bookingList = new ArrayList<>();
    }

    public void checkConflict(String chargerId, LocalDate newDate, LocalTime newStart, LocalTime newEnd) throws BookingConflictException 
    {
        if (newDate.isBefore(LocalDate.now())) {
            throw new BookingConflictException("Error: Cannot book a date in the past!");
        }

        for (Booking existingBooking : bookingList) {
            
    
            if (existingBooking.getStatus().equals("Active") && existingBooking.getChargerId().equals(chargerId)) {
                TimeSlot existingSlot = existingBooking.getTimeSlot();

        
                if (existingSlot.getDate().equals(newDate)) {

                    LocalTime existingStart = existingSlot.getStartTime();
                    LocalTime existingEnd = existingSlot.getEndTime();
                    if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                        throw new BookingConflictException("Error: Time slot overlaps with an existing booking!");
                    }
                }
            }
        }
    }

    public void createBooking(String bookingId, String userId, String chargerId, LocalDate date, LocalTime start, LocalTime end) 
    {
        try {
            checkConflict(chargerId, date, start, end);
            
            TimeSlot newSlot = new TimeSlot(date, start, end); 
            Booking newBooking = new Booking(bookingId, userId, chargerId, newSlot);
            
            bookingList.add(newBooking);
            System.out.println("Success! Booking " + bookingId + " has been created.");
            
        } catch (BookingConflictException e) {
           
            System.out.println(e.getMessage());
        }
    }

    public void viewAllBookings() {
        System.out.println("\n All Bookings : ");
        for (Booking b : bookingList) {
            System.out.println(b.toString());
            System.out.println("Duration: " + b.getDuration() + " minutes\n");
        }
    }
}