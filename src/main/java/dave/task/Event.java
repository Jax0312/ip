package dave.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that occurs within a specific time interval.
 */
public class Event extends Task {

    /** Formatter for displaying date-only event boundaries. */
    private static final DateTimeFormatter DATE_ONLY_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    /** Formatter for displaying date and time event boundaries. */
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");
    /** Formatter for serializing date-time event boundaries to persistent storage. */
    private static final DateTimeFormatter DATE_TIME_FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Start date and optional time of the event. */
    private final LocalDateTime from;
    /** Indicates whether the start time of day was explicitly specified. */
    private final boolean hasFromTime;
    /** End date and optional time of the event. */
    private final LocalDateTime to;
    /** Indicates whether the end time of day was explicitly specified. */
    private final boolean hasToTime;

    /**
     * Constructs a new Event task with full date-time and time presence information.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param hasFromTime True if start time of day was specified, false if date only.
     * @param to End date and time.
     * @param hasToTime True if end time of day was specified, false if date only.
     */
    public Event(String description, LocalDateTime from, boolean hasFromTime, LocalDateTime to, boolean hasToTime) {
        super(description);
        this.from = from;
        this.hasFromTime = hasFromTime;
        this.to = to;
        this.hasToTime = hasToTime;
    }

    /**
     * Constructs a new Event task with start and end date-times with times of day.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param to End date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, true, to, true);
    }

    /**
     * Constructs a new Event task with date-only start and end bounds.
     *
     * @param description Description of the event.
     * @param from Start date.
     * @param to End date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        this(description, from.atStartOfDay(), false, to.atStartOfDay(), false);
    }

    @Override
    public String toFileFormat() {
        String fromString = this.hasFromTime
                ? this.from.format(DATE_TIME_FILE_FORMATTER)
                : this.from.toLocalDate().toString();
        String toString = this.hasToTime
                ? this.to.format(DATE_TIME_FILE_FORMATTER)
                : this.to.toLocalDate().toString();
        return String.format("E | %d | %s | %s | %s",
                this.getDone() ? 1 : 0, this.getDescription(), fromString, toString);
    }

    @Override
    public String toString() {
        String fromString = this.hasFromTime
                ? this.from.format(DATE_TIME_OUTPUT_FORMATTER)
                : this.from.format(DATE_ONLY_OUTPUT_FORMATTER);
        String toString = this.hasToTime
                ? this.to.format(DATE_TIME_OUTPUT_FORMATTER)
                : this.to.format(DATE_ONLY_OUTPUT_FORMATTER);
        return String.format("[E]%s (from: %s to: %s)", super.toString(), fromString, toString);
    }
}
