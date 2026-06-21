// [Module: Booking & Reservation Management — Hong Jia Bao, Member 3]

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking 
{
    private String bookingId;
    private Resident resident;
    private ChargingStation station;
    private TimeSlot timeSlot;
    private String status; 

    public Booking (String bookingId, Resident resident, ChargingStation station, LocalDate date, LocalTime start, LocalTime end) 
    {
        this.bookingId = bookingId;
        this.resident = resident;
        this.station = station;
        this.timeSlot = new TimeSlot(date, start, end);
        this.status = "Active"; 
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public void setStation(ChargingStation station) {
        this.station = station;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Resident getResident() {
        return resident;
    }

    public ChargingStation getStation() {
        return station;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public double getDuration() {
        return timeSlot.calculateDurationInMinutes();
    }

    @Override
    public String toString()
    {
        return "Booking ID: " + bookingId + "\nResident: " + resident.getName() + " (" + resident.getResidentId() + ")" + "\nCharger: " + station.getStationId() + "\nDate: " + timeSlot.getDate() + "\nTime: " + timeSlot.getStartTime() + " to " + timeSlot.getEndTime() + "\nStatus: " + status;
    }
}