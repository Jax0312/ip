/**
 * Deals with making sense of user commands and parsing input arguments.
 */
public class Parser {

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
        String[] attributes = arguments.split(" /by ");
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
        String[] attributes = arguments.split(" /from ");
        if (attributes.length < 2) {
            throw new DaveCommandException("NEGATIVE! An event requires /from [time] and /to [time]");
        }
        if (attributes[0].trim().isEmpty()) {
            throw new DaveCommandException("NEGATIVE! The description of an event cannot be empty");
        }

        String[] fromTo = attributes[1].split(" /to ");
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
}
