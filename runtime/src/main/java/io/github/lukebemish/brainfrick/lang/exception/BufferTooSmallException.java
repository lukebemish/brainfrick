package io.github.lukebemish.brainfrick.lang.exception;

/**
 * An exception thrown to indicate that the buffer of values available when brainfrick attempts to call a callable was
 * too small for the callable in question.
 */
public class BufferTooSmallException extends RuntimeException {

    /**
     * The number of values required by the {@link io.github.lukebemish.brainfrick.lang.runtime.Caller}.
     */
    public final int required;

    /**
     * The number of values present on the buffer.
     */
    public final int provided;

    /**
     * Creates a new {@link BufferTooSmallException} with the given message.
     * @param msg The detail message.
     * @param required The number of values required by the {@link io.github.lukebemish.brainfrick.lang.runtime.Caller}.
     * @param provided The number of values present on the buffer.
     */
    public BufferTooSmallException(String msg, int required, int provided) {
        super(msg);
        this.required = required;
        this.provided = provided;
    }
}
