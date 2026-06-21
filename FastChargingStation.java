// [Module: Charging Station Management — Firdaus, Member 2]
/**
 * Fast charging station (Inheritance from ChargingStation).
 * Provides a higher charging power rating than a normal station.
 */
public class FastChargingStation extends ChargingStation {
    private double powerOutputKw;

    public FastChargingStation(String stationId, String location, double powerOutputKw) {
        super(stationId, location);
        this.powerOutputKw = powerOutputKw;
    }

    public double getPowerOutputKw() {
        return powerOutputKw;
    }

    public void setPowerOutputKw(double powerOutputKw) {
        this.powerOutputKw = powerOutputKw;
    }

    @Override
    public String getStationType() {
        return "Fast";
    }

    @Override
    public String displayInfo() {
        return "[FAST] " + getStationId()
                + " | Location: " + getLocation()
                + " | Power: " + powerOutputKw + " kW"
                + " | Status: " + getStatus()
                + " | Total Bookings: " + getTotalBookings();
    }
}
