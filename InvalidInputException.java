/** TOPIC: Exception handling - user-defined CHECKED exception (extends Exception). */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
