/**
 * TOPIC: Strings (trim, validation, replace), method overloading, throws.
 * Static helper methods that validate raw console input.
 */
public final class Validator {
    private Validator() { }

    /** Trims text and rejects empty values. '|' is replaced because it is our file separator. */
    public static String requireText(String field, String value) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(field + " cannot be empty.");
        }
        return value.trim().replace(Constants.SEPARATOR, '/');
    }

    /** Phone must be exactly 10 digits. Character-by-character check of a String. */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.length() != 10) {
            return false;
        }
        for (char c : phone.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    // ---- Method overloading: same name, different parameter types ----
    public static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean inRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /** Parses an int from typed text; converts the unchecked NumberFormatException to our checked one. */
    public static int parseInt(String text, int min, int max) throws InvalidInputException {
        int value;
        try {
            value = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("'" + text.trim() + "' is not a whole number.");
        }
        if (!inRange(value, min, max)) {
            throw new InvalidInputException("Value must be between " + min + " and " + max + ".");
        }
        return value;
    }
}
