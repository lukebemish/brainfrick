package io.github.lukebemish.brainfrick.lang.runtime;

import java.util.List;

public final class StringUtils {
    private StringUtils() {}

    @SuppressWarnings("rawtypes")
    public static String fromChars(List list) {
        if (list.get(list.size()-1) instanceof String s)
            return s;
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
