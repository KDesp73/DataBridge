package io.github.kdesp73.databridge.exceptions;

/**
 * {@code IncorrectOperatorException} is a custom exception that extends {@link RuntimeException}.
 * <p>
 * This exception is thrown when an operation or query encounters an invalid or unsupported operator.
 * It is a {@link RuntimeException}, meaning it is unchecked and does not need to be declared or caught
 * in a method signature.
 * </p>
 *
 * @author KDesp73
 */
public class IncorrectOperatorException extends RuntimeException {

    /**
     * Constructs a new {@code IncorrectOperatorException} with the specified error message.
     *
     * @param err the detail message explaining the reason for the exception.
     */
    public IncorrectOperatorException(String err) {
        super(err);
    }
}
