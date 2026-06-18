import java.time.Duration;
import java.time.LocalDateTime;

public class QueueRecord {
    private String stationId;
    private LocalDateTime joinTime;
    private LocalDateTime promotedTime;

    public QueueRecord(String stationId, LocalDateTime joinTime, LocalDateTime promotedTime) {
        this.stationId = stationId;
        this.joinTime = joinTime;
        this.promotedTime = promotedTime;
    }

    public long   getWaitMinutes() { 
        return Duration.between(joinTime, promotedTime).toMinutes(); 
    }

    public String getStationId() { 
        return stationId; 
    }

    @Override
    public String toString() {
        return "Station " + stationId
                + " | Joined: "   + joinTime
                + " | Promoted: " + promotedTime
                + " | Waited: "   + getWaitMinutes() + " min";
    }

    public String toCsvLine(String residentId, String residentName) {
        return residentId + "," + residentName + "," + stationId + ","
                + joinTime + "," + promotedTime;
    }
}
