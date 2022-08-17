package io.github.lukebemish.brainfrick.lang;

/**
 * An object implementing this interface is marked as being number-like for the purpose of indexing arguments, using the
 * ',' operator, or indexing callers, using the ':' operator. When either operator is used while pointing towards
 * an instance, {@link Numberlike#getInt()} is called. Additionally, instances can be unboxed to any primitive type.
 */
public interface Numberlike extends Zeroable {
    /**
     * Gets an integer representation of the object.
     * @return an integer representation of the object to be used for indexing or unboxing.
     */
    int getInt();

    /**
     * Gets a short representation of the object.
     * @return a short representation of the object to be used for unboxing.
     */
    default short getShort() {
        return (short)getInt();
    }

    /**
     * Gets a byte representation of the object.
     * @return a byte representation of the object to be used for unboxing.
     */
    default byte getByte() {
        return (byte)getInt();
    }

    /**
     * Gets a char representation of the object.
     * @return a char representation of the object to be used for unboxing.
     */
    default char getChar() {
        return (char)getInt();
    }

    /**
     * Gets a long representation of the object.
     * @return a long representation of the object to be used for unboxing.
     */
    default long getLong() {
        return getInt();
    }

    /**
     * Gets a float representation of the object.
     * @return a float representation of the object to be used for unboxing.
     */
    default float getFloat() {
        return getInt();
    }

    /**
     * Gets a double representation of the object.
     * @return a double representation of the object to be used for unboxing.
     */
    default double getDouble() {
        return getFloat();
    }

    @Override
    default boolean isZero() {
        return getInt()==0;
    }
}
