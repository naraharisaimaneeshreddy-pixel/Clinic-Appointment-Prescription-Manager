/** TOPIC: Exception handling - second checked exception, thrown when a doctor slot is taken. */
public class SlotConflictException extends Exception {
    public SlotConflictException(String message) {
        super(message);
    }
}
