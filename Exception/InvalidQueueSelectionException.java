package Exception;

public class InvalidQueueSelectionException extends Exception {
    public InvalidQueueSelectionException(String detail) {
        super("Invalid queue selection: " + detail);
    }
}
