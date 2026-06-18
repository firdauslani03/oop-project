public class Vehicle {
    private String model;
    private double batteryCapacity; // Stored in kWh

    public Vehicle(String model, double batteryCapacity) {
        this.model = model;
        this.batteryCapacity = batteryCapacity;
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
        return "Model: " + model + " | Battery Capacity: " + batteryCapacity + " kWh";
    }
}