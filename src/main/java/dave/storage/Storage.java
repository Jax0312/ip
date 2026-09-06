package dave.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dave.exception.DaveCommandException;
import dave.parser.DateTimeParser;
import dave.parser.ParsedDateTime;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Task;
import dave.task.Todo;

/**
 * Manages persistent storage of tasks in a file on disk.
 * Handles loading tasks from and saving tasks to the specified file path.
 */
public class Storage {

    /** Path to the file where tasks are stored. */
    private final Path filePath;

    /**
     * Constructs a new Storage instance with the specified file path.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the persistent storage file.
     *
     * @return List of tasks loaded from the file, or an empty list if the file does not exist.
     * @throws DaveCommandException If an I/O error occurs while reading the file.
     */
    public ArrayList<Task> load() throws DaveCommandException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(this.filePath)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(this.filePath);
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(" \\| ");
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                Task task;

                try {
                    switch (type) {
                    case "T":
                        task = new Todo(parts[2]);
                        break;
                    case "D":
                        ParsedDateTime deadlineBy = DateTimeParser.parse(parts[3]);
                        task = new Deadline(parts[2], deadlineBy.getDateTime(), deadlineBy.hasTime());
                        break;
                    case "E":
                        ParsedDateTime eventFrom = DateTimeParser.parse(parts[3]);
                        ParsedDateTime eventTo = DateTimeParser.parse(parts[4]);
                        task = new Event(parts[2],
                                eventFrom.getDateTime(), eventFrom.hasTime(),
                                eventTo.getDateTime(), eventTo.hasTime());
                        break;
                    default:
                        continue;
                    }
                } catch (DaveCommandException e) {
                    // Skip corrupted or unparseable task entry
                    continue;
                }

                task.setDone(isDone);
                loadedTasks.add(task);
            }
        } catch (IOException e) {
            throw new DaveCommandException("Unable to load tasks from disk: " + e.getMessage());
        }

        return loadedTasks;
    }

    /**
     * Saves the provided list of tasks to the storage file.
     *
     * @param tasks List of tasks to save to disk.
     * @throws DaveCommandException If an I/O error occurs while writing to the file.
     */
    public void save(List<Task> tasks) throws DaveCommandException {
        try {
            if (this.filePath.getParent() != null && !Files.exists(this.filePath.getParent())) {
                Files.createDirectories(this.filePath.getParent());
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileFormat());
            }

            Files.write(this.filePath, lines);
        } catch (IOException e) {
            throw new DaveCommandException("Unable to save tasks to disk: " + e.getMessage());
        }
    }
}
