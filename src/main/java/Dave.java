import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the main entry point for the Dave chatbot application.
 * Manages user interactions, command execution, and task persistence.
 */
public class Dave {

    /** Name of the chatbot. */
    static String NAME = "Dave";
    /** Line separator printed between console messages. */
    static String SEPARATOR = "____________________________________________________________";
    /** List of tasks currently managed by the chatbot. */
    static ArrayList<Task> tasks = new ArrayList<>();

    /** Path to the data file used for persistent storage. */
    static final Path DATA_FILE_PATH = Paths.get("data", "dave.txt");

    /**
     * Starts the Dave chatbot application and processes user commands until exit.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        tasks = loadList();
        sendGreetings();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            try {
                String userIn = scanner.nextLine().trim();

                if (userIn.isEmpty()) {
                    continue;
                }
                String[] parts = userIn.split("\\s+", 2);
                Command command = Command.from(parts[0]);

                switch (command) {
                    case BYE:
                        isRunning = false;
                        break;
                    case LIST:
                        listTask();
                        break;
                    case MARK:
                        updateTaskStatus(parts[1], true);
                        break;
                    case UNMARK:
                        updateTaskStatus(parts[1], false);
                        break;
                    case TODO:
                        addTodo(parts[1]);
                        break;
                    case DEADLINE:
                        addDeadline(parts[1]);
                        break;
                    case EVENT:
                        addEvent(parts[1]);
                        break;
                    case DELETE:
                        deleteTask(parts[1]);
                        break;
                    case UNKNOWN:
                        // Fallthrough
                    default:
                        System.out.println(SEPARATOR);
                        System.out.println("I'm afraid I cannot understand you");
                        System.out.println(SEPARATOR);
                        break;
                }
            } catch (DaveCommandException e) {
                System.out.println(SEPARATOR);
                System.out.println(e.getMessage());
                System.out.println(SEPARATOR);
            }
        }
        sendByeMessage();
    }

    /**
     * Deletes a task from the task list according to the specified 1-based index.
     *
     * @param userIn User input containing the index of the task to be removed.
     */
    private static void deleteTask(String userIn) {
        int itemNumber = Integer.parseInt(userIn);
        if (itemNumber < 1 || itemNumber > tasks.size()) {
            System.out.println(SEPARATOR);
            System.out.println("Wrong number!");
            System.out.println(SEPARATOR);
            return;
        }

        Task removedTask = tasks.remove(itemNumber - 1);
        saveList();
        System.out.println(SEPARATOR);
        System.out.println("Affirmative! This task was removed:");
        System.out.printf("    %s\n", removedTask);
        System.out.println(SEPARATOR);
    }

    /**
     * Updates the completion status of a task based on the specified 1-based index.
     *
     * @param userIn User input containing the index of the task to be updated.
     * @param isComplete True if the task should be marked as completed, false otherwise.
     */
    private static void updateTaskStatus(String userIn, boolean isComplete) {
        int itemNumber = Integer.parseInt(userIn);
        if (itemNumber < 1 || itemNumber > tasks.size()) {
            System.out.println(SEPARATOR);
            System.out.println("Wrong number!");
            System.out.println(SEPARATOR);
            return;
        }

        Task task = tasks.get(itemNumber - 1);
        task.setDone(isComplete);
        saveList();
        System.out.println(SEPARATOR);
        if (isComplete) {
            System.out.println("Another one down!");
        } else {
            System.out.println("Negative progress...");
        }
        System.out.println(task);
        System.out.println(SEPARATOR);
    }

    /**
     * Prints all tasks currently stored in the task list.
     */
    private static void listTask() {
        System.out.println(SEPARATOR);
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.printf("%d. %s\n", i, tasks.get(i - 1));
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Parses the deadline description and date/time from user input, and adds the deadline task.
     *
     * @param userIn User input containing deadline description and '/by' date/time in yyyy-MM-dd or yyyy-MM-dd HH:mm.
     * @throws DaveCommandException If delimiters are missing, description is empty, or date/time format is invalid.
     */
    private static void addDeadline(String userIn) {
        String[] attributes = userIn.split(" /by ");
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! A deadline requires /by [time]");
        }
        if (attributes[0].trim().isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a deadline cannot be empty");
        }

        ParsedDateTime parsed = parseDateTime(attributes[1]);
        addTask(new Deadline(attributes[0].trim(), parsed.getDateTime(), parsed.hasTime()));
    }

    /**
     * Parses the event description and dates/times from user input, and adds the event task.
     *
     * @param userIn User input containing description, '/from' date/time, and '/to' date/time.
     * @throws DaveCommandException If delimiters are missing, description is empty, or date/time formats are invalid.
     */
    private static void addEvent(String userIn) {
        String[] attributes = userIn.split(" /from ");
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }
        if (attributes[0].trim().isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of an event cannot be empty");
        }

        String[] fromTo = attributes[1].split(" /to ");
        if (fromTo.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }

        ParsedDateTime fromParsed = parseDateTime(fromTo[0]);
        ParsedDateTime toParsed = parseDateTime(fromTo[1]);

