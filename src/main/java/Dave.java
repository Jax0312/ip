/**
 * Represents the main entry point for the Dave chatbot application.
 * Manages user interactions, command execution, and task persistence.
 */
public class Dave {

    /** Path to the data file used for persistent storage. */
    private static final String DATA_FILE_PATH = "data/dave.txt";

    /** User interface handler responsible for all input and output. */
    private static Ui ui = new Ui();
    /** Storage handler responsible for loading and saving tasks on disk. */
    private static Storage storage = new Storage(DATA_FILE_PATH);
    /** List of tasks currently managed by the chatbot. */
    private static TaskList tasks;

    /**
     * Starts the Dave chatbot application and processes user commands until exit.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            tasks = new TaskList(storage.load());
        } catch (DaveCommandException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }

        ui.showWelcome();
        boolean isRunning = true;

        while (isRunning) {
            try {
                String userIn = ui.readCommand();

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
                        ui.showError("I'm afraid I cannot understand you");
                        break;
                }
            } catch (DaveCommandException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.showGoodbye();
    }

    /**
     * Deletes a task from the task list according to the specified 1-based index.
     *
     * @param userIn User input containing the index of the task to be removed.
     */
    private static void deleteTask(String userIn) {
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(userIn);
        } catch (NumberFormatException e) {
            throw new DaveCommandException("Wrong number!");
        }

        Task removedTask = tasks.delete(itemNumber - 1);
        saveTasks();
        ui.showTaskDeleted(removedTask);
    }

    /**
     * Updates the completion status of a task based on the specified 1-based index.
     *
     * @param userIn User input containing the index of the task to be updated.
     * @param isComplete True if the task should be marked as completed, false otherwise.
     */
    private static void updateTaskStatus(String userIn, boolean isComplete) {
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(userIn);
        } catch (NumberFormatException e) {
            throw new DaveCommandException("Wrong number!");
        }

        Task task = tasks.setDone(itemNumber - 1, isComplete);
        saveTasks();
        ui.showTaskStatusUpdated(task, isComplete);
    }

    /**
     * Prints all tasks currently stored in the task list.
     */
    private static void listTask() {
        ui.showTaskList(tasks.asList());
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

        ParsedDateTime parsed = DateTimeParser.parse(attributes[1]);
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

        ParsedDateTime fromParsed = DateTimeParser.parse(fromTo[0]);
        ParsedDateTime toParsed = DateTimeParser.parse(fromTo[1]);

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
        saveTasks();
        ui.showTaskAdded(task);
    }

    /**
     * Saves the current list of tasks to persistent storage via the storage handler.
     */
    private static void saveTasks() {
        try {
            storage.save(tasks.asList());
        } catch (DaveCommandException e) {
            ui.showSavingError(e.getMessage());
        }
    }
}
