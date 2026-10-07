import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * TOPIC: File I/O - Path/Files, BufferedReader/Writer, try-with-resources, append, missing files.
 */
public class FileStore {
    private final Path dir;

    public FileStore(String dirName) throws IOException {
        this.dir = Paths.get(dirName);
        Files.createDirectories(dir);
    }

    /** Line-by-line read. A missing file is NOT an error: it just means "no data yet". */
    public List<String> readLines(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        Path file = dir.resolve(fileName);
        if (!Files.exists(file)) {
            return lines;
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {     // auto-closed
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /** Overwrites the file with the given lines. */
    public void writeLines(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(dir.resolve(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /** Appends one line (creates the file if needed). */
    public void appendLine(String fileName, String line) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(dir.resolve(fileName),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }

    /** Reads the complete file in one go. */
    public String readAll(String fileName) throws IOException {
        Path file = dir.resolve(fileName);
        return Files.exists(file) ? Files.readString(file) : "(nothing logged yet)\n";
    }
}
