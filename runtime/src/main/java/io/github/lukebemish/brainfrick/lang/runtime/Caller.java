package io.github.lukebemish.brainfrick.lang.runtime;

import java.util.List;

/**
 * Represents any sort of callable object. This could be a method, constructor, field getter or setter, or class getter.
 */
public interface Caller {

    /**
     * Call the callable object, pulling the necessary parameters from the provided {@link List}
     * @param args List of arguments to pull parameters from as needed.
     * @return The returned value of the callable, or null if it is a method returning void.
     * @exception io.github.lukebemish.brainfrick.lang.exception.BufferTooSmallException if the argument list does not
     * contain enough arguments for this callable.
     */
    @SuppressWarnings("rawtypes")
    Object call(List args);
}
