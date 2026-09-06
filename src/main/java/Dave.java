import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Dave {

    static String NAME = "Dave";
    static String SEPARATOR = "____________________________________________________________";
    static ArrayList<Task> tasks = new ArrayList<>();

    static final Path DATA_FILE_PATH = Paths.get("data", "dave.txt");

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

    private static void listTask() {
        System.out.println(SEPARATOR);
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.printf("%d. %s\n", i, tasks.get(i - 1));
        }
        System.out.println(SEPARATOR);
    }

    private static void addDeadline(String userIn) {
        String[] attributes = userIn.split(" /by ");
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! A deadline requires /by [time]");
        }
        addTask(new Deadline(attributes[0], attributes[1]));
    }

    private static void addEvent(String userIn) {
        String[] attributes = userIn.split(" /from ");
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }
        String[] fromTo = attributes[1].split(" /to ");
        if (fromTo.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }
        addTask(new Event(attributes[0], fromTo[0], fromTo[1]));
    }

    private static void addTodo(String userIn) {
        if (userIn.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a todo cannot be empty");
        }
        addTask(new Todo(userIn));
    }

    private static void addTask(Task task) {
        tasks.add(task);
        saveList();
        System.out.println(SEPARATOR);
        System.out.printf("added: %s\n", task);
        System.out.println(SEPARATOR);
    }

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

    private static void sendByeMessage() {
        System.out.println(SEPARATOR);
        System.out.println("The wind calls. Farewell!");
        System.out.println(SEPARATOR);
    }

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

                switch (type) {
                    case "T":
                        task = new Todo(parts[2]);
                        break;
                    case "D":
                        task = new Deadline(parts[2], parts[3]);
                        break;
                    case "E":
                        task = new Event(parts[2], parts[3], parts[4]);
                        break;
                    default:
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
}