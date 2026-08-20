import java.util.ArrayList;
import java.util.Scanner;

public class Dave {

    static String NAME = "Dave";
    static String SEPARATOR = "____________________________________________________________";
    static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {

        sendGreetings();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
            String userIn = scanner.nextLine();
            if (userIn.equals("bye")) {
                break;
            } else if (userIn.equals("list")) {
                listTask();
            } else if (userIn.startsWith("mark")) {
                updateTaskStatus(userIn, true);
            } else if (userIn.startsWith("unmark")) {
                updateTaskStatus(userIn, false);
            } else if (userIn.startsWith("todo")) {
                addTodo(userIn);
            } else if (userIn.startsWith("deadline")) {
                addDeadline(userIn);
            } else if (userIn.startsWith("event")) {
                addEvent(userIn);
            } else {
                System.out.println(SEPARATOR);
                System.out.println("I'm afraid I cannot understand you");
                System.out.println(SEPARATOR);
            }
            } catch (DaveCommandException e) {
                System.out.println(SEPARATOR);
                System.out.println(e.getMessage());
                System.out.println(SEPARATOR);
            }
        }
        sendByeMessage();
    }

    private static void updateTaskStatus(String userIn, boolean complete) {
        // assume correct input format
        int itemNumber = Integer.parseInt(userIn.split(" ")[1]);
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
        userIn = userIn.substring(8);
        String[] attributes = userIn.split(" /by ");
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! A deadline requires /by [time]");
        }
        addTask(new Deadline(attributes[0], attributes[1]));
    }

    private static void addEvent(String userIn) {
        userIn =  userIn.substring(5);
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
        String description = userIn.substring(4);
        if (description.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a todo cannot be empty");
        }
        addTask(new Todo(userIn.substring(5)));
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
