package io.github.lukebemish.brainfrick.lang;

/**
 * An object implementing this interface is marked as having custom decrementation logic. When using the '-' operator on
 * while pointing to an instance, {@link Decrementable#decr()} will be called.
 */
public interface Decrementable {
    /**
     * Called when the '-' operator is used while pointing towards this instance.
     * @return The object to place at the pointer. Can be null or this.
     */
    Object decr();
}
