/**
 * TOPIC: Classes/objects, constructors (parameterized + overloading), this(), static members,
 * inheritance (is-a Person), String split/join for file records.
 */
public class Patient extends Person {
    private static int counter = 0;          // static: shared by ALL patients, used for auto ids
    private final int age;

    /** Used when loading from file (id already known). */
    public Patient(int id, String name, String phone, int age) {
        super(id, name, phone);
        this.age = age;
        if (id > counter) {
            counter = id;                     // keep counter ahead of loaded ids
        }
    }

    /** Overloaded constructor: new patient, id generated automatically. */
    public Patient(String name, String phone, int age) {
        this(++counter, name, phone, age);    // this(...) chains to the constructor above
    }

    public int getAge() { return age; }

    @Override
    public String role() { return "Patient"; }

    @Override
    public String toString() {
        return super.toString() + " age " + age;
    }

    public String toRecord() {
        return String.join("|", String.valueOf(getId()), getName(), String.valueOf(age), getPhone());
    }

    public static Patient fromRecord(String line) throws InvalidInputException {
        String[] parts = line.split(Constants.SEPARATOR_REGEX);
        if (parts.length != 4) {
            throw new InvalidInputException("Bad patient record: " + line);
        }
        try {
            return new Patient(Integer.parseInt(parts[0].trim()), parts[1].trim(),
                               parts[3].trim(), Integer.parseInt(parts[2].trim()));
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Bad number in patient record: " + line);
        }
    }
}
