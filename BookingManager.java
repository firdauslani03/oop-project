import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import Exception.*;
import java.io.*;

public class BookingManager{

    private ArrayList<Booking> bookingList;
    private WaitlistManager waitlistManager;
    private StationManager stationManager;

    private static final String BOOKING_FILE = "booking.txt";

    public BookingManager(WaitlistManager waitlistManager, StationManager stationManager) {
        this.bookingList = new ArrayList<>();
        this.waitlistManager = waitlistManager;
        this.stationManager = stationManager;
        loadBookingsFromFile();
    }

    public void checkConflict(ChargingStation station, LocalDate newDate, LocalTime newStart, LocalTime newEnd) throws BookingConflictException 
    {
        if (newDate.isBefore(LocalDate.now())) {
            throw new BookingConflictException("Error: Cannot book a date in the past!");
        }

        for (Booking existingBooking : bookingList) {
            
    
            if (existingBooking.getStatus().equals("Active") && existingBooking.getStation().getStationId().equals(station.getStationId())) {
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

    public void createBooking(String bookingId, Resident resident, ChargingStation station, LocalDate date, LocalTime start, LocalTime end)
    {
        try {
            checkConflict(station, date, start, end);
            
            TimeSlot newSlot = new TimeSlot(date, start, end); 
            Booking newBooking = new Booking(bookingId, resident, station, newSlot);
            
            bookingList.add(newBooking);
            System.out.println("Success! Booking " + bookingId + " has been created.");
            saveBookingsToFile();

        } catch (BookingConflictException e) {
           
            System.out.println(e.getMessage());
        }
    }

    public void cancelBooking(String bookingId) {
        for (Booking b : bookingList) {
            if (b.getBookingId().equals(bookingId) && b.getStatus().equals("Active")) {
                b.setStatus("Cancelled");
                System.out.println("Booking " + bookingId + " has been successfully cancelled.");
                saveBookingsToFile();

                try {
                    System.out.println("Checking waitlist for Station " + b.getStation().getStationId() + "...");
                    waitlistManager.promoteNext(b.getStation().getStationId());
                } catch (EmptyQueueException e) {
                    System.out.println("No one is waiting for this station. Slot is completely free.");
                } catch (InvalidQueueSelectionException e) {
                    System.out.println("[ERROR] " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("Booking not found or already cancelled.");
    }

    public Booking findBooking(String bookingId) {
        for (Booking b : bookingList) {
            if (b.getBookingId().equals(bookingId)) {
                return b;
            }
        }
        return null;
    }

    public void viewAllBookings() {
        System.out.println("\n All Bookings : ");
        for (Booking b : bookingList) {
            System.out.println(b.toString());
            System.out.println("Duration: " + b.getDuration() + " minutes\n");
        }
    }

    public void saveBookingsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKING_FILE))) {
            for (Booking b : bookingList) {
                writer.println(b.getBookingId() + "," + b.getResident().getResidentId() + "," + b.getStation().getStationId() + "," + b.getTimeSlot().getDate() + "," + b.getTimeSlot().getStartTime() + "," + b.getTimeSlot().getEndTime() + "," + b.getStatus());
            }
        } catch (IOException e) {
            System.err.println("File Error: Writing failed to " + BOOKING_FILE);
        }
    }

    public void loadBookingsFromFile() {
        File file = new File(BOOKING_FILE);
        if (!file.exists()) 
        {
            return;
        }

        try (java.util.Scanner scan = new java.util.Scanner(file)) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] data = line.split(",");

                if (data.length == 7) {
                    try {
                        String bookingId = data[0];
                        String residentId = data[1];
                        String stationId = data[2];
                        LocalDate date = LocalDate.parse(data[3]);
                        LocalTime startTime = LocalTime.parse(data[4]);
                        LocalTime endTime = LocalTime.parse(data[5]);
                        String status = data[6];

                        Resident loadedResident = new Resident(residentId, "Loaded User", "", "", "");
                        ChargingStation loadedStation = (stationManager != null)
                                ? stationManager.findStation(stationId)
                                : null;
                        if (loadedStation == null) {
                            System.err.println("Skipping booking " + bookingId
                                    + ": station '" + stationId + "' not found.");
                            continue;
                        }

                        TimeSlot slot = new TimeSlot(date, startTime, endTime);
                        Booking b = new Booking(bookingId, loadedResident, loadedStation, slot);
                        b.setStatus(status); 
                        
                        bookingList.add(b);
                    } catch (Exception e) {
                        System.err.println("Skipping corrupted line: " + line);
                    }
                }
            }
            System.out.println("[System] Loaded " + bookingList.size() + " bookings from " + BOOKING_FILE);
            
        } catch (java.io.FileNotFoundException e) {
            System.err.println("File Error: Could not find " + BOOKING_FILE);
        }
    }
}