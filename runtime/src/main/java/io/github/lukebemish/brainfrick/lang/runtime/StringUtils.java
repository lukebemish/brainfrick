package io.github.lukebemish.brainfrick.lang.runtime;

import io.github.lukebemish.brainfrick.lang.exception.BufferTooSmallException;

import java.util.List;

/**
 * Contains utility methods used by brainfrick to handle strings.
 */
public final class StringUtils {
    private StringUtils() {}

    /**
     * Creates a string from a buffer. If the buffer contains a string at the top, that string should be returned.
     * Otherwise, each element of the buffer should be converted to a character and the output string assembled from
     * those. Called at the end of brainfrick methods returning a {@link String}
     * @param list The buffer to assemble a string from.
     * @return A string to be returned by the brainfrick method.
     * @exception BufferTooSmallException if the buffer does not contain at least one item.
     * @exception io.github.lukebemish.brainfrick.lang.exception.ImproperTypeException if a member of the buffer cannot
     * be converted to a character.
     */
    @SuppressWarnings("rawtypes")
    public static String fromChars(List list) {
        if (list.isEmpty())
            throw new BufferTooSmallException("Buffer contains 0 elements; 1 needed.",1,0);
        Object first = list.get(list.size()-1);
        if (first instanceof String s)
            return s;
        else if (first==null)
            return null;
        else {
            char[] chars = new char[list.size()];
            int counter = 0;
            for (Object o : list) {
                chars[counter] = InvocationUtils.asC(o);
                counter++;
            }
            return String.valueOf(chars);
        }
    }

    /**
     * Gets a value to return from the top of a buffer. Called at the end of brainfrick methods whose return tyoes do
     * not have special cases.
     * @param list The buffer to assemble a string from.
     * @return An object to be returned, after type conversion, by the brainfrick method.
     * @see StringUtils#fromChars(List)
     * @exception BufferTooSmallException if the buffer does not contain at least one item.
     */
    @SuppressWarnings("rawtypes")
    public static Object fromBuffer(List list) {
        if (list.isEmpty())
            throw new BufferTooSmallException("Buffer contains 0 elements; 1 needed.",1,0);
        return list.get(list.size()-1);
    }
}
