/**
 * Represents a task that occurs within a specific time period.
 */
public class Event extends Task {

    /** Start time or date of the event. */
    private final String from;
    /** End time or date of the event. */
    private final String to;

    /**
     * Constructs a new Event task with the specified description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start time or date of the event.
     * @param to End time or date of the event.
     */
    Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return String.format("E | %d | %s | %s | %s", super.getDone() ? 1 : 0, super.getDescription(), from, to);
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), this.from, this.to);
    }
}
