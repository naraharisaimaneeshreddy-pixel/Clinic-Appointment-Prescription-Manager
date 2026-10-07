import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TOPIC: Business logic - collections, sorting, binary search, counting/summation/average,
 * StringBuilder report, exception propagation (throws), persistence calls.
 */
public class ClinicService {
    private final List<Patient> patients = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();
    private final FileStore store;

    public ClinicService(FileStore store) {
        this.store = store;
        doctors.add(new Doctor(1, "Dr. Rao", "9000000001", "General Medicine", 300));
        doctors.add(new Doctor(2, "Dr. Meena", "9000000002", "Pediatrics", 500));
        doctors.add(new Doctor(3, "Dr. Khan", "9000000003", "Dermatology", 600));
    }

    public List<Doctor> getDoctors() { return Collections.unmodifiableList(doctors); }

    // ---------- lookups (return null when not found: null reference concept) ----------
    public Patient findPatient(int id) {
        for (Patient p : patients) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Doctor findDoctor(int id) {
        for (Doctor d : doctors) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    /** Sort by id, copy ids into an int[] and use Arrays.binarySearch (needs sorted data). */
    public Appointment findAppointment(int id) {
        Collections.sort(appointments, new AppointmentComparators.ById());
        int[] ids = new int[appointments.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = appointments.get(i).getId();
        }
        int index = Arrays.binarySearch(ids, id);
        return index >= 0 ? appointments.get(index) : null;
    }

    // ---------- patients ----------
    public Patient addPatient(String name, String phone, int age) throws InvalidInputException {
        String cleanName = Validator.requireText("Name", name);
        if (!Validator.isValidPhone(phone)) {
            throw new InvalidInputException("Phone must be exactly 10 digits.");
        }
        Patient p = new Patient(cleanName, phone, age);
        patients.add(p);
        return p;
    }

    /** Case-insensitive partial name search. */
    public List<Patient> searchPatients(String query) {
        List<Patient> found = new ArrayList<>();
        String q = query.trim().toLowerCase();
        for (Patient p : patients) {
            if (p.getName().toLowerCase().contains(q)) {
                found.add(p);
            }
        }
        return found;
    }

    // ---------- appointments ----------
    public Appointment book(int patientId, int doctorId, int day, int slot, String reason)
            throws InvalidInputException, SlotConflictException, IOException {
        Patient p = findPatient(patientId);
        if (p == null) {
            throw new InvalidInputException("No patient with id " + patientId);
        }
        Doctor d = findDoctor(doctorId);
        if (d == null) {
            throw new InvalidInputException("No doctor with id " + doctorId);
        }
        String cleanReason = Validator.requireText("Reason", reason);
        d.book(day, slot);                                   // may throw SlotConflictException
        Appointment a = new Appointment(patientId, doctorId, day, slot, cleanReason);
        appointments.add(a);
        store.appendLine("audit.log", "BOOKED    " + a.toRecord());
        return a;
    }

    public void cancel(int appointmentId) throws InvalidInputException, IOException {
        Appointment a = findAppointment(appointmentId);
        if (a == null) {
            throw new InvalidInputException("No appointment with id " + appointmentId);
        }
        findDoctor(a.getDoctorId()).release(a.getDay(), a.getSlot());
        appointments.remove(a);
        store.appendLine("audit.log", "CANCELLED " + a.toRecord());
    }

    public void setFlag(int appointmentId, int flag) throws InvalidInputException, IOException {
        Appointment a = findAppointment(appointmentId);
        if (a == null) {
            throw new InvalidInputException("No appointment with id " + appointmentId);
        }
        a.setFlag(flag);
        store.appendLine("audit.log", "UPDATED   " + a.toRecord());
    }

    /** Recursion wrapper: first free slot of this doctor on a day, or -1. */
    public int suggestSlot(int doctorId, int day) throws InvalidInputException {
        Doctor d = findDoctor(doctorId);
        if (d == null) {
            throw new InvalidInputException("No doctor with id " + doctorId);
        }
        return ScheduleGrid.nextFreeSlot(d.getGridCopy(), day, 0);
    }

    // ---------- sorted views ----------
    public List<Appointment> scheduleByTime() {
        List<Appointment> copy = new ArrayList<>(appointments);
        Collections.sort(copy);                                        // Comparable
        return copy;
    }

    public List<Appointment> scheduleByDoctor() {
        List<Appointment> copy = new ArrayList<>(appointments);
        Collections.sort(copy, new AppointmentComparators.ByDoctorThenTime());   // Comparator
        return copy;
    }

    /** Readable line with names instead of ids. */
    public String describe(Appointment a) {
        Patient p = findPatient(a.getPatientId());
        Doctor d = findDoctor(a.getDoctorId());
        return String.format("#%-3d %-11s %-16s %-12s %-14s %s",
                a.getId(), a.timeText(),
                p != null ? p.getName() : "?",
                d != null ? d.getName() : "?",
                a.getReason(), a.statusText());
    }

    // ---------- report ----------
    public String report() {
        int total = appointments.size();
        int[] perDoctor = new int[doctors.size()];                 // counting technique
        double revenue = 0;                                        // summation
        int paidCount = 0;

        for (Appointment a : appointments) {
            for (int i = 0; i < doctors.size(); i++) {
                if (doctors.get(i).getId() == a.getDoctorId()) {
                    perDoctor[i]++;
                }
            }
            if (a.hasFlag(Constants.FLAG_PAID)) {
                Doctor d = findDoctor(a.getDoctorId());
                if (d != null) {
                    revenue += d.getFee();
                    paidCount++;
                }
            }
        }

        int busiest = 0;
        for (int i = 1; i < perDoctor.length; i++) {
            if (perDoctor[i] > perDoctor[busiest]) {
                busiest = i;
            }
        }

        double avgFee = paidCount > 0 ? revenue / paidCount : 0.0;
        double paidPercent = total > 0 ? (double) paidCount * 100 / total : 0.0;   // typecasting

        int[][] clinicLoad = ScheduleGrid.newGrid();
        for (Doctor d : doctors) {
            clinicLoad = ScheduleGrid.add(clinicLoad, d.getGridCopy());   // matrix addition
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== CLINIC REPORT =====\n");
        sb.append(String.format("Patients registered : %d%n", patients.size()));
        sb.append(String.format("Appointments        : %d%n", total));
        for (int i = 0; i < doctors.size(); i++) {
            sb.append(String.format("  %-10s : %d%n", doctors.get(i).getName(), perDoctor[i]));
        }
        if (total > 0) {
            sb.append("Busiest doctor      : ").append(doctors.get(busiest).getName()).append('\n');
        }
        sb.append(String.format("Paid appointments   : %d (%.1f%%)%n", paidCount, paidPercent));
        sb.append(String.format("Revenue collected   : Rs.%.2f%n", revenue));
        sb.append(String.format("Average paid fee    : Rs.%.2f%n", avgFee));
        sb.append("\nClinic load (all doctors combined, slots booked):\n");
        sb.append(ScheduleGrid.render(clinicLoad));
        return sb.toString();
    }

    // ---------- persistence ----------
    public void save() throws IOException {
        List<String> patientLines = new ArrayList<>();
        for (Patient p : patients) {
            patientLines.add(p.toRecord());
        }
        store.writeLines("patients.txt", patientLines);

        List<String> apptLines = new ArrayList<>();
        for (Appointment a : appointments) {
            apptLines.add(a.toRecord());
        }
        store.writeLines("appointments.txt", apptLines);
    }

    /** Loads files; malformed or conflicting lines are skipped and counted, never crash the program. */
    public String load() throws IOException {
        int loaded = 0;
        int skipped = 0;
        for (String line : store.readLines("patients.txt")) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                patients.add(Patient.fromRecord(line));
                loaded++;
            } catch (InvalidInputException e) {
                skipped++;
            }
        }
        for (String line : store.readLines("appointments.txt")) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                Appointment a = Appointment.fromRecord(line);
                Doctor d = findDoctor(a.getDoctorId());
                if (d == null || findPatient(a.getPatientId()) == null) {
                    throw new InvalidInputException("Unknown doctor/patient in: " + line);
                }
                d.book(a.getDay(), a.getSlot());
                appointments.add(a);
                loaded++;
            } catch (InvalidInputException | SlotConflictException e) {   // multi-catch
                skipped++;
            }
        }
        return "Loaded " + loaded + " record(s), skipped " + skipped + " bad record(s).";
    }

    public String auditLog() throws IOException {
        return store.readAll("audit.log");
    }
}
