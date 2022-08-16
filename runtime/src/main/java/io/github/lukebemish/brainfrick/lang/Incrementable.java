package io.github.lukebemish.brainfrick.lang;

/**
 * An object implementing this interface is marked as having custom incrementation logic. When using the '+' operator on
 * while pointing to an instance, {@link Incrementable#incr()} will be called.
 */
public interface Incrementable {
    /**
     * Called when the '+' operator is used while pointing towards this instance.
     * @return The object to place at the pointer. Can be null or this.
     */
    Object incr();
}
