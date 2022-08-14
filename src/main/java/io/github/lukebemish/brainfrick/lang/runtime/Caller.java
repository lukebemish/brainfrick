package io.github.lukebemish.brainfrick.lang.runtime;

import java.util.List;

public interface Caller {
    Object call(List args);
}
