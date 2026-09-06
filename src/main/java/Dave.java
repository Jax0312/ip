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

    /**
     * Constructs a new Dave chatbot application instance with the specified data file path.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Dave(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(this.storage.load());
        } catch (DaveCommandException e) {
            this.ui.showLoadingError(e.getMessage());
            this.tasks = new TaskList();
        }
    }

    /**
     * Runs the Dave chatbot application, reading and processing user commands until exit.
     */
    public void run() {
        this.ui.showWelcome();
        boolean isRunning = true;

        while (isRunning) {
            try {
                String userIn = this.ui.readCommand();

                if (userIn.isEmpty()) {
                    continue;
                }

                Command command = Parser.parseCommand(userIn);
                String arguments = Parser.parseArguments(userIn);

                switch (command) {
                    case BYE:
                        isRunning = false;
                        break;
                    case LIST:
                        this.ui.showTaskList(this.tasks.asList());
                        break;
                    case MARK:
                        updateTaskStatus(arguments, true);
                        break;
                    case UNMARK:
                        updateTaskStatus(arguments, false);
                        break;
                    case TODO:
                        addTask(Parser.parseTodo(arguments));
                        break;
                    case DEADLINE:
                        addTask(Parser.parseDeadline(arguments));
                        break;
                    case EVENT:
                        addTask(Parser.parseEvent(arguments));
                        break;
                    case DELETE:
                        deleteTask(arguments);
                        break;
                    case UNKNOWN:
                        // Fallthrough
                    default:
                        this.ui.showError("I'm afraid I cannot understand you");
                        break;
                }
            } catch (DaveCommandException e) {
                this.ui.showError(e.getMessage());
            }
        }
        this.ui.showGoodbye();
    }

    /**
     * Deletes a task from the task list according to the specified 1-based index string.
     *
     * @param arguments User input arguments containing the index of the task to be removed.
     */
    private void deleteTask(String arguments) {
        int index = Parser.parseIndex(arguments);
        Task removedTask = this.tasks.delete(index);
        saveTasks();
        this.ui.showTaskDeleted(removedTask);
    }

    /**
     * Updates the completion status of a task based on the specified 1-based index string.
     *
     * @param arguments User input arguments containing the index of the task to be updated.
     * @param isComplete True if the task should be marked as completed, false otherwise.
     */
    private void updateTaskStatus(String arguments, boolean isComplete) {
        int index = Parser.parseIndex(arguments);
        Task task = this.tasks.setDone(index, isComplete);
        saveTasks();
        this.ui.showTaskStatusUpdated(task, isComplete);
    }

    /**
     * Adds the specified task to the task list, saves the updated list to disk, and confirms addition.
     *
     * @param task Task to be added.
     */
    private void addTask(Task task) {
        this.tasks.add(task);
        saveTasks();
        this.ui.showTaskAdded(task);
    }

    /**
     * Saves the current list of tasks to persistent storage via the storage handler.
     */
    private void saveTasks() {
        try {
            this.storage.save(this.tasks.asList());
        } catch (DaveCommandException e) {
            this.ui.showSavingError(e.getMessage());
        }
    }

    /**
     * Starts the Dave chatbot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Dave(DEFAULT_FILE_PATH).run();
    }
}
