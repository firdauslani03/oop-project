import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueHistory {
    private Map<String, Integer> joinCount = new HashMap<>();
    private Map<String, List<QueueRecord>> records = new HashMap<>();

    public void recordJoin(String residentId) {
        joinCount.put(residentId, joinCount.getOrDefault(residentId, 0) + 1);
    }

    public void recordPromotion(String residentId, QueueRecord record) {
        records.computeIfAbsent(residentId, k -> new ArrayList<>()).add(record);
    }

    public int getJoinCount(String residentId) { 
        return joinCount.getOrDefault(residentId, 0); 
    }

    public List<QueueRecord> getRecords(String residentId) { 
        return records.getOrDefault(residentId, new ArrayList<>()); 
    }

    public double getAverageWaitTime(String residentId) {
        List<QueueRecord> list = getRecords(residentId);
        if (list.isEmpty()) return 0.0;
        long total = 0;
        for (QueueRecord r : list) total += r.getWaitMinutes();
        return (double) total / list.size();
    }
}
