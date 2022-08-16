package io.github.lukebemish.brainfrick.lang;

import java.lang.reflect.Array;

/**
 * Utility methods for using arrays from brainfrick. As brainfrick does not have dedicated array operations, the methods
 * in this class allow for creating, viewing, and modifying arrays.
 */
public class ArrayUtils {
    public static int[] ofSizeInt(int size) {
        return new int[size];
    }
    public static short[] ofSizeShort(int size) {
        return new short[size];
    }
    public static byte[] ofSizeByte(int size) {
        return new byte[size];
    }
    public static char[] ofSizeChar(int size) {
        return new char[size];
    }
    public static long[] ofSizeLong(int size) {
        return new long[size];
    }
    public static float[] ofSizeFloat(int size) {
        return new float[size];
    }
    public static double[] ofSizeDouble(int size) {
        return new double[size];
    }
    public static boolean[] ofSizeBoolean(int size) {
        return new boolean[size];
    }
    @SuppressWarnings("unchecked")
    public static <T> T[] ofSizeObject(int size, Class<T> clazz) {
        return (T[]) Array.newInstance(clazz, size);
    }
    public static void set(int[] array, int index, int value) {
        array[index] = value;
    }
    public static void set(short[] array, int index, short value) {
        array[index] = value;
    }
    public static void set(byte[] array, int index, byte value) {
        array[index] = value;
    }
    public static void set(char[] array, int index, char value) {
        array[index] = value;
    }
    public static void set(long[] array, int index, long value) {
        array[index] = value;
    }
    public static void set(float[] array, int index, float value) {
        array[index] = value;
    }
    public static void set(double[] array, int index, double value) {
        array[index] = value;
    }
    public static void set(boolean[] array, int index, boolean value) {
        array[index] = value;
    }
    public static <T> void set(T[] array, int index, T value) {
        array[index] = value;
    }
    public static int get(int[] array, int index) {
        return array[index];
    }
    public static short get(short[] array, int index) {
        return array[index];
    }
    public static byte get(byte[] array, int index) {
        return array[index];
    }
    public static char get(char[] array, int index) {
        return array[index];
    }
    public static long get(long[] array, int index) {
        return array[index];
    }
    public static float get(float[] array, int index) {
        return array[index];
    }
    public static double get(double[] array, int index) {
        return array[index];
    }
    public static boolean get(boolean[] array, int index) {
        return array[index];
    }
    public static <T> T get(T[] array, int index) {
        return array[index];
    }
    public static int size(int[] array) {
        return array.length;
    }
    public static int size(short[] array) {
        return array.length;
    }
    public static int size(byte[] array) {
        return array.length;
    }
    public static int size(char[] array) {
        return array.length;
    }
    public static int size(long[] array) {
        return array.length;
    }
    public static int size(float[] array) {
        return array.length;
    }
    public static int size(double[] array) {
        return array.length;
    }
    public static int size(boolean[] array) {
        return array.length;
    }
    public static <T> int size(T[] array) {
        return array.length;
    }
}
