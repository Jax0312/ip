package dave.parser;

import java.time.temporal.ChronoUnit;

import dave.command.Command;
import dave.exception.DaveCommandException;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Todo;

/**
 * Deals with making sense of user commands and parsing input arguments.
 */
public class Parser {

    /** Delimiter separating deadline task description from due date-time. */
    private static final String DEADLINE_DELIMITER = " /by ";
    /** Delimiter separating event task description from start date-time. */
    private static final String EVENT_FROM_DELIMITER = " /from ";
    /** Delimiter separating event start date-time from end date-time. */
    private static final String EVENT_TO_DELIMITER = " /to ";
    /** Delimiter prefix for snooze target date-time. */
    private static final String SNOOZE_TO_PREFIX = "/to ";
    /** Delimiter prefix for snooze alternative deadline target date-time. */
    private static final String SNOOZE_BY_PREFIX = "/by ";
    /** Delimiter prefix for snooze event start boundary. */
    private static final String SNOOZE_FROM_PREFIX = "/from ";

    /**
     * Parses the command type from the full user input string.
     *
     * @param fullCommand Full input string entered by the user.
     * @return Corresponding Command enum value.
     */
    public static Command parseCommand(String fullCommand) {
        String[] parts = fullCommand.trim().split("\\s+", 2);
        return Command.from(parts[0]);
    }

    /**
     * Extracts the arguments portion following the command keyword.
     *
     * @param fullCommand Full input string entered by the user.
     * @return Argument string following the command keyword, or an empty string if none.
     */
    public static String parseArguments(String fullCommand) {
        String[] parts = fullCommand.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Parses a Todo task from the provided arguments string.
     *
     * @param arguments Argument string containing the task description.
     * @return Newly created Todo task.
     * @throws DaveCommandException If the description is empty.
     */
    public static Todo parseTodo(String arguments) {
        if (arguments.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a todo cannot be empty");
        }
        return new Todo(arguments);
    }

    /**
     * Parses a Deadline task from the provided arguments string.
     *
     * @param arguments Argument string containing description and '/by' date/time.
     * @return Newly created Deadline task.
     * @throws DaveCommandException If delimiters are missing, description is empty, or date format is invalid.
     */
    public static Deadline parseDeadline(String arguments) {
        String[] attributes = arguments.split(DEADLINE_DELIMITER);
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! A deadline requires /by [time]");
        }
        if (attributes[0].trim().isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of a deadline cannot be empty");
        }

        ParsedDateTime parsed = DateTimeParser.parse(attributes[1]);
        return new Deadline(attributes[0].trim(), parsed.getDateTime(), parsed.hasTime());
    }

    /**
     * Parses an Event task from the provided arguments string.
     *
     * @param arguments Argument string containing description, '/from' and '/to' dates/times.
     * @return Newly created Event task.
     * @throws DaveCommandException If delimiters are missing, description is empty, or date format is invalid.
     */
    public static Event parseEvent(String arguments) {
        String[] attributes = arguments.split(EVENT_FROM_DELIMITER);
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }
        if (attributes[0].trim().isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of an event cannot be empty");
        }

        String[] fromTo = attributes[1].split(EVENT_TO_DELIMITER);
        if (fromTo.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }

        ParsedDateTime fromParsed = DateTimeParser.parse(fromTo[0]);
        ParsedDateTime toParsed = DateTimeParser.parse(fromTo[1]);

