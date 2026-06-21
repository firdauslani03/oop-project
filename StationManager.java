// [Module: Charging Station Management — Firdaus, Member 2]
import Exception.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of charging stations: creation, removal, status
 * updates, maintenance records, usage statistics, and persistence to file.
 */
public class StationManager {

    private static final String STATION_FILE = "station.txt";
    private static final String MAINTENANCE_FILE = "maintenance.txt";

    private List<ChargingStation> stations;
    private List<MaintenanceRecord> maintenanceRecords;
    private BookingManager bookingManager; // Association: used to look up bookings when completing a charge

    public StationManager() {
        this.stations = new ArrayList<>();
        this.maintenanceRecords = new ArrayList<>();
        loadStationsFromFile();
        loadMaintenanceFromFile();
    }

    /** Allows Main to link this manager with the BookingManager after both are created. */
    public void setBookingManager(BookingManager bookingManager) {
        this.bookingManager = bookingManager;
    }

    // ---------- Station CRUD ----------

    public void addStation(String stationId, String type, String location, double powerOutputKw)
            throws DuplicateStationException {
        if (findStation(stationId) != null) {
            throw new DuplicateStationException("Station ID '" + stationId + "' already exists.");
        }

        ChargingStation station;
        if (type.equalsIgnoreCase("Fast")) {
            station = new FastChargingStation(stationId, location, powerOutputKw);
        } else {
            station = new NormalChargingStation(stationId, location, powerOutputKw);
        }

        stations.add(station);
        System.out.println("Success! Station " + stationId + " has been added.");
        saveStationsToFile();
    }

    public void removeStation(String stationId) throws StationNotFoundException {
        ChargingStation station = findStation(stationId);
        if (station == null) {
            throw new StationNotFoundException("Station '" + stationId + "' not found.");
        }
        stations.remove(station);
        System.out.println("Station " + stationId + " has been removed.");
        saveStationsToFile();
    }

    public ChargingStation findStation(String stationId) {
        for (ChargingStation s : stations) {
            if (s.getStationId().equalsIgnoreCase(stationId)) {
                return s;
            }
        }
        return null;
    }

    public List<ChargingStation> getAllStations() {
        return stations;
    }

    public void viewAllStations() {
        if (stations.isEmpty()) {
            System.out.println("No charging stations have been registered yet.");
            return;
        }
        System.out.println("\n--- ALL CHARGING STATIONS ---");
        for (ChargingStation s : stations) {
            // Polymorphism: each subtype formats displayInfo() differently
            System.out.println(s.displayInfo());
        }
    }

    public void viewStationAvailability() {
        if (stations.isEmpty()) {
            System.out.println("No charging stations have been registered yet.");
            return;
        }
        System.out.println("\n--- STATION AVAILABILITY ---");
        for (ChargingStation s : stations) {
            String availability = s.isAvailable() ? "Active" : "Inactive";
            System.out.println(s.getStationId() + " (" + s.getStationType() + ") - " + availability);
        }
    }

    // ---------- Status Management ----------

