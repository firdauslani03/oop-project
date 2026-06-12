import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/* ============================================================
 * MEMBER 4 - SMART QUEUE & WAITLIST MANAGEMENT
 * ============================================================
 * OOP Concepts used in this file:
 *  - ENCAPSULATION  : private fields + getters in Resident, WaitlistEntry, QueueRecord
 *  - ABSTRACTION    : QueueStrategy interface hides HOW the next resident is chosen
 *  - POLYMORPHISM   : NormalQueueStrategy & PriorityQueueStrategy implement
 *                      QueueStrategy differently, but are used the SAME way
 *  - COMPOSITION    : WaitlistManager "has" a List<WaitlistEntry>, a QueueStrategy,
 *                      and a QueueHistory object
 *
 * ------------------------------------------------------------
 * MODULE BOUNDARY (interaction with Member 3 - Booking module)
 * ------------------------------------------------------------
 *   Booking SUCCESS (slot available)
 *        -> handled entirely by the Booking & Reservation module.
 *           This Waitlist module is NOT involved. A confirmed
 *           booking can never be displaced by anyone, priority
 *           or not.
 *
 *   Booking FAILS (slot already full)
 *        -> resident calls WaitlistManager.joinQueue(resident, stationId)
 *           They now have NO reservation, only a place in line.
 *
 *   Confirmed booking is CANCELLED (slot becomes free)
 *        -> Booking module calls WaitlistManager.promoteNext(stationId)
 *           The waitlist entry chosen (based on the active
 *           QueueStrategy) is removed from the waitlist and is
 *           turned into a NEW confirmed booking by the Booking
 *           module.
 *
 *   In short: Priority affects WHO MOVES UP THE WAITLIST FIRST,
 *   never WHO KEEPS / LOSES AN EXISTING RESERVATION.
 * ============================================================ */


/* ----------------------------------------------------------
 * 1. Resident class
 *    Represents a resident who can join the queue.
 *    ENCAPSULATION: fields are private, accessed via getters
 * ---------------------------------------------------------- */
class Resident {
    private String residentId;
    private String name;
    private boolean priorityMember; // true = priority queue member

    public Resident(String residentId, String name, boolean priorityMember) {
        this.residentId = residentId;
        this.name = name;
        this.priorityMember = priorityMember;
    }

    public String getResidentId() {
        return residentId;
    }

    public String getName() {
        return name;
    }

    public boolean isPriorityMember() {
        return priorityMember;
    }

    @Override
    public String toString() {
        return name + " (ID: " + residentId + (priorityMember ? ", PRIORITY" : "") + ")";
    }
}


/* ----------------------------------------------------------
 * 2. WaitlistEntry class
 *    Represents ONE record of a resident waiting for a station.
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
 *    Defines WHAT a strategy must be able to do,
 *    without saying HOW it picks the next resident.
 * ---------------------------------------------------------- */
interface QueueStrategy {
    /**
     * Decide which entry should be served next for a given station.
     */
    WaitlistEntry getNext(List<WaitlistEntry> queue, String stationId);

    /**
     * Name of the strategy, for display purposes.
     */
    String getStrategyName();
}


/* ----------------------------------------------------------
 * 4. NormalQueueStrategy (POLYMORPHISM - implementation #1)
 *    Simple First-Come-First-Served (FIFO).
 * ---------------------------------------------------------- */
class NormalQueueStrategy implements QueueStrategy {

    @Override
    public WaitlistEntry getNext(List<WaitlistEntry> queue, String stationId) {

        WaitlistEntry earliest = null;

        for (WaitlistEntry e : queue) {

            if (!e.getStationId().equals(stationId))
                continue;

            if (earliest == null ||
                e.getJoinTime().isBefore(earliest.getJoinTime())) {

                earliest = e;
            }
        }

        return earliest;
    }

    @Override
    public String getStrategyName() {
        return "Normal (FIFO)";
    }
}


/* ----------------------------------------------------------
 * 5. PriorityQueueStrategy (POLYMORPHISM - implementation #2)
 *
 *    IMPORTANT DESIGN NOTE (read this!):
 *    --------------------------------------------------------
 *    Priority does NOT affect CONFIRMED bookings. A resident
 *    who already has a confirmed reservation (handled by the
 *    Booking & Reservation module) can NEVER be displaced or
 *    "skipped" by a priority member. That would take away
 *    something the resident already has, which is unfair.
 *
 *    Priority ONLY affects the ORDER OF THE WAITLIST -- i.e.
 *    when a slot becomes free (due to a cancellation), who
 *    gets the FIRST CHANCE to be promoted into that slot.
 *
 *    Since EVERYONE in the waitlist currently has nothing
 *    (no reservation yet), letting priority members go first
 *    does not take anything away from anyone -- it only
 *    decides who gets the next "opportunity" first. This is
 *    why a simple Priority -> FIFO ordering is fair here, and
 *    no aging/quota/anti-starvation mechanism is required.
 *    --------------------------------------------------------
 * ---------------------------------------------------------- */
