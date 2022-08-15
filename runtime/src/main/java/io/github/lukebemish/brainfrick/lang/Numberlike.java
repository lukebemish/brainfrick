package io.github.lukebemish.brainfrick.lang;

public interface Numberlike extends Zeroable {
    int getInt();

    default short getShort() {
        return (short)getInt();
    }

    default byte getByte() {
        return (byte)getInt();
    }

    default char getChar() {
        return (char)getInt();
    }

    default long getLong() {
        return getInt();
    }

    default float getFloat() {
        return getInt();
    }

    default double getDouble() {
        return getFloat();
    }

    @Override
    default boolean isZero() {
        return getInt()==0;
    }
}
