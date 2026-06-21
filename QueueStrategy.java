import java.util.ArrayList;

public interface QueueStrategy {
    WaitlistEntry getNext(ArrayList<WaitlistEntry> queue, String stationId);
    String getStrategyName();
}
