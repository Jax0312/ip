import java.util.ArrayList;
import java.util.Scanner;

public class Dave {

    static String NAME = "Dave";
    static String SEPARATOR = "____________________________________________________________";
    static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {

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
        System.out.println(SEPARATOR);
        System.out.println("Affirmative! This task was removed:");
        System.out.printf("    %s\n", removedTask);
        System.out.println(SEPARATOR);
    }

    private static void updateTaskStatus(String userIn, boolean complete) {
        // assume correct input format
        int itemNumber = Integer.parseInt(userIn);
        if (itemNumber < 1 || itemNumber > tasks.size()) {
            System.out.println(SEPARATOR);
            System.out.println("Wrong number!");
            System.out.println(SEPARATOR);
            return;
        }

        Task task = tasks.get(itemNumber - 1);
        task.setMark(complete);
        System.out.println(SEPARATOR);
        if (complete) {
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
        String[] time = attributes[1].split(" /to ");
        if (time.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }
        addTask(new Event(attributes[0], time[0], time[1]));
    }

    private static void addTodo(String userIn) {
        if (userIn.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a todo cannot be empty");
        }
        addTask(new Todo(userIn));
    }

    private static void addTask(Task task) {
        tasks.add(task);
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
}
