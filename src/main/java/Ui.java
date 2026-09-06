import java.util.List;
import java.util.Scanner;

/**
 * Handles user interactions for the Dave chatbot application.
 * Responsible for reading user input and displaying messages and error alerts.
 */
public class Ui {

    /** Line separator printed between console messages. */
    private static final String SEPARATOR = "____________________________________________________________";
    /** Name of the chatbot. */
    private static final String NAME = "Dave";
    /** Welcome banner displayed upon application start. */
    private static final String BANNER = """
            ____
            |  _ \\  __ ___   _____\s
            | | | |/ _` \\ \\ / / _ \\
            | |_| | (_| |\\ V /  __/
            |____/ \\__,_| \\_/ \\___|
            """;

    /** Scanner used to read input from the standard input stream. */
    private final Scanner scanner;

    /**
     * Constructs a new Ui instance with a standard input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of input from the user.
     *
     * @return Trimmed input line entered by the user.
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Displays a horizontal line separator to standard output.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the welcome banner and greeting message to the user.
     */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.printf("Hello! I'm %s.\nAt your service!\n", NAME);
        showLine();
    }

    /**
     * Displays the farewell exit message to the user.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("The wind calls. Farewell!");
        showLine();
    }

    /**
     * Displays a custom error or status message enclosed in line separators.
     *
     * @param message Error or status message to be displayed.
     */
    public void showError(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Displays a warning when tasks could not be loaded from persistent storage.
     *
     * @param message Detailed error description.
     */
    public void showLoadingError(String message) {
        showLine();
        System.out.println("Warning: Unable to load tasks from disk: " + message);
        showLine();
    }

    /**
     * Displays a warning when tasks could not be saved to persistent storage.
     *
     * @param message Detailed error description.
     */
    public void showSavingError(String message) {
        showLine();
        System.out.println("Warning: Unable to save tasks to disk: " + message);
        showLine();
    }

    /**
     * Displays confirmation that a task has been added to the task list.
     *
     * @param task Task that was added.
     */
    public void showTaskAdded(Task task) {
        showLine();
        System.out.printf("added: %s\n", task);
        showLine();
    }

    /**
     * Displays confirmation that a task has been removed from the task list.
     *
     * @param task Task that was removed.
     */
    public void showTaskDeleted(Task task) {
        showLine();
        System.out.println("Affirmative! This task was removed:");
        System.out.printf("    %s\n", task);
        showLine();
    }

    /**
     * Displays confirmation that a task's status has been updated.
     *
     * @param task Task whose status changed.
     * @param isComplete True if the task was marked completed, false if uncompleted.
     */
    public void showTaskStatusUpdated(Task task, boolean isComplete) {
        showLine();
        if (isComplete) {
            System.out.println("Another one down!");
        } else {
            System.out.println("Negative progress...");
        }
        System.out.println(task);
        showLine();
    }

    /**
     * Displays the full list of tasks with 1-based indexing.
     *
     * @param tasks List of tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        showLine();
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.printf("%d. %s\n", i, tasks.get(i - 1));
        }
        showLine();
    }
}
