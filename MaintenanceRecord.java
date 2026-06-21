import java.time.LocalDate;

/**
 * Represents a single maintenance event logged against a charging station.
 */
public class MaintenanceRecord {
    private String stationId;
    private LocalDate date;
    private String reason;
    private String status; // "Ongoing" or "Resolved"

    public MaintenanceRecord(String stationId, LocalDate date, String reason, String status) {
        this.stationId = stationId;
        this.date = date;
        this.reason = reason;
        this.status = status;
    }

    public String getStationId() {
        return stationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Station: " + stationId
                + " | Date: " + date
                + " | Reason: " + reason
                + " | Status: " + status;
    }
}
