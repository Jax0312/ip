/**
 * Represents the commands supported by the Dave chatbot application.
 */
public enum Command {
    /** Exits the application. */
    BYE,
    /** Displays all tasks in the task list. */
    LIST,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Adds a todo task. */
    TODO,
    /** Adds a deadline task with a due date. */
    DEADLINE,
    /** Adds an event task with start and end times. */
    EVENT,
    /** Deletes a task from the list. */
    DELETE,
    /** Represents an unrecognized command. */
    UNKNOWN;

    /**
     * Converts a command word into its corresponding Command enum constant.
     * If the command word does not match any known command, UNKNOWN is returned.
     *
     * @param commandWord Raw command word entered by the user.
     * @return Corresponding Command constant, or UNKNOWN if invalid.
     */
    public static Command from(String commandWord) {
        try {
            return Command.valueOf(commandWord.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
