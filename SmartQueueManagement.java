import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/* ============================================================
 * MEMBER 4 - SMART QUEUE & WAITLIST MANAGEMENT
 * ============================================================
 * OOP Concepts used in this file:
 *  - ENCAPSULATION  : private fields + getters in Resident, WaitlistEntry, QueueRecord
 *  - ABSTRACTION    : QueueStrategy interface hides HOW the next resident is chosen
 *  - POLYMORPHISM   : QueueStrategy is implemented by NormalQueueStrategy.
 *  - COMPOSITION    : WaitlistManager "has" a List<WaitlistEntry>, a QueueStrategy,
 *                      and a QueueHistory object
 *
 * Exception Handling:
 *  - ResidentAlreadyInQueueException : thrown when a resident tries to join a queue
 *                                       they are already in
 *  - EmptyQueueException             : thrown when promoteNext() is called on an
 *                                       empty queue for a given station
 *  - InvalidQueueSelectionException  : thrown when a null/blank stationId is given
 *
 * File I/O:
 *  - queue.txt        : persists the current live waitlist (written on every change)
 *  - queueHistory.txt : persists completed promotion records (appended on each promotion)
 * ============================================================ */


/* ----------------------------------------------------------
 * Custom Exceptions
 * ---------------------------------------------------------- */

/** Thrown when a resident attempts to join a queue they are already in. */
class ResidentAlreadyInQueueException extends Exception {
    public ResidentAlreadyInQueueException(String residentName, String stationId) {
        super(residentName + " is already in the queue for Station " + stationId);
    }
}

/** Thrown when promoteNext() is called but no residents are waiting for that station. */
class EmptyQueueException extends Exception {
    public EmptyQueueException(String stationId) {
        super("The queue for Station " + stationId + " is empty — no one to promote.");
    }
}

/** Thrown when a null or blank stationId is provided. */
class InvalidQueueSelectionException extends Exception {
    public InvalidQueueSelectionException(String detail) {
        super("Invalid queue selection: " + detail);
    }
}


/* ----------------------------------------------------------
 * 1. Resident class
 * ---------------------------------------------------------- */
class Resident {
    private String residentId;
    private String name;

    public Resident(String residentId, String name) {
        this.residentId = residentId;
        this.name = name;
    }

    public String getResidentId() { 
        return residentId; 
    }

    public String getName() { 
        return name; 
    }

    @Override
    public String toString() {
        return name + " (ID: " + residentId + ")";
    }
}


/* ----------------------------------------------------------
 * 2. WaitlistEntry class
 * ---------------------------------------------------------- */
class WaitlistEntry {
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


/* ----------------------------------------------------------
 * 3. QueueStrategy interface (ABSTRACTION)
 * ---------------------------------------------------------- */
interface QueueStrategy {
    WaitlistEntry getNext(List<WaitlistEntry> queue, String stationId);
    String getStrategyName();
}


/* ----------------------------------------------------------
 * 4. NormalQueueStrategy — FIFO (POLYMORPHISM)
 * ---------------------------------------------------------- */
class NormalQueueStrategy implements QueueStrategy {

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


/* ----------------------------------------------------------
 * 5. QueueRecord class
 * ---------------------------------------------------------- */
class QueueRecord {
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

    /** Serialize to a single CSV line for queueHistory.txt. */
    public String toCsvLine(String residentId, String residentName) {
        return residentId + "," + residentName + "," + stationId + ","
                + joinTime + "," + promotedTime;
    }
}


/* ----------------------------------------------------------
 * 6. QueueHistory class
 * ---------------------------------------------------------- */
class QueueHistory {
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


/* ----------------------------------------------------------
 * 7. WaitlistManager class (COMPOSITION)
 *
 *    File I/O contract
 *    -----------------
 *    queue.txt  (overwritten on every mutation)
 *      Format per line:  residentId,residentName,stationId,joinTime
 *      Example:          R001,Alice,FAST-01,2025-06-01T09:00:00
 *
 *    queueHistory.txt  (appended on every promotion)
 *      Format per line:  residentId,residentName,stationId,joinTime,promotedTime
 *      Example:          R001,Alice,FAST-01,2025-06-01T09:00:00,2025-06-01T09:45:00
 * ---------------------------------------------------------- */
class WaitlistManager {

    private static final String QUEUE_FILE = "queue.txt";
    private static final String HISTORY_FILE = "queueHistory.txt";
    private static final int ESTIMATED_MINUTES_PER_SLOT = 30;

    private List<WaitlistEntry> queue = new ArrayList<>();
    private QueueStrategy strategy;
    private QueueHistory history = new QueueHistory();

    public WaitlistManager(QueueStrategy strategy) {
        this.strategy = strategy;
    }

    // ================================================================
    //  VALIDATION HELPER
    // ================================================================

    /**
     * Validates that stationId is not null or blank.
     *
     * @throws InvalidQueueSelectionException if stationId is invalid
     */
    private void validateStationId(String stationId) throws InvalidQueueSelectionException {
        if (stationId == null || stationId.trim().isEmpty()) {
            throw new InvalidQueueSelectionException(
                    "stationId cannot be null or blank.");
        }
    }

    // ================================================================
    //  Feature 1 : Join Queue
    // ================================================================

    /**
     * Adds a resident to the waitlist for the given station.
     *
     * @throws InvalidQueueSelectionException  if stationId is null/blank
     * @throws ResidentAlreadyInQueueException if resident is already waiting at that station
     */
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

    // ================================================================
    //  Feature 2 : Leave Queue
    // ================================================================

