package io.github.lukebemish.brainfrick.lang.runtime;

import java.util.List;

/**
 * Contains utility methods used by brainfrick to handle strings. Should be considered an implementation detail, and not
 * referenced directly.
 */
public final class StringUtils {
    private StringUtils() {}

    @SuppressWarnings("rawtypes")
    public static String fromChars(List list) {
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
}
