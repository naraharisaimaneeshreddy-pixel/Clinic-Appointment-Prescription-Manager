import java.util.Comparator;

/** TOPIC: Comparator interface - alternative orderings besides Comparable. */
public final class AppointmentComparators {
    private AppointmentComparators() { }

    /** Order by id (needed before Arrays.binarySearch). */
    public static class ById implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a, Appointment b) {
            return Integer.compare(a.getId(), b.getId());
        }
    }

    /** Order by doctor, then by time. */
    public static class ByDoctorThenTime implements Comparator<Appointment> {
        @Override
        public int compare(Appointment a, Appointment b) {
            if (a.getDoctorId() != b.getDoctorId()) {
                return Integer.compare(a.getDoctorId(), b.getDoctorId());
            }
            return a.compareTo(b);
        }
    }
}