        addTask(new Event(attributes[0].trim(),
                fromParsed.getDateTime(), fromParsed.hasTime(),
                toParsed.getDateTime(), toParsed.hasTime()));
    }

    /**
     * Parses the todo description from user input and adds the todo task.
     *
     * @param userIn User input containing the todo description.
     * @throws DaveCommandException If the todo description is empty.
     */
    private static void addTodo(String userIn) {
        if (userIn.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a todo cannot be empty");
        }
        addTask(new Todo(userIn));
    }

    /**
     * Adds the specified task to the task list, saves the updated list to disk, and confirms addition.
     *
     * @param task Task to be added.
     */
    private static void addTask(Task task) {
        tasks.add(task);
        saveList();
        System.out.println(SEPARATOR);
        System.out.printf("added: %s\n", task);
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the welcome banner and greeting message to the user.
     */
    private static void sendGreetings() {
        String banner = """
                ____
                |  _ \\  __ ___   _____\s
                | | | |/ _` \\ \\ / / _ \\
                | |_| | (_| |\\ V /  __/
                |____/ \\__,_| \\_/ \\___|
                """;
        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.printf("Hello! I'm %s.\nAt your service!\n", NAME);
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the farewell exit message to the user.
     */
    private static void sendByeMessage() {
        System.out.println(SEPARATOR);
        System.out.println("The wind calls. Farewell!");
        System.out.println(SEPARATOR);
    }

    /**
     * Loads tasks from the persistent storage file into a list of tasks.
     *
     * @return List of tasks loaded from the storage file, or an empty list if the file is missing.
     */
    private static ArrayList<Task> loadList() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE_PATH)) {
            return loadedTasks;
        }

        try {
            List<String> lines = Files.readAllLines(DATA_FILE_PATH);
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
                            ParsedDateTime deadlineBy = parseDateTime(parts[3]);
                            task = new Deadline(parts[2], deadlineBy.getDateTime(), deadlineBy.hasTime());
                            break;
                        case "E":
                            ParsedDateTime eventFrom = parseDateTime(parts[3]);
                            ParsedDateTime eventTo = parseDateTime(parts[4]);
                            task = new Event(parts[2],
                                    eventFrom.getDateTime(), eventFrom.hasTime(),
                                    eventTo.getDateTime(), eventTo.hasTime());
                            break;
                        default:
                            continue;
                    }
                } catch (DaveCommandException e) {
                    System.out.println(SEPARATOR);
                    System.out.println("Warning: Skipping task with invalid date format: " + line);
                    System.out.println(SEPARATOR);
                    continue;
                }

                task.setDone(isDone);
                loadedTasks.add(task);
            }
        } catch (IOException e) {
            System.out.println(SEPARATOR);
            System.out.println("Warning: Unable to load tasks from disk: " + e.getMessage());
            System.out.println(SEPARATOR);
        }

        return loadedTasks;
    }

    /**
     * Saves the current list of tasks to the persistent storage file.
     */
    private static void saveList() {
        try {
            if (DATA_FILE_PATH.getParent() != null && !Files.exists(DATA_FILE_PATH.getParent())) {
                Files.createDirectories(DATA_FILE_PATH.getParent());
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileFormat());
            }

            Files.write(DATA_FILE_PATH, lines);
        } catch (IOException e) {
            System.out.println(SEPARATOR);
            System.out.println("Warning: Unable to save tasks to disk: " + e.getMessage());
            System.out.println(SEPARATOR);
        }
    }

    /**
     * Parses a date or date-time string into a ParsedDateTime object.
     * Supports formats such as yyyy-MM-dd HH:mm, yyyy-MM-dd HHmm, and yyyy-MM-dd.
     *
     * @param input Raw date or date-time string from user input or file storage.
     * @return Parsed date-time along with a flag indicating if time was specified.
     * @throws DaveCommandException If the string cannot be parsed using any supported format.
     */
    private static ParsedDateTime parseDateTime(String input) {
        String trimmed = input.trim();

        DateTimeFormatter[] dateTimeFormatters = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        for (DateTimeFormatter formatter : dateTimeFormatters) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(trimmed, formatter);
                return new ParsedDateTime(ldt, true);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        try {
            LocalDate ld = LocalDate.parse(trimmed);
            return new ParsedDateTime(ld.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // Not a date-only string either
        }

        throw new DaveCommandException(
                "NEGATIVE! Date must be in yyyy-MM-dd or yyyy-MM-dd HH:mm format (e.g., 2019-10-15 or 2005-12-22 16:00)");
    }

    /**
     * Represents a parsed date and optional time.
     */
    private static class ParsedDateTime {

        /** Parsed date and time value. */
        private final LocalDateTime dateTime;
        /** Indicates whether time of day was explicitly specified. */
        private final boolean hasTime;

        /**
         * Constructs a ParsedDateTime with the specified date-time and time presence flag.
         *
         * @param dateTime Parsed LocalDateTime object.
         * @param hasTime True if time was specified, false if date only.
         */
        ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }

        /**
         * Returns the parsed date and time value.
         *
         * @return Parsed LocalDateTime object.
         */
        public LocalDateTime getDateTime() {
            return this.dateTime;
        }

        /**
         * Returns whether time of day was explicitly specified.
         *
         * @return True if time was specified, false otherwise.
         */
        public boolean hasTime() {
            return this.hasTime;
        }
    }
}
