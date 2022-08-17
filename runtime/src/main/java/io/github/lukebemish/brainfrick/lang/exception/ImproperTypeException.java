package io.github.lukebemish.brainfrick.lang.exception;

/**
 * An exception thrown to indicate that a type cannot be unboxed into another type. Differs from a
 * {@link ClassCastException} in that no cast was attempted; there was just no known path from existing to target type.
 */
public class ImproperTypeException extends RuntimeException {
    /**
     * The original type of the object.
     */
    public final Class<?> existingType;

    /**
     * The type that the object could not be unboxed to or turned into.
     */
    public final Class<?> targetType;

    /**
     * Creates a new {@link ImproperTypeException} with the given message and initial and target types.
     * @param msg The detail message.
     * @param existingType The original type of the object.
     * @param targetType The type the object could not be turned into.
     */

    public ImproperTypeException(String msg, Class<?> existingType, Class<?> targetType) {
        super(msg);
        this.existingType = existingType;
        this.targetType = targetType;
    }
}
