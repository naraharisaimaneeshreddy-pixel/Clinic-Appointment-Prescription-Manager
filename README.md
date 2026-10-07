# ClinicAppointmentManager

A Java console application that helps a small clinic register patients, book appointments, block double bookings, track appointment status and save everything to files.

Built as a Programming in Java course project using only core Java (no external libraries).

## Features

- Register patients with input validation (name, 10-digit phone, age)
- Book and cancel appointments on a weekly timetable (Mon-Sat, 8 one-hour slots per day)
- Prevents a doctor from being booked twice in the same slot
- Update status: confirmed, paid, follow-up needed
- View the schedule sorted by time or by doctor
- Search patients by name
- Suggest the next free slot for a doctor
- Report: appointments per doctor, busiest doctor, revenue, average fee and clinic load grid
- Saves data to text files and keeps an audit log

## How to run

Requires Java 11 or newer.

```bash
javac *.java
java ClinicAppointmentManager
```

Data is stored in a `data/` folder created on first run (`patients.txt`, `appointments.txt`, `audit.log`).

## Project structure

| File | Purpose |
|---|---|
| `ClinicAppointmentManager.java` | Main class: menu, Scanner input, formatted output |
| `ClinicService.java` | Business logic: booking, cancelling, sorting, searching, report, save/load |
| `Person.java` | Abstract base class (id, name, phone) |
| `Patient.java` | Patient, extends Person |
| `Doctor.java` | Doctor with a private 2D timetable grid, extends Person |
| `Appointment.java` | Appointment with bit-flag status, implements Comparable |
| `AppointmentComparators.java` | Comparators: by id, by doctor then time |
| `Reportable.java` | Interface for one-line summaries |
| `ScheduleGrid.java` | 2D array helpers: matrix addition, totals, recursive free-slot search |
| `Validator.java` | Input validation helpers |
| `FileStore.java` | File reading/writing with `Path`, `Files` and buffered streams |
| `Constants.java` | Fixed values (days, slots, flags) |
| `InvalidInputException.java`, `SlotConflictException.java` | Custom checked exceptions |

## Java concepts used

- **Foundations:** primitive types, constants, operators (including bitwise and ternary), typecasting, `Scanner`, `printf`
- **Control flow:** `do-while` menu loop, `switch`, `if-else`, `for`, enhanced `for`, `while`
- **Methods and arrays:** method overloading, recursion, 1D arrays (counting, sum, average), 2D arrays (matrix addition)
- **OOP:** classes and objects, constructors and `this`, encapsulation, `static`, inheritance, abstract class, interface, `toString()`
- **Exceptions:** checked and unchecked, `try-catch-finally`, multi-catch, `throw`, `throws`
- **Strings and files:** `split`, `join`, `StringBuilder`, `Arrays.sort`, `Collections.sort`, `Arrays.binarySearch`, `Comparable`, `Comparator`, try-with-resources, appending, malformed-record handling

See [`VIVA_NOTES.md`](VIVA_NOTES.md) for a topic-by-topic explanation of why each concept is used.

## Menu

```
1 Register patient   2 Book appointment   3 Cancel appointment
4 Update status      5 Schedule (by time)  6 Schedule (by doctor)
7 Search patient     8 Next free slot      9 Report
10 Audit log         0 Save & exit
```

## Author

Your Name - Your Roll Number - Your College
