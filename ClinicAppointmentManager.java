import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * TOPIC: Java program structure, Scanner console I/O, formatted printing,
 * do-while menu loop, switch, if-else, try-catch-finally, polymorphism via Reportable.
 */
public class ClinicAppointmentManager {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {                 // try-with-resources closes Scanner
            FileStore store = new FileStore(Constants.DATA_DIR);
            ClinicService service = new ClinicService(store);
            System.out.println("Clinic Appointment Manager");
            System.out.println(service.load());

            int choice;
            do {                                                     // runs at least once
                printMenu();
                choice = readChoice(sc);
                try {
                    handle(choice, sc, service);
                } catch (InvalidInputException | SlotConflictException e) {
                    System.out.println("! " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("! File problem: " + e.getMessage());
                }
            } while (choice != 0);

            try {
                service.save();
                System.out.println("Data saved.");
            } catch (IOException e) {
                System.out.println("! Could not save: " + e.getMessage());
            } finally {
                System.out.println("Goodbye.");                      // always runs
            }
        } catch (IOException e) {
            System.out.println("! Cannot start - data folder problem: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println("\n1 Register patient   2 Book appointment   3 Cancel appointment");
        System.out.println("4 Update status      5 Schedule (by time)  6 Schedule (by doctor)");
        System.out.println("7 Search patient     8 Next free slot      9 Report");
        System.out.println("10 Audit log         0 Save & exit");
        System.out.print("Choice: ");
    }

    private static int readChoice(Scanner sc) {
        try {
            return Validator.parseInt(sc.nextLine(), 0, 10);
        } catch (InvalidInputException e) {
            System.out.println("! " + e.getMessage());
            return -1;                                               // -1 = just show menu again
        }
    }

    private static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private static int askInt(Scanner sc, String prompt, int min, int max) throws InvalidInputException {
        return Validator.parseInt(ask(sc, prompt), min, max);
    }

    private static void handle(int choice, Scanner sc, ClinicService service)
            throws InvalidInputException, SlotConflictException, IOException {
        switch (choice) {
            case 1: {
                String name = ask(sc, "Name: ");
                String phone = ask(sc, "Phone (10 digits): ");
                int age = askInt(sc, "Age: ", 0, 120);
                Patient p = service.addPatient(name, phone, age);
                System.out.println("Registered -> " + p);
                break;
            }
            case 2: {
                printAll("Doctors", service.getDoctors());
                int patientId = askInt(sc, "Patient id: ", 1, Integer.MAX_VALUE);
                int doctorId = askInt(sc, "Doctor id: ", 1, Integer.MAX_VALUE);
                int day = askInt(sc, "Day (1=Mon .. 6=Sat): ", 1, Constants.DAYS) - 1;
                showFreeSlots(service, doctorId, day);
                int slot = askInt(sc, "Slot number (1.." + Constants.SLOTS + "): ", 1, Constants.SLOTS) - 1;
                String reason = ask(sc, "Reason: ");
                Appointment a = service.book(patientId, doctorId, day, slot, reason);
                System.out.println("Booked -> " + service.describe(a));
                break;
            }
            case 3: {
                service.cancel(askInt(sc, "Appointment id: ", 1, Integer.MAX_VALUE));
                System.out.println("Cancelled.");
                break;
            }
            case 4: {
                int id = askInt(sc, "Appointment id: ", 1, Integer.MAX_VALUE);
                int what = askInt(sc, "1 Confirm  2 Mark paid  3 Follow-up needed: ", 1, 3);
                int flag;
                if (what == 1) {
                    flag = Constants.FLAG_CONFIRMED;
                } else if (what == 2) {
                    flag = Constants.FLAG_PAID;
                } else {
                    flag = Constants.FLAG_FOLLOW_UP;
                }
                service.setFlag(id, flag);
                System.out.println("Updated.");
                break;
            }
            case 5:
                printSchedule("Schedule by time", service.scheduleByTime(), service);
                break;
            case 6:
                printSchedule("Schedule by doctor", service.scheduleByDoctor(), service);
                break;
            case 7: {
                List<Patient> found = service.searchPatients(ask(sc, "Name contains: "));
                if (found.isEmpty()) {
                    System.out.println("No match.");
                } else {
                    printAll("Matches", found);
                }
                break;
            }
            case 8: {
                int doctorId = askInt(sc, "Doctor id: ", 1, Integer.MAX_VALUE);
                int day = askInt(sc, "Day (1=Mon .. 6=Sat): ", 1, Constants.DAYS) - 1;
                int slot = service.suggestSlot(doctorId, day);
                System.out.println(slot < 0 ? "Fully booked that day."
                        : "First free slot: " + ScheduleGrid.timeLabel(slot) + " (slot " + (slot + 1) + ")");
                break;
            }
            case 9:
                System.out.println(service.report());
                break;
            case 10:
                System.out.print(service.auditLog());
                break;
            case 0:
            case -1:
                break;                                               // exit / re-show menu
            default:
                System.out.println("Unknown option.");
        }
    }

    /** Programming to an interface: works for patients, doctors, appointments alike. */
    private static void printAll(String title, List<? extends Reportable> items) {
        System.out.println("--- " + title + " ---");
        for (Reportable r : items) {
            System.out.println(r.summaryLine());
        }
    }

    private static void printSchedule(String title, List<Appointment> list, ClinicService service) {
        System.out.println("--- " + title + " ---");
        if (list.isEmpty()) {
            System.out.println("(no appointments)");
            return;
        }
        System.out.printf("%-4s %-11s %-16s %-12s %-14s %s%n",
                "ID", "When", "Patient", "Doctor", "Reason", "Status");
        for (Appointment a : list) {
            System.out.println(service.describe(a));
        }
    }

    private static void showFreeSlots(ClinicService service, int doctorId, int day) {
        Doctor d = service.findDoctor(doctorId);
        if (d == null) {
            return;
        }
        System.out.print("Free slots: ");
        for (int s = 0; s < Constants.SLOTS; s++) {
            if (d.isFree(day, s)) {
                System.out.print((s + 1) + "=" + ScheduleGrid.timeLabel(s) + "  ");
            }
        }
        System.out.println();
    }
}
