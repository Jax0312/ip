import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs within a specific time period defined by start and end dates/times.
 */
public class Event extends Task {

    /** Formatter for displaying date only to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    /** Formatter for displaying date and time to the user. */
    private static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH);

    /** Start date and time of the event. */
    private final LocalDateTime from;
    /** Indicates whether a specific start time was provided. */
    private final boolean hasFromTime;
    /** End date and time of the event. */
    private final LocalDateTime to;
    /** Indicates whether a specific end time was provided. */
    private final boolean hasToTime;

    /**
     * Constructs a new Event task with start and end dates/times and time presence flags.
     *
     * @param description Description of the event.
     * @param from Start date and time of the event.
     * @param hasFromTime True if start time was specified, false if date only.
     * @param to End date and time of the event.
     * @param hasToTime True if end time was specified, false if date only.
     */
    Event(String description, LocalDateTime from, boolean hasFromTime, LocalDateTime to, boolean hasToTime) {
        super(description);
        this.from = from;
        this.hasFromTime = hasFromTime;
        this.to = to;
        this.hasToTime = hasToTime;
    }

    /**
     * Constructs a new Event task with start and end dates and times.
     *
     * @param description Description of the event.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    Event(String description, LocalDateTime from, LocalDateTime to) {
        this(description, from, true, to, true);
    }

    /**
     * Constructs a new Event task with start and end dates only.
     *
     * @param description Description of the event.
     * @param from Start date of the event.
     * @param to End date of the event.
     */
    Event(String description, LocalDate from, LocalDate to) {
        this(description, from.atStartOfDay(), false, to.atStartOfDay(), false);
    }

    /**
     * Returns the start date and time of the event.
     *
     * @return Start date and time of the event.
     */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /**
     * Returns the end date and time of the event.
     *
     * @return End date and time of the event.
     */
    public LocalDateTime getTo() {
        return this.to;
    }

    /**
     * Returns whether a start time was provided for the event.
     *
     * @return True if start time was specified, false otherwise.
     */
    public boolean hasFromTime() {
        return this.hasFromTime;
    }

    /**
     * Returns whether an end time was provided for the event.
     *
     * @return True if end time was specified, false otherwise.
     */
    public boolean hasToTime() {
        return this.hasToTime;
    }

    @Override
    public String toFileFormat() {
        String fromString = this.hasFromTime
                ? this.from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : this.from.toLocalDate().toString();
        String toString = this.hasToTime
                ? this.to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : this.to.toLocalDate().toString();
        return String.format("E | %d | %s | %s | %s", super.getDone() ? 1 : 0, super.getDescription(),
                fromString, toString);
    }

    @Override
    public String toString() {
        String fromString = this.hasFromTime
                ? this.from.format(DISPLAY_DATETIME_FORMATTER)
                : this.from.format(DISPLAY_DATE_FORMATTER);
        String toString = this.hasToTime
                ? this.to.format(DISPLAY_DATETIME_FORMATTER)
                : this.to.format(DISPLAY_DATE_FORMATTER);
        return String.format("[E]%s (from: %s to: %s)", super.toString(), fromString, toString);
    }
}
