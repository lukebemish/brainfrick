package io.github.lukebemish.brainfrick.lang.runtime;

import java.util.List;

public interface Caller {
    @SuppressWarnings("rawtypes")
    Object call(List args);
}
