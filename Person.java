/**
 * TOPIC: Inheritance (base class), abstract method, encapsulation, toString(), interface implementation.
 */
public abstract class Person implements Reportable {
    private final int id;          // private fields = information hiding
    private String name;
    private String phone;

    protected Person(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }

    /** Every subclass must say what role it plays. */
    public abstract String role();

    @Override
    public String summaryLine() {
        return toString();
    }

    @Override
    public String toString() {
        return String.format("%-8s #%-3d %-18s %s", role(), id, name, phone);
    }
}
