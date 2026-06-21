import java.util.ArrayList;
import java.util.Vector;

public class QueueHistory {
    private Vector<String> joinCount = new Vector<>();
    private Vector<PromotionRecord> records = new Vector<>();

    private static class PromotionRecord {
        private final String residentId;
        private final QueueRecord record;

        public PromotionRecord(String residentId, QueueRecord record) {
            this.residentId = residentId;
            this.record = record;
        }

        public String getResidentId() {
            return residentId;
        }

        public QueueRecord getRecord() {
            return record;
        }
    }

    public void recordJoin(String residentId) {
        joinCount.add(residentId);
    }

    public void recordPromotion(String residentId, QueueRecord record) {
        records.add(new PromotionRecord(residentId, record));
    }

    public int getJoinCount(String residentId) { 
        int count = 0;
        for (String id : joinCount) {
            if (id.equals(residentId)) {
                count++;
            }
        }
        return count; 
    }

    public ArrayList<QueueRecord> getRecords(String residentId) { 
        ArrayList<QueueRecord> residentRecords = new ArrayList<>();
        for (PromotionRecord pr : records) {
            if (pr.getResidentId().equals(residentId)) {
                residentRecords.add(pr.getRecord());
            }
        }
        return residentRecords; 
    }

    public double getAverageWaitTime(String residentId) {
        ArrayList<QueueRecord> list = getRecords(residentId);
        if (list.isEmpty()) return 0.0;
        long total = 0;
        for (QueueRecord r : list) total += r.getWaitMinutes();
        return (double) total / list.size();
    }
}
