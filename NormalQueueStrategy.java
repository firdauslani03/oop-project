// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
import java.util.ArrayList;

public class NormalQueueStrategy implements QueueStrategy {

    @Override
    public WaitlistEntry getNext(ArrayList<WaitlistEntry> queue, String stationId) {
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