    public void updateStationStatus(String stationId, String newStatus)
            throws StationNotFoundException, InvalidStatusUpdateException {
        ChargingStation station = findStation(stationId);
        if (station == null) {
            throw new StationNotFoundException("Station '" + stationId + "' not found.");
        }
        if (!newStatus.equalsIgnoreCase("Active") && !newStatus.equalsIgnoreCase("Inactive")) {
            throw new InvalidStatusUpdateException("Status must be either 'Active' or 'Inactive'.");
        }
        station.setUnderMaintenance(false);
        station.setStatus(capitalize(newStatus));
        System.out.println("Station " + stationId + " status updated to " + capitalize(newStatus) + ".");
        saveStationsToFile();
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // ---------- Maintenance Management ----------

    public void markUnderMaintenance(String stationId, String reason) throws StationNotFoundException {
        ChargingStation station = findStation(stationId);
        if (station == null) {
            throw new StationNotFoundException("Station '" + stationId + "' not found.");
        }
        station.setUnderMaintenance(true);
        MaintenanceRecord record = new MaintenanceRecord(stationId, LocalDate.now(), reason, "Ongoing");
        maintenanceRecords.add(record);
        System.out.println("Station " + stationId + " marked as under maintenance.");
        saveStationsToFile();
        saveMaintenanceToFile();
    }

    public void resolveMaintenance(String stationId) throws StationNotFoundException {
        ChargingStation station = findStation(stationId);
        if (station == null) {
            throw new StationNotFoundException("Station '" + stationId + "' not found.");
        }
        station.setUnderMaintenance(false);
        station.setStatus("Active");
        for (MaintenanceRecord r : maintenanceRecords) {
            if (r.getStationId().equals(stationId) && r.getStatus().equals("Ongoing")) {
                r.setStatus("Resolved");
            }
        }
        System.out.println("Station " + stationId + " maintenance marked as resolved.");
        saveStationsToFile();
        saveMaintenanceToFile();
    }

    public void viewMaintenanceRecords() {
        if (maintenanceRecords.isEmpty()) {
            System.out.println("No maintenance records found.");
            return;
        }
        System.out.println("\n--- MAINTENANCE RECORDS ---");
        for (MaintenanceRecord r : maintenanceRecords) {
            System.out.println(r);
        }
    }

    // ---------- Usage Statistics ----------

    public void viewUsageStatistics() {
        if (stations.isEmpty()) {
            System.out.println("No charging stations have been registered yet.");
            return;
        }

        ChargingStation mostUsed = stations.get(0);
        ChargingStation leastUsed = stations.get(0);

        for (ChargingStation s : stations) {
            if (s.getTotalBookings() > mostUsed.getTotalBookings()) {
                mostUsed = s;
            }
            if (s.getTotalBookings() < leastUsed.getTotalBookings()) {
                leastUsed = s;
            }
        }

        System.out.println("\n--- USAGE STATISTICS ---");
        System.out.println("Most Used Station  : " + mostUsed.getStationId()
                + " (" + mostUsed.getTotalBookings() + " bookings)");
        System.out.println("Least Used Station : " + leastUsed.getStationId()
                + " (" + leastUsed.getTotalBookings() + " bookings)");
        System.out.println("\nTotal Bookings Per Station:");
        for (ChargingStation s : stations) {
            System.out.println("  " + s.getStationId() + " : " + s.getTotalBookings());
        }
    }

    // ---------- Manually Complete Charging (demo purposes) ----------

    /**
     * Marks a booking as completed and frees up the associated station so it
     * becomes available again. Intended for demo purposes since there is no
     * real-time hardware feedback on when a charge actually finishes.
     */
    public void completeCharging(String bookingId) throws StationNotFoundException {
        if (bookingManager == null) {
            System.out.println("Booking system is not linked. Cannot complete charging.");
            return;
        }

        Booking booking = bookingManager.findBooking(bookingId);
        if (booking == null) {
            System.out.println("Booking '" + bookingId + "' not found.");
            return;
        }
        if (!booking.getStatus().equals("Active")) {
            System.out.println("Booking '" + bookingId + "' is not currently active.");
            return;
        }

        ChargingStation station = findStation(booking.getStation().getStationId());
        if (station == null) {
            throw new StationNotFoundException("Station for booking '" + bookingId + "' not found.");
        }

        booking.setStatus("Completed");
        bookingManager.saveBookingsToFile();
        System.out.println("Charging for booking " + bookingId + " marked as completed. Station "
                + station.getStationId() + " is now free.");
    }

    // ---------- File I/O ----------

    public void saveStationsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATION_FILE))) {
            for (ChargingStation s : stations) {
                double power = 0;
                if (s instanceof FastChargingStation) {
                    power = ((FastChargingStation) s).getPowerOutputKw();
                } else if (s instanceof NormalChargingStation) {
                    power = ((NormalChargingStation) s).getPowerOutputKw();
                }
                writer.println(s.getStationId() + "," + s.getStationType() + "," + s.getLocation()
                        + "," + power + "," + s.getStatus() + "," + s.isUnderMaintenance()
                        + "," + s.getTotalBookings());
            }
        } catch (IOException e) {
            System.err.println("File Error: Writing failed to " + STATION_FILE);
        }
    }

    public void loadStationsFromFile() {
        File file = new File(STATION_FILE);
        if (!file.exists()) {
            return;
        }

        try (java.util.Scanner scan = new java.util.Scanner(file)) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] data = line.split(",");

                if (data.length == 7) {
                    try {
                        String stationId = data[0];
                        String type = data[1];
                        String location = data[2];
                        double power = Double.parseDouble(data[3]);
                        String status = data[4];
                        boolean underMaintenance = Boolean.parseBoolean(data[5]);
                        int totalBookings = Integer.parseInt(data[6]);

                        ChargingStation station;
                        if (type.equalsIgnoreCase("Fast")) {
                            station = new FastChargingStation(stationId, location, power);
                        } else {
                            station = new NormalChargingStation(stationId, location, power);
                        }
                        station.setStatus(status);
                        station.setUnderMaintenance(underMaintenance);
                        for (int i = 0; i < totalBookings; i++) {
                            station.addBookingRecord("LOADED");
                        }

                        stations.add(station);
                    } catch (Exception e) {
                        System.err.println("Skipping corrupted line: " + line);
                    }
                }
            }
            System.out.println("[System] Loaded " + stations.size() + " stations from " + STATION_FILE);
        } catch (FileNotFoundException e) {
            System.err.println("File Error: Could not find " + STATION_FILE);
        }
    }

    public void saveMaintenanceToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MAINTENANCE_FILE))) {
            for (MaintenanceRecord r : maintenanceRecords) {
                writer.println(r.getStationId() + "," + r.getDate() + "," + r.getReason() + "," + r.getStatus());
            }
        } catch (IOException e) {
            System.err.println("File Error: Writing failed to " + MAINTENANCE_FILE);
        }
    }

    public void loadMaintenanceFromFile() {
        File file = new File(MAINTENANCE_FILE);
        if (!file.exists()) {
            return;
        }

        try (java.util.Scanner scan = new java.util.Scanner(file)) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] data = line.split(",");

                if (data.length == 4) {
                    try {
                        String stationId = data[0];
                        LocalDate date = LocalDate.parse(data[1]);
                        String reason = data[2];
                        String status = data[3];
                        maintenanceRecords.add(new MaintenanceRecord(stationId, date, reason, status));
                    } catch (Exception e) {
                        System.err.println("Skipping corrupted line: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File Error: Could not find " + MAINTENANCE_FILE);
        }
    }

    /**
     * Seeds the system with 5 fast and 5 normal stations if none exist yet,
     * matching the module spec (5 Fast + 5 Normal).
     */
    public void seedDefaultStationsIfEmpty() {
        if (!stations.isEmpty()) {
            return;
        }
        try {
            for (int i = 1; i <= 5; i++) {
                addStation(String.format("FAST-%02d", i), "Fast", "Block " + i, 50.0);
            }
            for (int i = 1; i <= 5; i++) {
                addStation(String.format("NORMAL-%02d", i), "Normal", "Block " + i, 7.4);
            }
        } catch (DuplicateStationException e) {
            // Should not happen on first seed; ignore safely.
        }
    }
}
