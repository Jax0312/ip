/**
 * Represents a todo task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a new Todo task with the specified description.
     *
     * @param description Description of the todo task.
     */
    Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return String.format("T | %d | %s", super.getDone() ? 1 : 0, super.getDescription());
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
