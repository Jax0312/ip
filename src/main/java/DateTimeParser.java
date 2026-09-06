import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses date and date-time strings into ParsedDateTime objects.
 * Supports multiple standard formats including yyyy-MM-dd HH:mm and yyyy-MM-dd.
 */
public class DateTimeParser {

    /** Supported date-time formatters for parsing. */
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    /**
     * Parses a date or date-time string into a ParsedDateTime object.
     *
     * @param input Raw date or date-time string.
     * @return Parsed date-time along with a flag indicating if time was specified.
     * @throws DaveCommandException If the string cannot be parsed using any supported format.
     */
    public static ParsedDateTime parse(String input) {
        String trimmed = input.trim();

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(trimmed, formatter);
                return new ParsedDateTime(ldt, true);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        try {
            LocalDate ld = LocalDate.parse(trimmed);
            return new ParsedDateTime(ld.atStartOfDay(), false);
        } catch (DateTimeParseException ignored) {
            // Not a date-only string either
        }

        throw new DaveCommandException(
                "NEGATIVE! Date must be in yyyy-MM-dd or yyyy-MM-dd HH:mm format (e.g., 2019-10-15 or 2005-12-22 16:00)");
    }
}
