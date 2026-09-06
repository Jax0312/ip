package dave.task;

/**
 * Represents a simple todo task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a new Todo task with the specified description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return String.format("T | %d | %s", this.isDone() ? 1 : 0, this.getDescription());
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
