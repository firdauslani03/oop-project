import java.util.List;

interface QueueStrategy {
    WaitlistEntry getNext(List<WaitlistEntry> queue, String stationId);
    String getStrategyName();
}

public class NormalQueueStrategy implements QueueStrategy {

    @Override
    public WaitlistEntry getNext(List<WaitlistEntry> queue, String stationId) {
        WaitlistEntry earliest = null;
        for (WaitlistEntry e : queue) {
            if (!e.getStationId().equals(stationId)) continue;
            if (earliest == null || e.getJoinTime().isBefore(earliest.getJoinTime())) {
                earliest = e;
            }
        }
        return earliest;
    }

    @Override
    public String getStrategyName() { return "Normal (FIFO)"; }
}
