package dave;

import java.util.ArrayList;

import dave.command.Command;
import dave.exception.DaveCommandException;
import dave.parser.Parser;
import dave.storage.Storage;
import dave.task.Task;
import dave.task.TaskList;
import dave.ui.Ui;

/**
 * Represents the main entry point for the Dave chatbot application.
 * Manages user interactions, command execution, and task persistence.
 */
public class Dave {

    /** Default path to the data file used for persistent storage. */
    private static final String DEFAULT_FILE_PATH = "data/dave.txt";

    /** User interface handler responsible for all input and output. */
    private final Ui ui;
    /** Storage handler responsible for loading and saving tasks on disk. */
    private final Storage storage;
    /** List of tasks currently managed by the chatbot. */
    private TaskList tasks;
    /** Flag indicating whether the chatbot application should terminate. */
    private boolean isExit;

    /**
     * Constructs a new Dave chatbot application instance with the default data file path.
     */
    public Dave() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a new Dave chatbot application instance with the specified data file path.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Dave(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.isExit = false;
        try {
            this.tasks = new TaskList(this.storage.load());
        } catch (DaveCommandException e) {
            this.ui.showLoadingError(e.getMessage());
            this.tasks = new TaskList();
        }
    }

    /**
     * Returns whether the chatbot application has received an exit command.
     *
     * @return True if Dave has been signaled to exit, false otherwise.
     */
    public boolean isExit() {
        return this.isExit;
    }

    /**
     * Returns the welcome greeting message from the chatbot.
     *
     * @return Welcome greeting string.
     */
    public String getWelcome() {
        return this.ui.formatWelcome();
    }

    /**
     * Generates a response string for a given user command input.
     *
     * @param input Raw command input from the user.
     * @return Formatted response string to display to the user.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        try {
            Command command = Parser.parseCommand(input);
            String arguments = Parser.parseArguments(input);

            switch (command) {
                case BYE:
                    this.isExit = true;
                    return this.ui.formatGoodbye();
                case LIST:
                    return this.ui.formatTaskList(this.tasks.asList());
                case FIND:
                    return findTasks(arguments);
                case MARK:
                    return updateTaskStatus(arguments, true);
                case UNMARK:
                    return updateTaskStatus(arguments, false);
                case TODO:
                    return addTask(Parser.parseTodo(arguments));
                case DEADLINE:
                    return addTask(Parser.parseDeadline(arguments));
                case EVENT:
                    return addTask(Parser.parseEvent(arguments));
                case DELETE:
                    return deleteTask(arguments);
                case UNKNOWN:
                    // Fallthrough
                default:
                    return "I'm afraid I cannot understand you";
            }
        } catch (DaveCommandException e) {
            return e.getMessage();
        }
    }

    /**
     * Runs the Dave chatbot application, reading and processing user commands until exit.
     */
    public void run() {
        this.ui.showWelcome();

        while (!this.isExit) {
            String userIn = this.ui.readCommand();

            if (userIn.isEmpty()) {
                continue;
            }

            String response = getResponse(userIn);
            this.ui.showMessage(response);
        }
    }

    /**
     * Finds tasks matching the specified keyword and returns the formatted result.
     *
     * @param arguments User input arguments containing the search keyword.
     * @return Formatted string containing matching tasks.
     * @throws DaveCommandException If parsing the keyword fails.
     */
    private String findTasks(String arguments) throws DaveCommandException {
        String keyword = Parser.parseFind(arguments);
        ArrayList<Task> matchingTasks = this.tasks.find(keyword);
        return this.ui.formatMatchingTasks(matchingTasks);
    }

    /**
     * Deletes a task from the task list according to the specified 1-based index string.
     *
     * @param arguments User input arguments containing the index of the task to be removed.
     * @return Formatted string confirming task removal.
     * @throws DaveCommandException If parsing the index or deleting the task fails.
     */
    private String deleteTask(String arguments) throws DaveCommandException {
        int index = Parser.parseIndex(arguments);
        Task removedTask = this.tasks.delete(index);
        saveTasks();
        return this.ui.formatTaskDeleted(removedTask);
    }

    /**
     * Updates the completion status of a task based on the specified 1-based index string.
     *
     * @param arguments User input arguments containing the index of the task to be updated.
     * @param isComplete True if the task should be marked as completed, false otherwise.
     * @return Formatted string confirming task status update.
     * @throws DaveCommandException If parsing the index or updating status fails.
     */
    private String updateTaskStatus(String arguments, boolean isComplete) throws DaveCommandException {
        int index = Parser.parseIndex(arguments);
        Task task = this.tasks.setDone(index, isComplete);
        saveTasks();
        return this.ui.formatTaskStatusUpdated(task, isComplete);
    }

    /**
     * Adds the specified task to the task list, saves the updated list to disk, and returns confirmation.
     *
     * @param task Task to be added.
     * @return Formatted string confirming task addition.
     * @throws DaveCommandException If saving tasks to persistent storage fails.
     */
    private String addTask(Task task) throws DaveCommandException {
        this.tasks.add(task);
        saveTasks();
        return this.ui.formatTaskAdded(task);
    }

    /**
     * Saves the current list of tasks to persistent storage via the storage handler.
     *
     * @throws DaveCommandException If saving tasks fails.
     */
    private void saveTasks() throws DaveCommandException {
        this.storage.save(this.tasks.asList());
    }

    /**
     * Starts the Dave chatbot application in terminal mode.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Dave(DEFAULT_FILE_PATH).run();
    }
}
