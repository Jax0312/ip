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
            String userIn = scanner.nextLine();
            if (userIn.equals("bye")) {
                break;
            } else if (userIn.equals("list")) {
                listTask();
            } else if (userIn.startsWith("mark")) {
                updateTaskStatus(userIn, true);
            } else if (userIn.startsWith("unmark")) {
                updateTaskStatus(userIn, false);
            } else {
                addTask(userIn);
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

    private static void addTask(String description) {
        tasks.add(new Todo(description));
        System.out.println(SEPARATOR);
        System.out.printf("added: %s\n", description);
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
