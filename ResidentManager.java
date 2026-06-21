// [Module: Resident & Vehicle Management — Iman, Member 1]
import Exception.*;
import java.io.*;
import java.util.*;

public class ResidentManager {
    private Map<String, Resident> residentMap = new HashMap<>();
    private static final String RESIDENT_FILE = "resident.txt";
    private static final String VEHICLE_FILE = "vehicle.txt";

    public ResidentManager() {
        loadDataFromFile();
    }

    // Feature 1: Register Account with Exception Handling
    public void registerResident(String residentId, String name, String password, String icNumber, String phoneNumber) 
            throws DuplicateAccountException, InvalidIcFormatException {
        
        if (residentMap.containsKey(residentId.toLowerCase())) {
            throw new DuplicateAccountException("Resident ID '" + residentId + "' is already registered!");
        }

        // Validate IC layout (Must be exactly 12 numerical digits)
        if (icNumber == null || !icNumber.matches("\\d{12}")) {
            throw new InvalidIcFormatException("Invalid IC format! Must be exactly 12 digits without hyphens.");
        }

        Resident newResident = new Resident(residentId, name, password, icNumber, phoneNumber);
        residentMap.put(residentId.toLowerCase(), newResident);
        saveDataToFile();
        System.out.println("Success: Resident account created successfully.");
    }

    // Feature 2: Login System
    public Resident login(String residentId, String password) throws InvalidLoginException {
        Resident resident = residentMap.get(residentId.toLowerCase());
        if (resident == null || !resident.authenticate(password)) {
            throw new InvalidLoginException("Error: Invalid Resident ID or password.");
        }
        return resident;
    }

    // Feature 4 & 5: Update Profiles
    public void updateProfile(Resident resident, String newPhone, String newPassword) {
        if (newPhone != null && !newPhone.trim().isEmpty()) resident.setPhoneNumber(newPhone);
        if (newPassword != null && !newPassword.trim().isEmpty()) resident.setPassword(newPassword);
        saveDataToFile();
    }

    // Secure password change requiring current password
    public boolean changePassword(Resident resident, String currentPassword, String newPassword) {
        if (resident == null) return false;
        if (currentPassword == null || newPassword == null) return false;
        if (!resident.authenticate(currentPassword)) return false;
        resident.setPassword(newPassword);
        saveDataToFile();
        return true;
    }

    // Feature 6 & 7: Vehicle & EV Profile Management
    public void addVehicleToResident(Resident resident, String model, double capacity) {
        Vehicle vehicle = new Vehicle(model, capacity);
        resident.addVehicle(vehicle);
        saveDataToFile();
    }

    public boolean removeVehicleFromResident(Resident resident, String model) {
        for (Vehicle v : resident.getVehicles()) {
            if (v.getModel().equalsIgnoreCase(model)) {
                resident.removeVehicle(v);
                saveDataToFile();
                return true;
            }
        }
        return false;
    }

    // --- File I/O Implementations ---
    private void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESIDENT_FILE))) {
            for (Resident r : residentMap.values()) {
                writer.println(r.getResidentId() + "," + r.getName() + "," + r.getPassword() + "," + r.getIcNumber() + "," + r.getPhoneNumber());
            }
        } catch (IOException e) {
            System.err.println("File Error: Writing failed to " + RESIDENT_FILE);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(VEHICLE_FILE))) {
            for (Resident r : residentMap.values()) {
                for (Vehicle v : r.getVehicles()) {
                    writer.println(r.getResidentId() + "," + v.getModel() + "," + v.getBatteryCapacity());
                }
            }
        } catch (IOException e) {
            System.err.println("File Error: Writing failed to " + VEHICLE_FILE);
        }
    }

    private void loadDataFromFile() {
        File resFile = new File(RESIDENT_FILE);
        File vehFile = new File(VEHICLE_FILE);

        if (resFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(resFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 5) {
                        Resident r = new Resident(data[0], data[1], data[2], data[3], data[4]);
                        residentMap.put(data[0].toLowerCase(), r);
                    }
                }
            } catch (IOException ignored) {}
        }

        if (vehFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(vehFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 3) {
                        Resident owner = residentMap.get(data[0].toLowerCase());
                        if (owner != null) {
                            owner.addVehicle(new Vehicle(data[1], Double.parseDouble(data[2])));
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
    }
    
    //Allows other managers (like BookingManager) to retrieve the full Resident object using only their ID. 
    public Resident getResidentById(String residentId) {
        if (residentId == null) return null;
        return residentMap.get(residentId.toLowerCase());
    }
}