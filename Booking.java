public class Booking 
{
    private String bookingId;
    private String userId;
    private String chargerId;
    private TimeSlot timeSlot;
    private String status; 

    public Booking (String bookingId, String userId, String chargerId, TimeSlot timeSlot) 
    {
        this.bookingId = bookingId;
        this.userId = userId;
        this.chargerId = chargerId;
        this.timeSlot = timeSlot;
        this.status = "Active"; 
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
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

    public String getUserId() {
        return userId;
    }

    public String getChargerId() {
        return chargerId;
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
        return "Booking ID: " + bookingId + "\nUser: " + userId + "\nCharger: " + chargerId + "\nDate: " + timeSlot.getDate() + "\nTime: " + timeSlot.getStartTime() + " to " + timeSlot.getEndTime() + "\nStatus: " + status;
    }
}