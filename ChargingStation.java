import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing a charging station in the EV community.
 * FastChargingStation and NormalChargingStation extend this class (Inheritance),
 * and both override displayInfo() to show their own details (Polymorphism).
 * ChargingStation is associated with Booking (a station can have many bookings,
 * but does not own them).
 */
public abstract class ChargingStation {
    private String stationId;
    private String location;
    private String status; // "Active", "Inactive", "Maintenance"
    private boolean underMaintenance;
    private List<String> bookingIds; // Association: tracks bookings made at this station

    public ChargingStation(String stationId, String location) {
        this.stationId = stationId;
        this.location = location;
        this.status = "Active";
        this.underMaintenance = false;
        this.bookingIds = new ArrayList<>();
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isUnderMaintenance() {
        return underMaintenance;
    }

    public void setUnderMaintenance(boolean underMaintenance) {
        this.underMaintenance = underMaintenance;
        if (underMaintenance) {
            this.status = "Maintenance";
        }
    }

    public boolean isAvailable() {
        return status.equals("Active") && !underMaintenance;
    }

    public void addBookingRecord(String bookingId) {
        bookingIds.add(bookingId);
    }

    public List<String> getBookingIds() {
        return bookingIds;
    }

    public int getTotalBookings() {
        return bookingIds.size();
    }

    /** Returns the type label, e.g. "Fast" or "Normal". Used for polymorphic display. */
    public abstract String getStationType();

    /** Each station subtype displays its info differently (Polymorphism). */
    public abstract String displayInfo();

    @Override
    public String toString() {
        return stationId;
    }
}
