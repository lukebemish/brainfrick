package io.github.lukebemish.brainfrick.lang;

public class BufferTooSmallException extends RuntimeException {
    public BufferTooSmallException(String msg) {
        super(msg);
    }
}
