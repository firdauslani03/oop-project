// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
package Exception;

public class EmptyQueueException extends Exception {
    public EmptyQueueException(String stationId) {
        super("The queue for Station " + stationId + " is empty — no one to promote.");
    }
}
