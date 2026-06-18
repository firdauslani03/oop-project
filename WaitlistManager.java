import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import Exception.*;

public class WaitlistManager {

    private static final String QUEUE_FILE = "queue.txt";
    private static final String HISTORY_FILE = "queueHistory.txt";
    private static final int ESTIMATED_MINUTES_PER_SLOT = 30;

    private List<WaitlistEntry> queue = new ArrayList<>();
    private QueueStrategy strategy;
    private QueueHistory history = new QueueHistory();

    public WaitlistManager(QueueStrategy strategy) {
        this.strategy = strategy;
    }

    private void validateStationId(String stationId) throws InvalidQueueSelectionException {
        if (stationId == null || stationId.trim().isEmpty()) {
            throw new InvalidQueueSelectionException(
                    "stationId cannot be null or blank.");
        }
    }

    
    //@throws InvalidQueueSelectionException  if stationId is null/blank
    //@throws ResidentAlreadyInQueueException if resident is already waiting at that station
    
    public void joinQueue(Resident resident, String stationId)
            throws InvalidQueueSelectionException, ResidentAlreadyInQueueException {

        validateStationId(stationId);

        // Check for duplicate
        for (WaitlistEntry e : queue) {
            if (e.getResident().getResidentId().equals(resident.getResidentId())
                    && e.getStationId().equals(stationId)) {
                throw new ResidentAlreadyInQueueException(resident.getName(), stationId);
            }
        }

        WaitlistEntry entry = new WaitlistEntry(resident, stationId);
        queue.add(entry);
        history.recordJoin(resident.getResidentId());
        System.out.println("[JOINED]  " + entry);
        saveQueueToFile();
    }

    //@throws InvalidQueueSelectionException if stationId is null/blank
    public boolean leaveQueue(Resident resident, String stationId)
            throws InvalidQueueSelectionException {

        validateStationId(stationId);

        for (WaitlistEntry e : queue) {
            if (e.getResident().getResidentId().equals(resident.getResidentId())
                    && e.getStationId().equals(stationId)) {
                queue.remove(e);
                System.out.println("[LEFT]    " + resident.getName()
                        + " left the queue for Station " + stationId);
                saveQueueToFile();
                return true;
            }
        }

        System.out.println("[ERROR]   " + resident.getName()
                + " is not in the queue for Station " + stationId);
        return false;
    }

    //@throws InvalidQueueSelectionException if stationId is null/blank
    public void viewQueueStatus(Resident resident, String stationId)
            throws InvalidQueueSelectionException {

        validateStationId(stationId);

        List<WaitlistEntry> ordered = getOrderedQueue(stationId);
        int position = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getResident().getResidentId()
                    .equals(resident.getResidentId())) {
                position = i + 1;
                break;
            }
        }

        if (position == -1) {
            System.out.println("[STATUS]  " + resident.getName()
                    + " is not currently in the queue for Station " + stationId);
            return;
        }

        int estimatedWait = (position - 1) * ESTIMATED_MINUTES_PER_SLOT;
        System.out.println("[STATUS]  " + resident.getName()
                + " | Station " + stationId
                + " | Position: " + position
                + " | Estimated wait: " + estimatedWait + " min");
    }

    // @throws InvalidQueueSelectionException if stationId is null/blank
    // @throws EmptyQueueException if no residents are waiting
    public void promoteNext(String stationId)
            throws InvalidQueueSelectionException, EmptyQueueException {

        validateStationId(stationId);

        WaitlistEntry next = strategy.getNext(queue, stationId);
        if (next == null) {
            throw new EmptyQueueException(stationId);
        }

        queue.remove(next);

        LocalDateTime now    = LocalDateTime.now();
        QueueRecord   record = new QueueRecord(stationId, next.getJoinTime(), now);
        history.recordPromotion(next.getResident().getResidentId(), record);

        System.out.println("[PROMOTE] " + next.getResident().getName()
                + " has been assigned to Station " + stationId
                + " (waited " + record.getWaitMinutes() + " min)");

        saveQueueToFile();
        appendHistoryToFile(next.getResident(), record);
    }

    public void viewQueueHistory(Resident resident) {
        String id = resident.getResidentId();
        System.out.println("\n--- Queue History: " + resident.getName() + " ---");
        System.out.println("Times joined queue    : " + history.getJoinCount(id));

        List<QueueRecord> records = history.getRecords(id);
        if (records.isEmpty()) {
            System.out.println("Completed records     : none yet");
        } else {
            System.out.println("Completed records:");
            for (QueueRecord r : records) {
                System.out.println("   - " + r);
            }
        }
        System.out.printf("Average waiting time  : %.2f min%n",
                history.getAverageWaitTime(id));
    }

    public void printCurrentQueue(String stationId) {
        System.out.println("\n--- Current Queue: Station " + stationId
                + " | Strategy: " + strategy.getStrategyName() + " ---");
        List<WaitlistEntry> ordered = getOrderedQueue(stationId);
        if (ordered.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        int pos = 1;
        for (WaitlistEntry e : ordered) {
            System.out.println(pos++ + ". " + e.getResident());
        }
    }

    private List<WaitlistEntry> getOrderedQueue(String stationId) {
        List<WaitlistEntry> remaining = new ArrayList<>();
        for (WaitlistEntry e : queue) {
            if (e.getStationId().equals(stationId)) remaining.add(e);
        }
        List<WaitlistEntry> ordered = new ArrayList<>();
        while (!remaining.isEmpty()) {
            WaitlistEntry next = strategy.getNext(remaining, stationId);
            if (next == null) break;
            ordered.add(next);
            remaining.remove(next);
        }
        return ordered;
    }

    private void saveQueueToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUEUE_FILE))) {
            for (WaitlistEntry e : queue) {
                writer.write(e.getResident().getResidentId() + ","
                        + e.getResident().getName() + ","
                        + e.getStationId() + ","
                        + e.getJoinTime());
                writer.newLine();
            }
            System.out.println("[FILE]    queue.txt saved (" + queue.size() + " entries).");
        } catch (IOException ex) {
            System.err.println("[FILE ERROR] Could not save queue.txt: " + ex.getMessage());
        }
    }

    private void appendHistoryToFile(Resident resident, QueueRecord record) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(HISTORY_FILE, true))) {   // true = append mode
            writer.write(record.toCsvLine(
                    resident.getResidentId(), resident.getName()));
            writer.newLine();
            System.out.println("[FILE]    queueHistory.txt updated.");
        } catch (IOException ex) {
            System.err.println("[FILE ERROR] Could not write queueHistory.txt: " + ex.getMessage());
        }
    }
}