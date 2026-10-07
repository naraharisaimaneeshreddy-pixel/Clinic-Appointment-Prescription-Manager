/**
 * TOPIC: 1D / 2D arrays, traversal, summation, matrix addition, recursion.
 * A grid is int[DAYS][SLOTS]: 1 = booked, 0 = free.
 */
public final class ScheduleGrid {
    private ScheduleGrid() { }

    public static int[][] newGrid() {
        return new int[Constants.DAYS][Constants.SLOTS];
    }

    /** Matrix addition: cell-by-cell sum (used to combine all doctors into one clinic-load grid). */
    public static int[][] add(int[][] a, int[][] b) {
        int[][] result = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    /** Summation over a 1D row (one day). */
    public static int rowTotal(int[] row) {
        int sum = 0;
        for (int cell : row) {            // enhanced for-each
            sum += cell;
        }
        return sum;
    }

    /** Counting technique over the whole 2D array. */
    public static int totalBooked(int[][] grid) {
        int count = 0;
        for (int[] row : grid) {
            count += rowTotal(row);
        }
        return count;
    }

    /**
     * RECURSION. Base cases: ran past the last slot (-1) or found a free slot.
     * Recursive case: look at the next slot.
     */
    public static int nextFreeSlot(int[][] grid, int day, int slot) {
        if (slot >= Constants.SLOTS) {
            return -1;                                   // base case 1: no free slot left today
        }
        if (grid[day][slot] == 0) {
            return slot;                                 // base case 2: found one
        }
        return nextFreeSlot(grid, day, slot + 1);        // recursive case
    }

    public static String timeLabel(int slot) {
        return String.format("%02d:00", Constants.START_HOUR + slot);
    }

    /** Builds a printable table using StringBuilder (mutable string). */
    public static String render(int[][] grid) {
        StringBuilder sb = new StringBuilder("      ");
        for (int s = 0; s < Constants.SLOTS; s++) {
            sb.append(String.format("%6s", timeLabel(s)));
        }
        sb.append("  | Total\n");
        for (int d = 0; d < Constants.DAYS; d++) {
            sb.append(String.format("%-6s", Constants.DAY_NAMES[d]));
            for (int s = 0; s < Constants.SLOTS; s++) {
                sb.append(String.format("%6d", grid[d][s]));
            }
            sb.append(String.format("  | %d%n", rowTotal(grid[d])));
        }
        return sb.toString();
    }
}
