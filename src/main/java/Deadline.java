import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that needs to be completed before a specific deadline date and time.
 */
public class Deadline extends Task {

    /** Formatter for displaying date only to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    /** Formatter for displaying date and time to the user. */
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    /** Due date and time of the deadline. */
    private final LocalDateTime by;
    /** Indicates whether a specific time was provided. */
    private final boolean hasTime;

    /**
     * Constructs a new Deadline task with the specified description, due date/time, and time presence flag.
     *
     * @param description Description of the deadline task.
     * @param by Due date and time for the task.
     * @param hasTime True if a specific time was specified, false if date only.
     */
    Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Constructs a new Deadline task with the specified description and due date/time.
     *
     * @param description Description of the deadline task.
     * @param by Due date and time for the task.
     */
    Deadline(String description, LocalDateTime by) {
        this(description, by, true);
    }

    /**
     * Constructs a new Deadline task with the specified description and date only.
     *
     * @param description Description of the deadline task.
     * @param by Due date for the task.
     */
    Deadline(String description, LocalDate by) {
        this(description, by.atStartOfDay(), false);
    }

    /**
     * Returns the due date and time of the deadline.
     *
     * @return Due date and time of the deadline.
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    /**
     * Returns whether a specific time was provided for the deadline.
     *
     * @return True if time was specified, false otherwise.
     */
    public boolean hasTime() {
        return this.hasTime;
    }

    @Override
    public String toFileFormat() {
        String timeString = this.hasTime
                ? this.by.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : this.by.toLocalDate().toString();
        return String.format("D | %d | %s | %s", super.getDone() ? 1 : 0, super.getDescription(), timeString);
    }

    @Override
    public String toString() {
        String formattedTime = this.hasTime
                ? this.by.format(DISPLAY_DATETIME_FORMATTER)
                : this.by.format(DISPLAY_DATE_FORMATTER);
        return String.format("[D]%s (by: %s)", super.toString(), formattedTime);
    }
}
