import java.time.LocalDateTime;

public class WaitlistEntry {
    private Resident resident;
    private String stationId;
    private LocalDateTime joinTime;

    public WaitlistEntry(Resident resident, String stationId) {
        this.resident = resident;
        this.stationId = stationId;
        this.joinTime = LocalDateTime.now();
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

    @Override
    public String toString() {
        return resident.getName() + " -> Station " + stationId + " (joined: " + joinTime + ")";
    }
}