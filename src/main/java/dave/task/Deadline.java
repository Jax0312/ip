package dave.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed before a specified deadline.
 */
public class Deadline extends Task {

    /** Formatter for displaying date-only deadlines. */
    private static final DateTimeFormatter DATE_ONLY_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Formatter for displaying date and time deadlines. */
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    /** Formatter for serializing date-time to persistent storage. */
    private static final DateTimeFormatter DATE_TIME_FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Deadline date and optional time. */
    private final LocalDateTime by;
    /** Indicates whether time of day was explicitly specified. */
    private final boolean hasTime;

    /**
     * Constructs a new Deadline task with description, deadline date-time, and time presence flag.
     *
     * @param description Description of the task.
     * @param by Deadline date and time.
     * @param hasTime True if time was specified, false if date only.
     */
    public Deadline(String description, LocalDateTime by, boolean hasTime) {
        super(description);
        this.by = by;
        this.hasTime = hasTime;
    }

    /**
     * Constructs a new Deadline task with description and deadline date-time with time of day.
     *
     * @param description Description of the task.
     * @param by Deadline date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, by, true);
    }

    /**
     * Constructs a new Deadline task with description and date-only deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date.
     */
    public Deadline(String description, LocalDate by) {
        this(description, by.atStartOfDay(), false);
    }

    @Override
    public String toFileFormat() {
        String byString = this.hasTime
                ? this.by.format(DATE_TIME_FILE_FORMATTER)
                : this.by.toLocalDate().toString();
        return String.format("D | %d | %s | %s", this.getDone() ? 1 : 0, this.getDescription(), byString);
    }

    @Override
    public String toString() {
        String byString = this.hasTime
                ? this.by.format(DATE_TIME_OUTPUT_FORMATTER)
                : this.by.format(DATE_ONLY_OUTPUT_FORMATTER);
        return String.format("[D]%s (by: %s)", super.toString(), byString);
    }
}