class PriorityQueueStrategy extends NormalQueueStrategy {

    @Override
    public WaitlistEntry getNext(List<WaitlistEntry> queue, String stationId) {

        WaitlistEntry priorityEntry = null;

        for (WaitlistEntry e : queue) {

            if (!e.getStationId().equals(stationId))
                continue;

            if (!e.getResident().isPriorityMember())
                continue;

            if (priorityEntry == null ||
                e.getJoinTime().isBefore(priorityEntry.getJoinTime())) {

                priorityEntry = e;
            }
        }

        // Found priority member
        if (priorityEntry != null) {
            return priorityEntry;
        }

        // No priority member -> use FIFO logic
        return super.getNext(queue, stationId);
    }

    @Override
    public String getStrategyName() {
        return "Priority-Based (Waitlist Only)";
    }
}


/* ----------------------------------------------------------
 * 6. QueueRecord class
 *    Stores ONE completed queue record (for history).
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

    public long getWaitMinutes() {
        return Duration.between(joinTime, promotedTime).toMinutes();
    }

    public String getStationId() {
        return stationId;
    }

    @Override
    public String toString() {
        return "Station " + stationId
                + " | Joined: " + joinTime
                + " | Promoted: " + promotedTime
                + " | Waited: " + getWaitMinutes() + " min";
    }
}


/* ----------------------------------------------------------
 * 7. QueueHistory class
 *    Keeps track of each resident's queue statistics:
 *      - number of times joined
 *      - list of completed records
 *      - average wait time
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
        if (list.isEmpty()) {
            return 0.0;
        }
        long total = 0;
        for (QueueRecord r : list) {
            total += r.getWaitMinutes();
        }
        return (double) total / list.size();
    }
}


/* ----------------------------------------------------------
 * 8. WaitlistManager class
 *    The "brain" of the module. Manages the live queue,
 *    applies the chosen strategy, and updates history.
 *
 *    COMPOSITION: this class is built FROM other objects
 *    (List<WaitlistEntry>, QueueStrategy, QueueHistory)
 * ---------------------------------------------------------- */
class WaitlistManager {

    private List<WaitlistEntry> queue = new ArrayList<>();
    private QueueStrategy strategy;          // can be swapped at runtime (polymorphism)
    private QueueHistory history = new QueueHistory();

    // Assumption: each charging session takes about 30 minutes
    private static final int ESTIMATED_MINUTES_PER_SLOT = 30;

    public WaitlistManager(QueueStrategy strategy) {
        this.strategy = strategy;
    }

    /** Change the queue ordering strategy at runtime. */
    public void setStrategy(QueueStrategy strategy) {
        this.strategy = strategy;
        System.out.println("[INFO] Queue strategy switched to: " + strategy.getStrategyName());
    }

    /* ---------- Feature 1: Join Waiting Queue ---------- */
    public void joinQueue(Resident resident, String stationId) {
        WaitlistEntry entry = new WaitlistEntry(resident, stationId);
        queue.add(entry);
        history.recordJoin(resident.getResidentId());
        System.out.println("[JOINED]  " + entry);
    }

    /* ---------- Feature 2: Leave Queue ---------- */
    public boolean leaveQueue(Resident resident, String stationId) {
        Optional<WaitlistEntry> found = queue.stream()
                .filter(e -> e.getResident().getResidentId().equals(resident.getResidentId())
                        && e.getStationId().equals(stationId))
                .findFirst();

        if (found.isPresent()) {
            queue.remove(found.get());
            System.out.println("[LEFT]    " + resident.getName()
                    + " left the queue for Station " + stationId);
            return true;
        }

        System.out.println("[ERROR]   " + resident.getName()
                + " is not in the queue for Station " + stationId);
        return false;
    }

