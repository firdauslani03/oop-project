// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4; constructor call updated by Iman, Member 1, after the User/Resident refactor]
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import Exception.*;

public class WaitlistManager {

    private static final String QUEUE_FILE = "queue.txt";
    private static final int ESTIMATED_MINUTES_PER_SLOT = 30;

    private ArrayList<WaitlistEntry> queue = new ArrayList<>();
    private QueueStrategy strategy;
    private StationManager stationManager;
    private BookingManager bookingManager;
    public WaitlistManager(QueueStrategy strategy) {
        this.strategy = strategy;
        loadQueueFromFile();
    }
    public void setStationManager(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    public void setBookingManager(BookingManager bookingManager) {
        this.bookingManager = bookingManager;
    }

    public void validateStationId(String stationId) throws InvalidQueueSelectionException {
        if (stationId == null || stationId.trim().isEmpty()) {
            throw new InvalidQueueSelectionException("stationId cannot be null or blank.");
        }
        if (stationManager != null && stationManager.findStation(stationId) == null) {
            throw new InvalidQueueSelectionException("Station '" + stationId + "' does not exist.");
        }
    }
    
    public void joinQueue(Resident resident, String stationId) throws InvalidQueueSelectionException, ResidentAlreadyInQueueException {

        try {
            joinQueue(resident, stationId, null);
        } catch (BookingConflictException e) {
            throw new IllegalStateException("Unexpected conflict with no requested slot.", e);
        }
    }

    public void joinQueue(Resident resident, String stationId, TimeSlot requestedSlot) throws InvalidQueueSelectionException, ResidentAlreadyInQueueException, BookingConflictException {

        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null.");
        }
        if (resident.getResidentId() == null || resident.getResidentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Resident ID cannot be null or blank.");
        }
        validateStationId(stationId);

        for (WaitlistEntry e : queue) {
            if (e.getResident().getResidentId().equals(resident.getResidentId()) && e.getStationId().equals(stationId)) {
                throw new ResidentAlreadyInQueueException(resident.getName(), stationId);
            }
        }

        if (requestedSlot != null && bookingManager != null) {
            Booking currentBooking = bookingManager.getActiveBooking(stationId);
            if (currentBooking != null) {
                TimeSlot bookedSlot = currentBooking.getTimeSlot();
                boolean sameDate = bookedSlot.getDate().equals(requestedSlot.getDate());
                boolean overlaps = requestedSlot.getStartTime().isBefore(bookedSlot.getEndTime())
                        && requestedSlot.getEndTime().isAfter(bookedSlot.getStartTime());
                if (sameDate && overlaps) {
                    throw new BookingConflictException(
                        "Error: Requested time slot (" + requestedSlot.getDate() + " "
                        + requestedSlot.getStartTime() + "-" + requestedSlot.getEndTime()
                        + ") overlaps with Station " + stationId + "'s current booking ("
                        + bookedSlot.getStartTime() + "-" + bookedSlot.getEndTime()
                        + "). Choose a time after the current booking ends.");
                }
            }
        }
        
        WaitlistEntry entry;
        if (requestedSlot != null) {
            entry = new WaitlistEntry(resident, stationId, LocalDateTime.now(), requestedSlot);
        } else {
            entry = new WaitlistEntry(resident, stationId);
        }
        queue.add(entry);
        System.out.println("[JOINED]  " + entry);
        saveQueueToFile();
    }

    public boolean leaveQueue(Resident resident, String stationId) throws InvalidQueueSelectionException {

        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null.");
        }
        validateStationId(stationId);

        WaitlistEntry toRemove = null;
        for (WaitlistEntry e : queue) {
            if (e.getResident().getResidentId().equals(resident.getResidentId())
                    && e.getStationId().equals(stationId)) {
                toRemove = e;
                break;
            }
        }

        if (toRemove != null) {
            queue.remove(toRemove);
            System.out.println("[LEFT]    " + resident.getName()
                    + " left the queue for Station " + stationId);
            saveQueueToFile();
            return true;
        }

