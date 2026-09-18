package dave.ui;

import java.util.List;
import java.util.Scanner;

import dave.task.Task;

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
            |  _ \\  __ ___   _____\\s
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
        System.out.println(formatGoodbye());
        showLine();
    }

    /**
     * Displays a message enclosed in horizontal line separators.
     *
     * @param message Message to be displayed.
     */
    public void showMessage(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Displays a custom error or status message enclosed in line separators.
     *
     * @param message Error or status message to be displayed.
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Displays a warning when tasks could not be loaded from persistent storage.
     *
     * @param message Detailed error description.
     */
    public void showLoadingError(String message) {
        showMessage("Warning: Unable to load tasks from disk: " + message);
    }

    /**
     * Displays a warning when tasks could not be saved to persistent storage.
     *
     * @param message Detailed error description.
     */
    public void showSavingError(String message) {
        showMessage("Warning: Unable to save tasks to disk: " + message);
    }

    /**
     * Displays confirmation that a task has been added to the task list.
     *
     * @param task Task that was added.
     */
    public void showTaskAdded(Task task) {
        showMessage(formatTaskAdded(task));
    }

    /**
     * Displays confirmation that a task has been removed from the task list.
     *
     * @param task Task that was removed.
     */
    public void showTaskDeleted(Task task) {
        showMessage(formatTaskDeleted(task));
    }

    /**
     * Displays confirmation that a task's status has been updated.
     *
     * @param task Task whose status changed.
     * @param isComplete True if the task was marked completed, false if uncompleted.
     */
    public void showTaskStatusUpdated(Task task, boolean isComplete) {
        showMessage(formatTaskStatusUpdated(task, isComplete));
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

    /**
     * Displays matching tasks found by keyword search with 1-based indexing.
     *
     * @param tasks List of matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showLine();
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.printf("%d.%s\n", i, tasks.get(i - 1));
        }
        showLine();
    }

    /**
     * Formats the welcome greeting message.
     *
     * @return Formatted welcome greeting string.
     */
    public String formatWelcome() {
        return String.format("Hello! I'm %s.\nAt your service!", NAME);
    }

    /**
     * Formats the farewell exit message.
     *
     * @return Formatted farewell message string.
     */
    public String formatGoodbye() {
        return "The wind calls. Farewell!";
    }

    /**
     * Formats confirmation that a task has been added to the task list.
     *
     * @param task Task that was added.
     * @return Formatted task addition confirmation string.
     */
    public String formatTaskAdded(Task task) {
        return String.format("added: %s", task);
    }

    /**
     * Formats confirmation that a task has been removed from the task list.
     *
     * @param task Task that was removed.
     * @return Formatted task removal confirmation string.
     */
    public String formatTaskDeleted(Task task) {
        return String.format("Affirmative! This task was removed:\n    %s", task);
    }

    /**
     * Formats confirmation that a task's status has been updated.
     *
     * @param task Task whose status changed.
     * @param isComplete True if the task was marked completed, false if uncompleted.
     * @return Formatted task status update string.
     */
    public String formatTaskStatusUpdated(Task task, boolean isComplete) {
        String statusMessage = isComplete ? "Another one down!" : "Negative progress...";
        return String.format("%s\n%s", statusMessage, task);
    }

    /**
     * Formats the full list of tasks with 1-based indexing.
     *
     * @param tasks List of tasks to format.
     * @return Formatted task list string.
     */
    public String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "There are no tasks in your list.";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= tasks.size(); i++) {
            builder.append(String.format("%d. %s", i, tasks.get(i - 1)));
            if (i < tasks.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Formats matching tasks found by keyword search with 1-based indexing.
     *
     * @param tasks List of matching tasks to format.
     * @return Formatted matching tasks string.
     */
    public String formatMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks found in your list.";
        }
        StringBuilder builder = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 1; i <= tasks.size(); i++) {
            builder.append(String.format("%d.%s", i, tasks.get(i - 1)));
            if (i < tasks.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
