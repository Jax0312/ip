/**
 * Represents an abstract task in the Dave application.
 */
public abstract class Task {

    /** Description of the task. */
    private final String description;
    /** Completion status of the task. */
    private boolean isDone;

    /**
     * Constructs a new Task with the specified description.
     *
     * @param description Description of the task.
     */
    Task(String description) {
        this.description = description;
    }

    /**
     * Sets the completion status of the task.
     *
     * @param isMark True if the task is completed, false otherwise.
     */
    public void setDone(boolean isMark) {
        this.isDone = isMark;
    }

    /**
     * Formats the task into a string representation suitable for file storage.
     *
     * @return Formatted string representation for file storage.
     */
    public abstract String toFileFormat();

    /**
     * Returns the completion status of the task.
     *
     * @return True if the task is done, false otherwise.
     */
    public boolean getDone() {
        return this.isDone;
    }

    /**
     * Returns the description of the task.
     *
     * @return Description of the task.
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        String statusString = this.isDone ? "X" : " ";
        return String.format("[%s] %s", statusString, this.description);
    }
}
