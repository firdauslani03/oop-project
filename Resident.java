import java.util.ArrayList;
import java.util.List;

public class Resident extends User {
    private String residentId;
    private String name;

    private String icNumber;
    private String phoneNumber;
    private List<Vehicle> vehicles; // Aggregation Requirement

    public Resident(String residentId, String name, String password, String icNumber, String phoneNumber) {
        super(residentId, password, "Resident"); // Calls the User base class with username and role
        this.residentId = residentId;
        this.name = name;
        this.icNumber = icNumber;
        this.phoneNumber = phoneNumber;
        this.vehicles = new ArrayList<>();
    }

    // Convenience constructor for existing two-argument usage
    public Resident(String residentId, String name) {
        this(residentId, name, "defaultPassword", "", "");
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

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
    }
}