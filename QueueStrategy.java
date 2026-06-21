// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
import java.util.ArrayList;

public interface QueueStrategy {
    WaitlistEntry getNext(ArrayList<WaitlistEntry> queue, String stationId);
    String getStrategyName();
}
