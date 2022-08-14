package io.github.lukebemish.brainfrick.lang.runtime;

import io.github.lukebemish.brainfrick.lang.BufferTooSmallException;
import io.github.lukebemish.brainfrick.lang.ImproperTypeException;

public final class InvocationUtils {
    private InvocationUtils() {}

    static void checkEnough(int size, int needed) {
        if (size < needed)
            throw new BufferTooSmallException("Buffer contains "+size+" elements; "+needed+" needed.");
    }

    static int asI(Object object) {
        return Cells.getIntValue(object);
    }

    static short asS(Object obj) {
        if (obj instanceof Number number)
            return number.shortValue();
        else if (obj instanceof Character character)
            return (short)(char)character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?(short)1:0;
        else if (obj == null)
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not short-like.");
    }

    static byte asB(Object obj) {
        if (obj instanceof Number number)
            return number.byteValue();
        else if (obj instanceof Character character)
            return (byte)(char)character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?(byte)1:0;
        else if (obj == null)
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not byte-like.");
    }

    static char asC(Object obj) {
        if (obj instanceof Number number)
            return (char)number.shortValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?(char)1:0;
        else if (obj == null)
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not char-like.");
    }

    static long asJ(Object obj) {
        if (obj instanceof Number number)
            return number.longValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not long-like.");
    }

    static float asF(Object obj) {
        if (obj instanceof Number number)
            return number.floatValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not float-like.");
    }

    static double asD(Object obj) {
        if (obj instanceof Number number)
            return number.doubleValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not double-like.");
    }

    static boolean asZ(Object obj) {
        if (obj instanceof Boolean b)
            return b;
        if (obj instanceof Double d)
            return d!=0;
        else if (obj instanceof Float f)
            return f!=0;
        try {
            return asI(obj) != 0;
        } catch (ImproperTypeException ignored) {
            return true;
        }
    }

    Object fromI(int i) {
        return i;
    }
    Object fromS(short s) {
        return s;
    }
    Object fromB(byte b) {
        return b;
    }
    Object fromC(char c) {
        return c;
    }
    Object fromJ(long j) {
        return j;
    }
    Object fromF(float f) {
        return f;
    }
    Object fromD(double d) {
        return d;
    }
    Object fromZ(boolean z) {
        return z;
    }
}
