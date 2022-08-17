package io.github.lukebemish.brainfrick.lang.runtime;

import io.github.lukebemish.brainfrick.lang.Decrementable;
import io.github.lukebemish.brainfrick.lang.Incrementable;
import io.github.lukebemish.brainfrick.lang.Zeroable;
import io.github.lukebemish.brainfrick.lang.exception.UnsupportedCellOperationException;

import java.util.Arrays;

/**
 * Holds the structure used to store data during a brainfrick method execution. A method will hold a reference to one of
 * these, as its first non-argument local variable, as well as an integer that points to a location within it as the
 * second such local variable.
 */
public final class Cells {
    private static final Object[] EMPTY_DATA = {};
    private static final int DEFAULT_SIZE = 5;

    private Object[] dataP;
    private Object[] dataN;
    private int sizeP;
    private int sizeN;

    /**
     * Creates a new cell structure. Should be called once at the beginning of each method or constructor in compiled
     * brainfrick. Each location in the structure is initialized as null.
     */
    public Cells() {
        this.dataP = EMPTY_DATA;
        this.dataN = EMPTY_DATA;
        this.sizeP = 0;
        this.sizeN = 0;
    }

    private void expandToIdx(int idx) {
        if (idx >= sizeP) {
            dataP = grow(dataP, sizeP + 1);
            sizeP = dataP.length;
        } else if ((1-idx) >= sizeN) {
            dataN = grow(dataN, sizeN + 1);
            sizeN = dataN.length;
        }
    }

    private Object[] grow(Object[] data, int minCapacity) {
        int oldCapacity = data.length;
        if (oldCapacity > 0 || data != EMPTY_DATA) {
            int newCapacity = newLength(oldCapacity,
                    minCapacity - oldCapacity, /* minimum growth */
                    oldCapacity >> 1           /* preferred growth */);
            return Arrays.copyOf(data, newCapacity);
        } else
            return new Object[Math.max(DEFAULT_SIZE, minCapacity)];
    }

    /**
     * Gets the object at a given index in the structure; used during the '.' operation.
     * @param idx The index of the element to return. Can be a positive or negative value.
     * @return The object at that index of the structure, or null if no element is stored there.
     */
    public Object get(int idx) {
        expandToIdx(idx);
        return (idx >= 0) ? dataP[idx] : dataN[1-idx];
    }

    /**
     * Replaces the object at a given index in the structure.
     * @param idx The index of the element to replace. Can be a positive or negative value.
     * @param obj The object to place at that index in the structure, or null to clear that index.
     */
    public void set(int idx, Object obj) {
        expandToIdx(idx);
        if (idx >= 0)
            dataP[idx] = obj;
        else
            dataN[1-idx] = obj;
    }

    private boolean isStrictlyInteger(Object obj) {
        return obj instanceof Integer ||
                obj instanceof Short ||
                obj instanceof Byte ||
                obj instanceof Character ||
                obj instanceof Boolean;
    }

    /**
     * Gets an integer representation of the object at a given index, to be used during the ':' or ',' operations.
     * @param idx The index to get an integer representation of.
     * @return An integer representation of the element at the given index.
     * @exception io.github.lukebemish.brainfrick.lang.exception.ImproperTypeException if the value cannot be unboxed or
     * converted to an int
     * @see io.github.lukebemish.brainfrick.lang.Numberlike
     */
    public int asInt(int idx) {
        return InvocationUtils.asI(get(idx));
    }

    /**
     * Determines whether the element at a given index is zero-like. Used during the '[' and ']' operations.
     * @param idx The index of the object to be checked.
     * @return true if the object is zero-like, false otherwise.
     * @see Zeroable
     */
    public boolean isZero(int idx) {
        Object obj = get(idx);
        if (obj==null)
            return true;
        else if (isStrictlyInteger(obj))
            return InvocationUtils.asI(obj)==0;
        else if (obj instanceof Float f)
            return f==0;
        else if (obj instanceof Double d)
            return d==0;
        else if (obj instanceof Long l)
            return l==0;
        else if (obj instanceof Boolean b)
            return Boolean.FALSE.equals(b);
        else if (obj instanceof Zeroable z)
            return z.isZero();
        return false;
    }

    /**
     * Increments the object at the given cell the provided number of times. Used during the '+' operation.
     * @param idx The index of the object to be incremented.
     * @param amount The number of times to increment the object.
     * @exception UnsupportedCellOperationException if the object cannot be incremented.
     * @see Incrementable
     */
    public void incr(int idx, int amount) {
        set(idx,incr(get(idx),amount));
    }

    private static Object incr(Object obj, int amount) {
        Object toSet;
        if (obj == null)
            toSet = amount;
        else if (obj instanceof Integer i)
            toSet = i+amount;
        else if (obj instanceof Short s)
            toSet = s+amount;
        else if (obj instanceof Byte b)
            toSet = b+amount;
        else if (obj instanceof Character c)
            toSet = (char)(c+amount);
        else if (obj instanceof Long l)
            toSet = l+amount;
        else if (obj instanceof Float f)
            toSet = f+amount;
        else if (obj instanceof Double d)
            toSet = d+amount;
        else if (obj instanceof Boolean b)
            toSet = Boolean.FALSE.equals(b)?(amount==1?Boolean.TRUE:amount):amount+1;
        else if (obj instanceof Incrementable incrementable) {
            toSet = incrementable.incr();
            if (amount>1)
                toSet = incr(toSet, amount-1);
        } else
            throw new UnsupportedCellOperationException(String.format("Objects of type %s do not support incrementing",obj.getClass()));
        return toSet;
    }

    /**
     * Decrements the object at the given cell the provided number of times. Used during the '-' operation.
     * @param idx The index of the object to be decremented.
     * @param amount The number of times to decrement the object.
     * @exception UnsupportedCellOperationException if the object cannot be decremented.
     * @see Decrementable
     */
    public void decr(int idx, int amount) {
        set(idx,decr(get(idx),amount));
    }

    private static Object decr(Object obj, int amount) {
        Object toSet;
        if (obj == null)
            toSet = -amount;
        else if (obj instanceof Integer i)
            toSet = i-amount;
        else if (obj instanceof Short s)
            toSet = s-amount;
        else if (obj instanceof Byte b)
            toSet = b-amount;
        else if (obj instanceof Character c)
            toSet = (char)(c-amount);
        else if (obj instanceof Long l)
            toSet = l-amount;
        else if (obj instanceof Float f)
            toSet = f-amount;
        else if (obj instanceof Double d)
            toSet = d-amount;
        else if (obj instanceof Boolean b)
            toSet = Boolean.TRUE.equals(b)?(amount==1?Boolean.FALSE:1-amount):-amount;
        else if (obj instanceof Decrementable decrementable) {
            toSet = decrementable.decr();
            if (amount>1)
                toSet = decr(toSet, amount-1);
        } else
            throw new UnsupportedCellOperationException(String.format("Objects of type %s do not support decrementing",obj.getClass()));
        return toSet;
    }

    // Internal array utils. Similar to ArraysSupport
    private static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;
    private static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        return (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) ?
            prefLength : hugeLength(oldLength, minGrowth);
    }
    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0)
            throw new OutOfMemoryError(
                    "Required array length " + oldLength + " + " + minGrowth + " is too large");
        else
            return Math.max(minLength, SOFT_MAX_ARRAY_LENGTH);
    }
}