    /* ---------- Feature 3: View Queue Status ---------- */
    public void viewQueueStatus(Resident resident, String stationId) {
        List<WaitlistEntry> ordered = getOrderedQueue(stationId);

        int position = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getResident().getResidentId().equals(resident.getResidentId())) {
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

    /* ---------- Feature 4: Automatic Queue Promotion ---------- */
    public void promoteNext(String stationId) {
        WaitlistEntry next = strategy.getNext(queue, stationId);

        if (next == null) {
            System.out.println("[PROMOTE] No residents waiting for Station " + stationId);
            return;
        }

        queue.remove(next);

        LocalDateTime now = LocalDateTime.now();
        QueueRecord record = new QueueRecord(stationId, next.getJoinTime(), now);
        history.recordPromotion(next.getResident().getResidentId(), record);

        System.out.println("[PROMOTE] " + next.getResident().getName()
                + " has been assigned to Station " + stationId
                + " (waited " + record.getWaitMinutes() + " min)");
    }

    /* ---------- Feature 6: Queue History ---------- */
    public void viewQueueHistory(Resident resident) {
        String id = resident.getResidentId();
        System.out.println("\n--- Queue History: " + resident.getName() + " ---");
        System.out.println("Number of times joined queue : " + history.getJoinCount(id));

        List<QueueRecord> records = history.getRecords(id);
        if (records.isEmpty()) {
            System.out.println("Completed records           : none yet");
        } else {
            System.out.println("Completed records:");
            for (QueueRecord r : records) {
                System.out.println("   - " + r);
            }
        }

        System.out.printf("Average waiting time         : %.2f min%n",
                history.getAverageWaitTime(id));
    }

    /* ---------- Helper: get queue in current strategy order ---------- */
    private List<WaitlistEntry> getOrderedQueue(String stationId) {
        List<WaitlistEntry> remaining = queue.stream()
                .filter(e -> e.getStationId().equals(stationId))
                .collect(Collectors.toList());

        List<WaitlistEntry> ordered = new ArrayList<>();
        while (!remaining.isEmpty()) {
            WaitlistEntry next = strategy.getNext(remaining, stationId);
            if (next == null) {
                break;
            }
            ordered.add(next);
            remaining.remove(next);
        }
        return ordered;
    }

    /* ---------- Helper: print current queue order ---------- */
    public void printCurrentQueue(String stationId) {
        System.out.println("\n--- Current Queue: Station " + stationId
                + " | Strategy: " + strategy.getStrategyName() + " ---");

        List<WaitlistEntry> ordered = getOrderedQueue(stationId);
        if (ordered.isEmpty()) {
            System.out.println("(empty)");
            return;
        }

        int position = 1;
        for (WaitlistEntry e : ordered) {
            System.out.println(position + ". " + e.getResident());
            position++;
        }
    }
}


/* ============================================================
 * 9. Main class - demonstrates all features
 * ============================================================ */
public class SmartQueueManagement {

    public static void main(String[] args) {

        // Create some residents
        Resident alice   = new Resident("R001", "Alice",   false);
        Resident bob     = new Resident("R002", "Bob",     true);  // priority member
        Resident charlie = new Resident("R003", "Charlie", false);
        Resident diana   = new Resident("R004", "Diana",   true);  // priority member

        String stationId = "FAST-01";

        // Start with the Normal (FIFO) strategy
        WaitlistManager manager = new WaitlistManager(new NormalQueueStrategy());

        System.out.println("===== STEP 1: Residents join the queue (Normal Strategy) =====");
        manager.joinQueue(alice, stationId);
        manager.joinQueue(charlie, stationId);
        manager.printCurrentQueue(stationId);

        System.out.println("\n===== STEP 2: Switch to Priority Strategy =====");
        manager.setStrategy(new PriorityQueueStrategy());
        manager.joinQueue(bob, stationId);     // joins later, but is a priority member
        manager.joinQueue(diana, stationId);   // joins later, also priority member
        manager.printCurrentQueue(stationId);

        System.out.println("\n===== STEP 3: View queue status =====");
        manager.viewQueueStatus(alice, stationId);
        manager.viewQueueStatus(bob, stationId);

        System.out.println("\n===== STEP 4: A booking is cancelled -> automatic promotion =====");
        manager.promoteNext(stationId);   // Bob (priority, earliest) should be promoted first
        manager.printCurrentQueue(stationId);

        System.out.println("\n===== STEP 5: Another cancellation -> next promotion =====");
        manager.promoteNext(stationId);   // Diana (priority) should be promoted next
        manager.printCurrentQueue(stationId);

        System.out.println("\n===== STEP 6: Charlie leaves the queue voluntarily =====");
        manager.leaveQueue(charlie, stationId);
        manager.printCurrentQueue(stationId);

        System.out.println("\n===== STEP 7: View queue history =====");
        manager.viewQueueHistory(bob);
        manager.viewQueueHistory(diana);
        manager.viewQueueHistory(alice);
        manager.viewQueueHistory(charlie);
    }
}