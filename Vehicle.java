// [Module: Resident & Vehicle Management — Iman, Member 1]

public class Vehicle {
    private String plateNumber;
    private String model;
    private double batteryCapacity;

    public Vehicle(String plateNumber, String model, double batteryCapacity) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.batteryCapacity = batteryCapacity;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    @Override
    public String toString() {
        return "Plate: " + plateNumber + " | Model: " + model + " | Battery Capacity: " + batteryCapacity + " kWh";
    }
}