package io.github.lukebemish.brainfrick.lang;

import java.lang.reflect.Array;

/**
 * Utility methods for using arrays from brainfrick. As brainfrick does not have dedicated array operations, the methods
 * in this class allow for creating, viewing, and modifying arrays.
 */
public class ArrayUtils {

    /**
     * Creates an empty int array.
     * @param size The size array to create.
     * @return An empty int array of the provided size.
     */
    public static int[] ofSizeInt(int size) {
        return new int[size];
    }

    /**
     * Creates an empty short array.
     * @param size The size array to create.
     * @return An empty short array of the provided size.
     */
    public static short[] ofSizeShort(int size) {
        return new short[size];
    }

    /**
     * Creates an empty byte array.
     * @param size The size array to create.
     * @return An empty byte array of the provided size.
     */
    public static byte[] ofSizeByte(int size) {
        return new byte[size];
    }

    /**
     * Creates an empty char array.
     * @param size The size array to create.
     * @return An empty char array of the provided size.
     */
    public static char[] ofSizeChar(int size) {
        return new char[size];
    }

    /**
     * Creates an empty long array.
     * @param size The size array to create.
     * @return An empty long array of the provided size.
     */
    public static long[] ofSizeLong(int size) {
        return new long[size];
    }

    /**
     * Creates an empty float array.
     * @param size The size array to create.
     * @return An empty float array of the provided size.
     */
    public static float[] ofSizeFloat(int size) {
        return new float[size];
    }

    /**
     * Creates an empty double array.
     * @param size The size array to create.
     * @return An empty double array of the provided size.
     */
    public static double[] ofSizeDouble(int size) {
        return new double[size];
    }

    /**
     * Creates an empty boolean array.
     * @param size The size array to create.
     * @return An empty boolean array of the provided size.
     */
    public static boolean[] ofSizeBoolean(int size) {
        return new boolean[size];
    }

    /**
     * Creates an empty object array.
     * @param size The size array to create.
     * @param clazz the {@link Class} of array to create.
     * @return An empty array of the provided size and class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] ofSizeObject(int size, Class<T> clazz) {
        return (T[]) Array.newInstance(clazz, size);
    }

    /**
     * Allows setting of values in int arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(int[] array, int index, int value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in short arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(short[] array, int index, short value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in byte arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(byte[] array, int index, byte value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in char arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(char[] array, int index, char value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in long arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(long[] array, int index, long value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in float arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(float[] array, int index, float value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in double arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(double[] array, int index, double value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in boolean arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static void set(boolean[] array, int index, boolean value) {
        array[index] = value;
    }

    /**
     * Allows setting of values in object arrays.
     * @param array The array to set a value in.
     * @param index The index to set a value at.
     * @param value The value to set at the provided index.
     */
    public static <T> void set(T[] array, int index, T value) {
        array[index] = value;
    }

    /**
     * Allows getting values from int arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static int get(int[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from short arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static short get(short[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from byte arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static byte get(byte[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from char arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static char get(char[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from long arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static long get(long[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from float arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static float get(float[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from double arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static double get(double[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from boolean arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static boolean get(boolean[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting values from object arrays.
     * @param array The array to get a value from.
     * @param index The index to get a value at.
     * @return The value at the provided index.
     */
    public static <T> T get(T[] array, int index) {
        return array[index];
    }

    /**
     * Allows getting the size of an int array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(int[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a short array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(short[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a byte array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(byte[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a char array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(char[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a long array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(long[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a float array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(float[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a double array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(double[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of a boolean array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static int size(boolean[] array) {
        return array.length;
    }

    /**
     * Allows getting the size of an object array.
     * @param array The array to get the size of.
     * @return The size of the array.
     */
    public static <T> int size(T[] array) {
        return array.length;
    }
}
