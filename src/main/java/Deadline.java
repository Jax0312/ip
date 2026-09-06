/**
 * Represents a task that needs to be completed before a specific deadline.
 */
public class Deadline extends Task {

    /** Due date or time of the deadline. */
    private final String by;

    /**
     * Constructs a new Deadline task with the specified description and due time.
     *
     * @param description Description of the deadline task.
     * @param by Due date or time for the task.
     */
    Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return String.format("D | %d | %s | %s", super.getDone() ? 1 : 0, super.getDescription(), by);
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.by);
    }
}
