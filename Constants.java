/**
 * TOPIC: Constants, primitive data types, identifiers, literals.
 * All fixed values live here (final + static) so no "magic numbers" appear in the code.
 */
public final class Constants {
    private Constants() { }                       // utility class: no objects needed

    public static final int DAYS = 6;             // Mon..Sat
    public static final int SLOTS = 8;            // 8 one-hour slots per day
    public static final int START_HOUR = 9;       // first slot starts at 09:00
    public static final String[] DAY_NAMES = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    // Bit flags for appointment status (bitwise operators)
    public static final int FLAG_CONFIRMED = 1;   // 0001
    public static final int FLAG_PAID      = 2;   // 0010
    public static final int FLAG_FOLLOW_UP = 4;   // 0100

    public static final String DATA_DIR = "data";
    public static final String SEPARATOR_REGEX = "\\|";
    public static final char SEPARATOR = '|';
}
