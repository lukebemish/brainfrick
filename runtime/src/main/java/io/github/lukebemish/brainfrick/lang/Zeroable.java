package io.github.lukebemish.brainfrick.lang;

/**
 * An object implementing this interface is marked as having custom logic to determine whether it is zero-like for the
 * purpose of the '[' or ']' operators. When these operators are used while pointing towards an instance,
 * {@link Zeroable#isZero()} is called. Zeroable objects can also be unboxed into booleans.
 */
public interface Zeroable {
    /**
     * Called when the '[' or ']' operators are used while pointing towards the instance, or when the instance is
     * unboxed into a boolean value.
     * @return true if the object should be considered zero-like; false otherwise.
     */
    boolean isZero();
}
