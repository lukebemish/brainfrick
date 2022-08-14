package io.github.lukebemish.brainfrick.runtime;

import java.util.Arrays;

public class Cells {
    private static final Object[] EMPTY_DATA = {};
    private static final int DEFAULT_SIZE = 5;

    protected Object[] dataP;
    protected Object[] dataN;
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

    private int getIntValue(Object obj) {
        if (obj instanceof Number number)
            return number.intValue();
        if (obj instanceof Character character)
            return character;
        return 0;
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
        return false;
    }

    public void incr(int idx) {
        Object obj = get(idx);
        Object toSet;
        if (obj == null)
            toSet = 1;
        else if (obj instanceof Integer i)
            toSet = i+1;
        else if (obj instanceof Short s)
            toSet = s+1;
        else if (obj instanceof Byte b)
            toSet = b+1;
        else if (obj instanceof Character c)
            toSet = (char)(c+1);
        else if (obj instanceof Long l)
            toSet = l+1;
        else if (obj instanceof Float f)
            toSet = f+1;
        else if (obj instanceof Double d)
            toSet = d+1;
        else if (obj instanceof Incrementable incrementable)
            toSet = incrementable.incr();
        else
            throw new UnsupportedOperationException(String.format("Objects of type %s do not support incrementing",obj.getClass()));
        set(idx,toSet);
    }

    public void decr(int idx) {
        Object obj = get(idx);
        Object toSet;
        if (obj == null)
            toSet = -1;
        else if (obj instanceof Integer i)
            toSet = i-1;
        else if (obj instanceof Short s)
            toSet = s-1;
        else if (obj instanceof Byte b)
            toSet = b-1;
        else if (obj instanceof Character c)
            toSet = (char)(c-1);
        else if (obj instanceof Long l)
            toSet = l-1;
        else if (obj instanceof Float f)
            toSet = f-1;
        else if (obj instanceof Double d)
            toSet = d-1;
        else if (obj instanceof Incrementable incrementable)
            toSet = incrementable.decr();
        else
            throw new UnsupportedOperationException(String.format("Objects of type %s do not support decrementing",obj.getClass()));
        set(idx,toSet);
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
