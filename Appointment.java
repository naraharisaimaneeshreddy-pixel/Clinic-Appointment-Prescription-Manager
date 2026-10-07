/**
 * TOPIC: Class as blueprint, constructor overloading, this, static counter, bitwise operators,
 * Comparable (natural ordering), interface implementation, toString().
 */
public class Appointment implements Comparable<Appointment>, Reportable {
    private static int counter = 0;

    private final int id;
    private final int patientId;
    private final int doctorId;
    private final int day;        // 0..5
    private final int slot;       // 0..7
    private int flags;            // several yes/no states packed into one int
    private String reason;

    public Appointment(int id, int patientId, int doctorId, int day, int slot, int flags, String reason) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.day = day;
        this.slot = slot;
        this.flags = flags;
        this.reason = reason;
        if (id > counter) {
            counter = id;
        }
    }

    /** New booking: auto id, no flags set yet. */
    public Appointment(int patientId, int doctorId, int day, int slot, String reason) {
        this(++counter, patientId, doctorId, day, slot, 0, reason);
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public int getDay() { return day; }
    public int getSlot() { return slot; }
    public String getReason() { return reason; }

    // ---- Bitwise operators: |= sets a bit, &= ~ clears it, & tests it ----
    public void setFlag(int flag)   { flags |= flag; }
    public void clearFlag(int flag) { flags &= ~flag; }
    public boolean hasFlag(int flag) { return (flags & flag) != 0; }

    public String timeText() {
        return Constants.DAY_NAMES[day] + " " + ScheduleGrid.timeLabel(slot);
    }

    public String statusText() {
        String s = hasFlag(Constants.FLAG_CONFIRMED) ? "Confirmed" : "Pending";
        s += hasFlag(Constants.FLAG_PAID) ? ", Paid" : ", Unpaid";
        if (hasFlag(Constants.FLAG_FOLLOW_UP)) {
            s += ", Follow-up";
        }
        return s;
    }

    /** Natural order: by day, then by slot. Used by Collections.sort(list). */
    @Override
    public int compareTo(Appointment other) {
        if (this.day != other.day) {
            return Integer.compare(this.day, other.day);
        }
        return Integer.compare(this.slot, other.slot);
    }

    @Override
    public String summaryLine() {
        return toString();
    }

    @Override
    public String toString() {
        return "Appt#" + id + " [" + timeText() + "] patient=" + patientId
                + " doctor=" + doctorId + " (" + statusText() + ")";
    }

    // ---- File record: join / split ----
    public String toRecord() {
        return String.join("|", String.valueOf(id), String.valueOf(patientId), String.valueOf(doctorId),
                String.valueOf(day), String.valueOf(slot), String.valueOf(flags), reason);
    }

    public static Appointment fromRecord(String line) throws InvalidInputException {
        String[] p = line.split(Constants.SEPARATOR_REGEX);
        try {
            int day = Integer.parseInt(p[3].trim());
            int slot = Integer.parseInt(p[4].trim());
            if (!Validator.inRange(day, 0, Constants.DAYS - 1)
                    || !Validator.inRange(slot, 0, Constants.SLOTS - 1)) {
                throw new InvalidInputException("Day/slot out of range: " + line);
            }
            return new Appointment(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()),
                    Integer.parseInt(p[2].trim()), day, slot, Integer.parseInt(p[5].trim()), p[6].trim());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {   // multi-catch
            throw new InvalidInputException("Malformed appointment record: " + line);
        }
    }
}
