// [Module: Charging Station Management — Firdaus, Member 2]
/**
 * Normal charging station (Inheritance from ChargingStation).
 * Provides a lower charging power rating than a fast station.
 */
public class NormalChargingStation extends ChargingStation {
    private double powerOutputKw;

    public NormalChargingStation(String stationId, String location, double powerOutputKw) {
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
        return "Normal";
    }

    @Override
    public String displayInfo() {
        return "[NORMAL] " + getStationId()
                + " | Location: " + getLocation()
                + " | Power: " + powerOutputKw + " kW"
                + " | Status: " + getStatus()
                + " | Total Bookings: " + getTotalBookings();
    }
}