    /**
     * Removes a resident from the waitlist for the given station.
     *
     * @throws InvalidQueueSelectionException if stationId is null/blank
     */
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

    // ================================================================
    //  Feature 3 : View Queue Status
    // ================================================================

    /**
     * Prints a resident's current position and estimated wait for a station.
     *
     * @throws InvalidQueueSelectionException if stationId is null/blank
     */
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

    // ================================================================
    //  Feature 4 : Promote Next (called by Booking module on cancellation)
    // ================================================================

    /**
     * Promotes the next resident in line for the given station.
     *
     * @throws InvalidQueueSelectionException if stationId is null/blank
     * @throws EmptyQueueException if no residents are waiting
     */
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

    // ================================================================
    //  Feature 6 : Queue History
    // ================================================================

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

    // ================================================================
    //  Helper : print current queue
    // ================================================================

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

    // ================================================================
    //  File I/O — queue.txt
    // ================================================================

    /**
     * Overwrites queue.txt with the current state of the live waitlist.
     * Each line: residentId,residentName,stationId,joinTime
     */
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

    // ================================================================
    //  File I/O — queueHistory.txt
    // ================================================================

    /**
     * Appends one promotion record to queueHistory.txt.
     * Format: residentId,residentName,stationId,joinTime,promotedTime
     */
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


/* ============================================================
 * 8. Main class — demonstrates all features
 *    Exception handling is shown with try-catch blocks.
 * ============================================================ */
public class SmartQueueManagement {

    public static void main(String[] args) {

        Resident alice = new Resident("R001", "Alice");
        Resident bob = new Resident("R002", "Bob");
        Resident charlie = new Resident("R003", "Charlie");
        Resident diana = new Resident("R004", "Diana");

        String stationId = "FAST-01";

        WaitlistManager manager = new WaitlistManager(new NormalQueueStrategy());

        // ----------------------------------------------------------------
        System.out.println("===== STEP 1: Residents join the queue =====");
        // ----------------------------------------------------------------
        try {
            manager.joinQueue(alice,   stationId);
            manager.joinQueue(charlie, stationId);
            manager.joinQueue(bob,     stationId);
            manager.joinQueue(diana,   stationId);
        } catch (InvalidQueueSelectionException | ResidentAlreadyInQueueException e) {
            System.err.println("[EXCEPTION] " + e.getMessage());
        }
        manager.printCurrentQueue(stationId);

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 2: Duplicate join attempt (Exception demo) =====");
        // ----------------------------------------------------------------
        try {
            manager.joinQueue(alice, stationId);   // Alice is already in queue
        } catch (ResidentAlreadyInQueueException e) {
            System.err.println("[EXCEPTION] ResidentAlreadyInQueueException: " + e.getMessage());
        } catch (InvalidQueueSelectionException e) {
            System.err.println("[EXCEPTION] InvalidQueueSelectionException: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 3: Invalid station ID (Exception demo) =====");
        // ----------------------------------------------------------------
        try {
            manager.joinQueue(alice, "");    // blank stationId
        } catch (InvalidQueueSelectionException e) {
            System.err.println("[EXCEPTION] InvalidQueueSelectionException: " + e.getMessage());
        } catch (ResidentAlreadyInQueueException e) {
            System.err.println("[EXCEPTION] ResidentAlreadyInQueueException: " + e.getMessage());
        }

        try {
            manager.joinQueue(alice, null);  // null stationId
        } catch (InvalidQueueSelectionException e) {
            System.err.println("[EXCEPTION] InvalidQueueSelectionException: " + e.getMessage());
        } catch (ResidentAlreadyInQueueException e) {
            System.err.println("[EXCEPTION] ResidentAlreadyInQueueException: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 4: View queue status =====");
        // ----------------------------------------------------------------
        try {
            manager.viewQueueStatus(alice, stationId);
            manager.viewQueueStatus(bob,   stationId);
        } catch (InvalidQueueSelectionException e) {
            System.err.println("[EXCEPTION] " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 5: Booking cancellation -> automatic promotion =====");
        // ----------------------------------------------------------------
        try {
            manager.promoteNext(stationId);   // Alice promoted
            manager.printCurrentQueue(stationId);
            manager.promoteNext(stationId);   // Charlie promoted
            manager.printCurrentQueue(stationId);
        } catch (InvalidQueueSelectionException | EmptyQueueException e) {
            System.err.println("[EXCEPTION] " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 6: Diana leaves voluntarily =====");
        // ----------------------------------------------------------------
        try {
            manager.leaveQueue(diana, stationId);
            manager.printCurrentQueue(stationId);
        } catch (InvalidQueueSelectionException e) {
            System.err.println("[EXCEPTION] " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 7: Empty queue promotion (Exception demo) =====");
        // ----------------------------------------------------------------
        try {
            manager.promoteNext(stationId);   // only Bob left
            manager.promoteNext(stationId);   // now truly empty -> exception
        } catch (EmptyQueueException e) {
            System.err.println("[EXCEPTION] EmptyQueueException: " + e.getMessage());
        } catch (InvalidQueueSelectionException e) {
            System.err.println("[EXCEPTION] InvalidQueueSelectionException: " + e.getMessage());
        }

        // ----------------------------------------------------------------
        System.out.println("\n===== STEP 8: Queue history =====");
        // ----------------------------------------------------------------
        manager.viewQueueHistory(alice);
        manager.viewQueueHistory(charlie);
        manager.viewQueueHistory(bob);
        manager.viewQueueHistory(diana);
    }
}