        System.out.println("[ERROR]   " + resident.getName()
                + " is not in the queue for Station " + stationId);
        return false;
    }

    public void viewQueueStatus(Resident resident, String stationId) throws InvalidQueueSelectionException {

        if (resident == null) {
            throw new IllegalArgumentException("Resident cannot be null.");
        }
        validateStationId(stationId);

        ArrayList<WaitlistEntry> ordered = getOrderedQueue(stationId);
        int pos = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getResident().getResidentId().equals(resident.getResidentId())) {
                pos = i + 1;
                break;
            }
        }

        if (pos == -1) {
            System.out.println("[STATUS]  " + resident.getName() + " is not currently in the queue for Station " + stationId);
            return;
        }

        int estimatedWait = (pos - 1) * ESTIMATED_MINUTES_PER_SLOT;
        System.out.println("[STATUS]  " + resident.getName()
                + " | Station " + stationId
                + " | Position: " + pos
                + " | Estimated wait: " + estimatedWait + " min");
    }

    public void promoteNext(String stationId) throws InvalidQueueSelectionException, EmptyQueueException {

        validateStationId(stationId);

        WaitlistEntry next = strategy.getNext(queue, stationId);
        if (next == null) {
            throw new EmptyQueueException(stationId);
        }

        queue.remove(next);

        LocalDateTime now = LocalDateTime.now();
        long waitMinutes = java.time.Duration.between(next.getJoinTime(), now).toMinutes();

        System.out.println("[PROMOTE] " + next.getResident().getName() + " has been assigned to Station " + stationId + " (waited " + waitMinutes + " min)");

        saveQueueToFile();
    }

    public void printCurrentQueue(String stationId) {
        System.out.println("\n--- Current Queue: Station " + stationId + " | Strategy: " + strategy.getStrategyName() + " ---");
        ArrayList<WaitlistEntry> ordered = getOrderedQueue(stationId);
        if (ordered.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        int pos = 1;
        for (WaitlistEntry e : ordered) {
            System.out.println(pos++ + ". " + e.getResident());
        }
    }

    public ArrayList<String> getQueuedStationIds(Resident resident) {
        ArrayList<String> stationIds = new ArrayList<>();
        for (WaitlistEntry e : queue) {
            if (e.getResident().getResidentId().equals(resident.getResidentId()) && !stationIds.contains(e.getStationId())) {
                stationIds.add(e.getStationId());
            }
        }
        return stationIds;
    }

    public ArrayList<WaitlistEntry> getOrderedQueue(String stationId) {
        ArrayList<WaitlistEntry> remaining = new ArrayList<>();
        for (WaitlistEntry e : queue) {
            if (e.getStationId().equals(stationId)) remaining.add(e);
        }
        ArrayList<WaitlistEntry> ordered = new ArrayList<>();
        while (!remaining.isEmpty()) {
            WaitlistEntry next = strategy.getNext(remaining, stationId);
            if (next == null) break;
            ordered.add(next);
            remaining.remove(next);
        }
        return ordered;
    }

    public void saveQueueToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUEUE_FILE))) {
            for (WaitlistEntry e : queue) {
                TimeSlot slot = e.getRequestedSlot();
                String dateStr = (slot != null) ? slot.getDate().toString() : "";
                String startStr = (slot != null) ? slot.getStartTime().toString() : "";
                String endStr = (slot != null) ? slot.getEndTime().toString() : "";
                writer.write(e.getResident().getResidentId() + ","
                        + e.getResident().getName() + ","
                        + e.getStationId() + ","
                        + e.getJoinTime() + ","
                        + dateStr + ","
                        + startStr + ","
                        + endStr);
                writer.newLine();
            }
        } catch (IOException ex) {
            System.err.println("There is a file error!");
        }
    }

    public void loadQueueFromFile() {
        File file = new File(QUEUE_FILE);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4 || parts.length == 7) {
                    try {
                        String residentId = parts[0];
                        String name = parts[1];
                        String stationId = parts[2];
                        LocalDateTime joinTime = LocalDateTime.parse(parts[3]);
                        Resident resident = new Resident(residentId, name, "", "", "");

                        TimeSlot requestedSlot = null;
                        if (parts.length == 7 && !parts[4].isEmpty() && !parts[5].isEmpty() && !parts[6].isEmpty()) {
                            requestedSlot = new TimeSlot(java.time.LocalDate.parse(parts[4]), java.time.LocalTime.parse(parts[5]), java.time.LocalTime.parse(parts[6]));
                        }

                        WaitlistEntry entry = new WaitlistEntry(resident, stationId, joinTime, requestedSlot);
                        queue.add(entry);
                    } catch (Exception ex) {
                        System.err.println("There is a file error!");
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("There is a file error!");
        }
    }
}