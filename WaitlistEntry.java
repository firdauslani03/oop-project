// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
import java.time.LocalDateTime;

public class WaitlistEntry {
    private Resident resident;
    private String stationId;
    private LocalDateTime joinTime;
    private TimeSlot requestedSlot; // may be null for entries with no specific requested time

    public WaitlistEntry(Resident resident, String stationId) {
        this.resident = resident;
        this.stationId = stationId;
        this.joinTime = LocalDateTime.now();
        this.requestedSlot = null;
    }

    public WaitlistEntry(Resident resident, String stationId, LocalDateTime joinTime) {
        this.resident = resident;
        this.stationId = stationId;
        this.joinTime = joinTime;
        this.requestedSlot = null;
    }

    public WaitlistEntry(Resident resident, String stationId, LocalDateTime joinTime, TimeSlot requestedSlot) {
        this.resident = resident;
        this.stationId = stationId;
        this.joinTime = joinTime;
        this.requestedSlot = requestedSlot;
    }

    public Resident getResident() { 
        return resident; 
    }

    public String getStationId() { 
        return stationId; 
    }

    public LocalDateTime getJoinTime() { 
        return joinTime;
    }

    public TimeSlot getRequestedSlot() {
        return requestedSlot;
    }

    @Override
    public String toString() {
        String base = resident.getName() + " -> Station " + stationId + " (joined: " + joinTime + ")";
        if (requestedSlot != null) {
            base += " | Requested: " + requestedSlot.getDate() + " "
                    + requestedSlot.getStartTime() + "-" + requestedSlot.getEndTime();
        }
        return base;
    }
}