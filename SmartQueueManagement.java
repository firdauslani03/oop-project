// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
import Exception.*;


public class SmartQueueManagement {

    public static void main(String[] args) {

        Resident alice = new Resident("R001", "Alice", "alice123", "900101010101", "010-0000-000");
        Resident bob = new Resident("R002", "Bob", "bob123", "900202020202", "010-0000-001");
        Resident charlie = new Resident("R003", "Charlie", "charlie123", "900303030303", "010-0000-002");
        Resident diana = new Resident("R004", "Diana", "diana123", "900404040404", "010-0000-003");

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