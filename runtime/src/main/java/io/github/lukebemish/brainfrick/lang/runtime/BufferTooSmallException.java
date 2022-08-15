package io.github.lukebemish.brainfrick.lang.runtime;

public class BufferTooSmallException extends RuntimeException {
    public BufferTooSmallException(String msg) {
        super(msg);
    }
}
