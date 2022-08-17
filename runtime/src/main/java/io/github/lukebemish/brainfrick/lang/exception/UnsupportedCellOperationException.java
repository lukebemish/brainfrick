package io.github.lukebemish.brainfrick.lang.exception;

/**
 * An exception thrown to indicate that a given brainfrick operation (such as incrementation or decrementing) cannot be
 * applied to the given object.
 */
public class UnsupportedCellOperationException extends UnsupportedOperationException {
    /**
     * Creates a new {@link UnsupportedCellOperationException} with the given message
     * @param msg The detail message.
     */
    public UnsupportedCellOperationException(String msg) {
        super(msg);
    }
}