        return new Event(attributes[0].trim(),
                fromParsed.getDateTime(), fromParsed.hasTime(),
                toParsed.getDateTime(), toParsed.hasTime());
    }

    /**
     * Parses a 1-based task index string into a 0-based integer index.
     *
     * @param arguments Argument string containing the 1-based task number.
     * @return 0-based integer index.
     * @throws DaveCommandException If the argument is not a valid integer.
     */
    public static int parseIndex(String arguments) {
        try {
            int itemNumber = Integer.parseInt(arguments);
            return itemNumber - 1;
        } catch (NumberFormatException e) {
            throw new DaveCommandException("Wrong number!");
        }
    }

    /**
     * Parses the search keyword from the find command arguments string.
     *
     * @param arguments Argument string containing the keyword.
     * @return Search keyword.
     * @throws DaveCommandException If the keyword is empty.
     */
    public static String parseFind(String arguments) {
        if (arguments.trim().isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The search keyword cannot be empty");
        }
        return arguments.trim();
    }

    /**
     * Parses a snooze command argument string into a SnoozeRequest object.
     *
     * @param arguments Argument string containing the task number and optional snooze parameters.
     * @return Parsed SnoozeRequest containing target task index and snooze details.
     * @throws DaveCommandException If the arguments string is invalid or cannot be parsed.
     */
    public static SnoozeRequest parseSnooze(String arguments) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            throw new DaveCommandException("Wrong number!");
        }

        String[] parts = trimmed.split("\\s+", 2);
        int index = parseIndex(parts[0]);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return new SnoozeRequest(index, 1, ChronoUnit.DAYS);
        }

        String spec = parts[1].trim();

        if (spec.contains(SNOOZE_FROM_PREFIX)) {
            return parseEventSnooze(index, spec);
        }

        if (spec.startsWith(SNOOZE_TO_PREFIX) || spec.startsWith(SNOOZE_BY_PREFIX)) {
            return parseTargetSnooze(index, spec);
        }

        return parseRelativeSnooze(index, spec);
    }

    /**
     * Parses event start and end boundaries from a snooze specification.
     *
     * @param index 0-based task index.
     * @param spec Specification string containing /from and /to delimiters.
     * @return SnoozeRequest configured for event rescheduling.
     * @throws DaveCommandException If delimiters are missing or boundaries cannot be parsed.
     */
    private static SnoozeRequest parseEventSnooze(int index, String spec) {
        String toPrefix = "/to ";
        if (!spec.contains(toPrefix)) {
            throw new DaveCommandException("NEGATIVE! Rescheduling an event requires /from [time] and /to [time]");
        }

        int fromIndex = spec.indexOf(SNOOZE_FROM_PREFIX);
        int toIndex = spec.indexOf(toPrefix, fromIndex + SNOOZE_FROM_PREFIX.length());
        if (toIndex == -1) {
            throw new DaveCommandException("NEGATIVE! Rescheduling an event requires /from [time] and /to [time]");
        }

        String fromStr = spec.substring(fromIndex + SNOOZE_FROM_PREFIX.length(), toIndex).trim();
        String toStr = spec.substring(toIndex + toPrefix.length()).trim();

        if (fromStr.isEmpty() || toStr.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! Rescheduling an event requires /from [time] and /to [time]");
        }

        ParsedDateTime fromParsed = DateTimeParser.parse(fromStr);
        ParsedDateTime toParsed = DateTimeParser.parse(toStr);
        return new SnoozeRequest(index, fromParsed, toParsed);
    }

    /**
     * Parses a target date-time from a /to or /by snooze specification.
     *
     * @param index 0-based task index.
     * @param spec Specification string starting with /to or /by.
     * @return SnoozeRequest configured for deadline rescheduling.
     * @throws DaveCommandException If the date-time string is missing or invalid.
     */
    private static SnoozeRequest parseTargetSnooze(int index, String spec) {
        String dateString;
        if (spec.startsWith(SNOOZE_TO_PREFIX)) {
            dateString = spec.substring(SNOOZE_TO_PREFIX.length()).trim();
        } else {
            dateString = spec.substring(SNOOZE_BY_PREFIX.length()).trim();
        }

        if (dateString.isEmpty()) {
            throw new DaveCommandException("NEGATIVE! A target date-time must follow /to");
        }

        ParsedDateTime targetParsed = DateTimeParser.parse(dateString);
        return new SnoozeRequest(index, targetParsed);
    }

    /**
     * Parses a relative duration from a snooze specification.
     *
     * @param index 0-based task index.
     * @param spec Specification string containing amount and time unit.
     * @return SnoozeRequest configured for relative duration adjustment.
     * @throws DaveCommandException If the duration is negative, non-numeric, or has an invalid unit.
     */
    private static SnoozeRequest parseRelativeSnooze(int index, String spec) {
        String[] durationParts = spec.split("\\s+", 2);
        if (durationParts.length < 2) {
            throw new DaveCommandException("NEGATIVE! Specify a valid duration (e.g., '3 days') "
                    + "or target date-time (e.g., '/to 2026-10-25').");
        }

        try {
            long amount = Long.parseLong(durationParts[0]);
            if (amount <= 0) {
                throw new DaveCommandException("NEGATIVE! Snooze duration amount must be a positive number");
            }
            ChronoUnit unit = parseChronoUnit(durationParts[1]);
            return new SnoozeRequest(index, amount, unit);
        } catch (NumberFormatException e) {
            throw new DaveCommandException("NEGATIVE! Specify a valid duration (e.g., '3 days') "
                    + "or target date-time (e.g., '/to 2026-10-25').");
        }
    }

    /**
     * Parses a text string into a supported ChronoUnit.
     *
     * @param unitText Text representing a time unit.
     * @return Corresponding ChronoUnit.
     * @throws DaveCommandException If the unit is not recognized.
     */
    private static ChronoUnit parseChronoUnit(String unitText) {
        String normalized = unitText.trim().toLowerCase();
        switch (normalized) {
            case "day":
            case "days":
            case "d":
                return ChronoUnit.DAYS;
            case "hour":
            case "hours":
            case "h":
            case "hr":
            case "hrs":
                return ChronoUnit.HOURS;
            case "week":
            case "weeks":
            case "w":
                return ChronoUnit.WEEKS;
            case "minute":
            case "minutes":
            case "m":
            case "min":
            case "mins":
                return ChronoUnit.MINUTES;
            default:
                throw new DaveCommandException("NEGATIVE! Unrecognized time unit: " + unitText
                        + ". Use days, hours, weeks, or minutes.");
        }
    }
}
