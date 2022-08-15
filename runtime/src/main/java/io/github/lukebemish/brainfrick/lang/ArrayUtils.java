package io.github.lukebemish.brainfrick.lang;

import java.lang.reflect.Array;

public class ArrayUtils {
    int[] ofSizeInt(int size) {
        return new int[size];
    }
    short[] ofSizeShort(int size) {
        return new short[size];
    }
    byte[] ofSizeByte(int size) {
        return new byte[size];
    }
    char[] ofSizeChar(int size) {
        return new char[size];
    }
    long[] ofSizeLong(int size) {
        return new long[size];
    }
    float[] ofSizeFloat(int size) {
        return new float[size];
    }
    double[] ofSizeDouble(int size) {
        return new double[size];
    }
    boolean[] ofSizeBoolean(int size) {
        return new boolean[size];
    }
    @SuppressWarnings("unchecked")
    <T> T[] ofSizeObject(int size, Class<T> clazz) {
        return (T[]) Array.newInstance(clazz, size);
    }
    void set(int[] array, int index, int value) {
        array[index] = value;
    }
    void set(short[] array, int index, short value) {
        array[index] = value;
    }
    void set(byte[] array, int index, byte value) {
        array[index] = value;
    }
    void set(char[] array, int index, char value) {
        array[index] = value;
    }
    void set(long[] array, int index, long value) {
        array[index] = value;
    }
    void set(float[] array, int index, float value) {
        array[index] = value;
    }
    void set(double[] array, int index, double value) {
        array[index] = value;
    }
    void set(boolean[] array, int index, boolean value) {
        array[index] = value;
    }
    <T> void set(T[] array, int index, T value) {
        array[index] = value;
    }
    int size(int[] array) {
        return array.length;
    }
    int size(short[] array) {
        return array.length;
    }
    int size(byte[] array) {
        return array.length;
    }
    int size(char[] array) {
        return array.length;
    }
    int size(long[] array) {
        return array.length;
    }
    int size(float[] array) {
        return array.length;
    }
    int size(double[] array) {
        return array.length;
    }
    int size(boolean[] array) {
        return array.length;
    }
    <T> int size(T[] array) {
        return array.length;
    }
}
