public class Resident {
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