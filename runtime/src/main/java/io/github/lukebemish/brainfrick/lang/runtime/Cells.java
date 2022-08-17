package io.github.lukebemish.brainfrick.lang.runtime;

import io.github.lukebemish.brainfrick.lang.Decrementable;
import io.github.lukebemish.brainfrick.lang.Incrementable;
import io.github.lukebemish.brainfrick.lang.Numberlike;
import io.github.lukebemish.brainfrick.lang.Zeroable;
import io.github.lukebemish.brainfrick.lang.exception.ImproperTypeException;
import io.github.lukebemish.brainfrick.lang.exception.UnsupportedCellOperationException;

import java.util.Arrays;

/**
 * Holds the structure used to store data during a brainfrick method execution. A method will hold a reference to one of
 * these, as its first non-argument local variable, as well as an integer that points to a location within it as the
 * second such local variable. This structure is considered an implementation detail, and should not be initialized or
 * modified directly as its implementation may change.
 */
public final class Cells {
    private static final Object[] EMPTY_DATA = {};
    private static final int DEFAULT_SIZE = 5;

    private Object[] dataP;
    private Object[] dataN;
    private int sizeP;
    private int sizeN;

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

    public Object get(int idx) {
        expandToIdx(idx);
        return (idx >= 0) ? dataP[idx] : dataN[1-idx];
    }

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

    public static int getIntValue(Object obj) {
        if (obj instanceof Number number)
            return number.intValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getInt();
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not integer-like.",obj.getClass(),int.class);
    }

    public int asInt(int idx) {
        return getIntValue(get(idx));
    }

    public boolean isZero(int idx) {
        Object obj = get(idx);
        if (obj==null)
            return true;
        else if (isStrictlyInteger(obj))
            return getIntValue(obj)==0;
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

    public void incr(int idx, int amount) {
        set(idx,incr(get(idx),amount));
    }

    public static Object incr(Object obj, int amount) {
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

    public void decr(int idx, int amount) {
        set(idx,decr(get(idx),amount));
    }

    public static Object decr(Object obj, int amount) {
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
    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;
    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
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
