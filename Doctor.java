/**
 * TOPIC: Inheritance, encapsulation of a 2D array (private grid), method overriding, throws/throw.
 */
public class Doctor extends Person {
    private final String speciality;
    private final double fee;
    private final int[][] grid = ScheduleGrid.newGrid();   // this doctor's weekly timetable

    public Doctor(int id, String name, String phone, String speciality, double fee) {
        super(id, name, phone);
        this.speciality = speciality;
        this.fee = fee;
    }

    public String getSpeciality() { return speciality; }
    public double getFee() { return fee; }

    @Override
    public String role() { return "Doctor"; }

    public boolean isFree(int day, int slot) {
        return grid[day][slot] == 0;
    }

    public void book(int day, int slot) throws SlotConflictException {
        if (!isFree(day, slot)) {
            throw new SlotConflictException(getName() + " is already booked on "
                    + Constants.DAY_NAMES[day] + " at " + ScheduleGrid.timeLabel(slot));
        }
        grid[day][slot] = 1;
    }

    public void release(int day, int slot) {
        grid[day][slot] = 0;
    }

    /** Returns a copy so outside code cannot modify the private grid (information hiding). */
    public int[][] getGridCopy() {
        int[][] copy = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" %-16s Rs.%.0f", speciality, fee);
    }
}
