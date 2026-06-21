// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
package Exception;

public class ResidentAlreadyInQueueException extends Exception {
    public ResidentAlreadyInQueueException(String residentName, String stationId) {
        super(residentName + " is already in the queue for Station " + stationId);
    }
}
