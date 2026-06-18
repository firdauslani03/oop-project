package Exception;

public class EmptyQueueException extends Exception {
    public EmptyQueueException(String stationId) {
        super("The queue for Station " + stationId + " is empty — no one to promote.");
    }
}
