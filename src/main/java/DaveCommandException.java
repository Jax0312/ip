/**
 * Represents an exception thrown when an invalid command or format is encountered.
 */
public class DaveCommandException extends RuntimeException {

    /**
     * Constructs a new DaveCommandException with the specified error message.
     *
     * @param message Detailed explanation of the error.
     */
    public DaveCommandException(String message) {
        super(message);
    }
}
