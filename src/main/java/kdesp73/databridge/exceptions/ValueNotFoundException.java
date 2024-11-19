package kdesp73.databridge.exceptions;

/**
 * {@code ValueNotFoundException} is a custom exception that extends {@link RuntimeException}.
 * <p>
 * This exception is thrown when a specific value is not found during an operation, such as when querying
 * a database or searching for a particular item that does not exist.
 * It is a {@link RuntimeException}, meaning it is unchecked and does not need to be declared or caught
 * in a method signature.
 * </p>
 *
 * @author KDesp73
 */
public class ValueNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code ValueNotFoundException} with the specified error message.
     *
     * @param err the detail message explaining the reason for the exception.
     */
    public ValueNotFoundException(String err) {
        super(err);
    }
}
