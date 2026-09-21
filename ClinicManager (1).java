import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClinicManager {
    static Scanner scanner = new Scanner(System.in);
    static List<Patient> patients = new ArrayList<>();
    static List<Doctor> doctors = new ArrayList<>();
    static List<Appointment> appointments = new ArrayList<>();
    static List<Prescription> prescriptions = new ArrayList<>();

    static int patientIdCounter = 1;
    static int doctorIdCounter = 1;
    static int appointmentIdCounter = 1;
    static int prescriptionIdCounter = 1;

    public static void main(String[] args) {
        addSampleData();
        int choice;
        do {
            System.out.println("\n======================================");
            System.out.println(" CLINIC APPOINTMENT & PRESCRIPTION MANAGER");
            System.out.println("======================================");
            System.out.println("1. Register Patient");
            System.out.println("2. Register Doctor");
            System.out.println("3. Book Appointment");
            System.out.println("4. View All Appointments");
            System.out.println("5. Cancel Appointment");
            System.out.println("6. Create Prescription");
            System.out.println("7. View Patient Prescriptions");
            System.out.println("8. View Patients");
            System.out.println("9. View Doctors");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            choice = readInt();
            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> registerDoctor();
                case 3 -> bookAppointment();
                case 4 -> viewAppointments();
                case 5 -> cancelAppointment();
                case 6 -> createPrescription();
                case 7 -> viewPatientPrescriptions();
                case 8 -> viewPatients();
                case 9 -> viewDoctors();
                case 0 -> System.out.println("Thank you for using the system.");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
        scanner.close();
    }

    static void registerPatient() {
        System.out.println("\n--- Register Patient ---");
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = readInt();
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        Patient patient = new Patient(patientIdCounter++, name, age, gender, phone);
        patients.add(patient);
        System.out.println("Patient registered successfully. Patient ID: " + patient.id);
    }

    static void viewPatients() {
        System.out.println("\n--- Patient List ---");
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        patients.forEach(System.out::println);
    }

    static Patient findPatientById(int id) {
        for (Patient patient : patients) if (patient.id == id) return patient;
        return null;
    }

    static void registerDoctor() {
        System.out.println("\n--- Register Doctor ---");
        System.out.print("Enter doctor name: ");
        String name = scanner.nextLine();
        System.out.print("Enter specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        Doctor doctor = new Doctor(doctorIdCounter++, name, specialization, phone);
        doctors.add(doctor);
        System.out.println("Doctor registered successfully. Doctor ID: " + doctor.id);
    }

    static void viewDoctors() {
        System.out.println("\n--- Doctor List ---");
        if (doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }
        doctors.forEach(System.out::println);
    }

    static Doctor findDoctorById(int id) {
        for (Doctor doctor : doctors) if (doctor.id == id) return doctor;
        return null;
    }

    static void bookAppointment() {
        System.out.println("\n--- Book Appointment ---");
        viewPatients();
        System.out.print("Enter patient ID: ");
        Patient patient = findPatientById(readInt());
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        viewDoctors();
        System.out.print("Enter doctor ID: ");
        Doctor doctor = findDoctorById(readInt());
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine();
        System.out.print("Enter appointment time (HH:MM): ");
        String timeInput = scanner.nextLine();
        try {
            LocalDate date = LocalDate.parse(dateInput);
            LocalTime time = LocalTime.parse(timeInput);
            for (Appointment a : appointments) {
                if (a.doctor.id == doctor.id && a.date.equals(date) &&
                        a.time.equals(time) && a.status.equals("Booked")) {
                    System.out.println("This doctor already has an appointment at that time.");
                    return;
                }
            }
            Appointment appointment = new Appointment(appointmentIdCounter++, patient, doctor,
                    date, time, "Booked");
            appointments.add(appointment);
            System.out.println("Appointment booked successfully. Appointment ID: " + appointment.id);
        } catch (Exception e) {
            System.out.println("Invalid date or time format.");
        }
    }

    static void viewAppointments() {
        System.out.println("\n--- Appointment List ---");
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        appointments.forEach(System.out::println);
    }

    static void cancelAppointment() {
        System.out.println("\n--- Cancel Appointment ---");
        viewAppointments();
        System.out.print("Enter appointment ID: ");
        int id = readInt();
        for (Appointment appointment : appointments) {
            if (appointment.id == id) {
                if (appointment.status.equals("Cancelled")) {
                    System.out.println("Appointment is already cancelled.");
                } else {
                    appointment.status = "Cancelled";
                    System.out.println("Appointment cancelled successfully.");
                }
                return;
            }
        }
        System.out.println("Appointment not found.");
    }

    static void createPrescription() {
        System.out.println("\n--- Create Prescription ---");
        viewPatients();
        System.out.print("Enter patient ID: ");
        Patient patient = findPatientById(readInt());
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        viewDoctors();
        System.out.print("Enter doctor ID: ");
        Doctor doctor = findDoctorById(readInt());
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
        System.out.print("Enter diagnosis: ");
        String diagnosis = scanner.nextLine();
        Prescription prescription = new Prescription(prescriptionIdCounter++, patient, doctor, diagnosis);
        System.out.print("How many medicines do you want to add? ");
        int count = readInt();
        for (int i = 1; i <= count; i++) {
            System.out.println("\nMedicine " + i);
            System.out.print("Medicine name: ");
            String name = scanner.nextLine();
            System.out.print("Dosage: ");
            String dosage = scanner.nextLine();
            System.out.print("Duration: ");
            String duration = scanner.nextLine();
            prescription.medicines.add(new Medicine(name, dosage, duration));
        }
        prescriptions.add(prescription);
        System.out.println("Prescription created successfully. Prescription ID: " + prescription.id);
    }

    static void viewPatientPrescriptions() {
        System.out.println("\n--- Patient Prescriptions ---");
        viewPatients();
        System.out.print("Enter patient ID: ");
        int id = readInt();
        if (findPatientById(id) == null) {
            System.out.println("Patient not found.");
            return;
        }
        boolean found = false;
        for (Prescription prescription : prescriptions) {
            if (prescription.patient.id == id) {
                System.out.println(prescription);
                found = true;
            }
        }
        if (!found) System.out.println("No prescriptions found for this patient.");
    }

    static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    static void addSampleData() {
        patients.add(new Patient(patientIdCounter++, "John Smith", 35, "Male", "9876543210"));
        doctors.add(new Doctor(doctorIdCounter++, "Dr. Sarah Johnson", "General Physician", "9123456780"));
    }
}

class Patient {
    int id; String name; int age; String gender; String phone;
    Patient(int id, String name, int age, String gender, String phone) {
        this.id = id; this.name = name; this.age = age; this.gender = gender; this.phone = phone;
    }
    public String toString() {
        return "Patient ID: " + id + ", Name: " + name + ", Age: " + age +
                ", Gender: " + gender + ", Phone: " + phone;
    }
}

class Doctor {
    int id; String name; String specialization; String phone;
    Doctor(int id, String name, String specialization, String phone) {
        this.id = id; this.name = name; this.specialization = specialization; this.phone = phone;
    }
    public String toString() {
        return "Doctor ID: " + id + ", Name: " + name + ", Specialization: " +
                specialization + ", Phone: " + phone;
    }
}

class Appointment {
    int id; Patient patient; Doctor doctor; LocalDate date; LocalTime time; String status;
    Appointment(int id, Patient patient, Doctor doctor, LocalDate date, LocalTime time, String status) {
        this.id = id; this.patient = patient; this.doctor = doctor;
        this.date = date; this.time = time; this.status = status;
    }
    public String toString() {
        return "Appointment ID: " + id + ", Patient: " + patient.name + ", Doctor: " +
                doctor.name + ", Date: " + date + ", Time: " + time + ", Status: " + status;
    }
}

class Prescription {
    int id; Patient patient; Doctor doctor; String diagnosis;
    List<Medicine> medicines = new ArrayList<>();
    LocalDate issueDate;
    Prescription(int id, Patient patient, Doctor doctor, String diagnosis) {
        this.id = id; this.patient = patient; this.doctor = doctor;
        this.diagnosis = diagnosis; this.issueDate = LocalDate.now();
    }
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\nPrescription ID: ").append(id)
                .append("\nIssue Date: ").append(issueDate)
                .append("\nPatient: ").append(patient.name)
                .append("\nDoctor: ").append(doctor.name)
                .append("\nDiagnosis: ").append(diagnosis)
                .append("\nMedicines:");
        for (Medicine medicine : medicines) result.append("\n  - ").append(medicine);
        return result.append("\n").toString();
    }
}

class Medicine {
    String name; String dosage; String duration;
    Medicine(String name, String dosage, String duration) {
        this.name = name; this.dosage = dosage; this.duration = duration;
    }
    public String toString() {
        return name + " | Dosage: " + dosage + " | Duration: " + duration;
    }
}
