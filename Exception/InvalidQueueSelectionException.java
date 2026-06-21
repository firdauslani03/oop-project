// [Module: Smart Queue & Waitlist Management — Teoh Xin Yee, Member 4]
package Exception;

public class InvalidQueueSelectionException extends Exception {
    public InvalidQueueSelectionException(String detail) {
        super("Invalid queue selection: " + detail);
    }
}
