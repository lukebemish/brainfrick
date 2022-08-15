package io.github.lukebemish.brainfrick.lang.runtime;

import io.github.lukebemish.brainfrick.lang.Numberlike;
import io.github.lukebemish.brainfrick.lang.Zeroable;

public final class InvocationUtils {
    private InvocationUtils() {}

    public static void checkEnough(int size, int needed) {
        if (size < needed)
            throw new BufferTooSmallException("Buffer contains "+size+" elements; "+needed+" needed.");
    }

    public static int asI(Object object) {
        return Cells.getIntValue(object);
    }

    public static short asS(Object obj) {
        if (obj instanceof Number number)
            return number.shortValue();
        else if (obj instanceof Character character)
            return (short)(char)character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?(short)1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getShort();
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not short-like.");
    }

    public static byte asB(Object obj) {
        if (obj instanceof Number number)
            return number.byteValue();
        else if (obj instanceof Character character)
            return (byte)(char)character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?(byte)1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getByte();
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not byte-like.");
    }

    public static char asC(Object obj) {
        if (obj instanceof Number number)
            return (char)number.shortValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?(char)1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getChar();
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not char-like.");
    }

    public static long asJ(Object obj) {
        if (obj instanceof Number number)
            return number.longValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getLong();
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not long-like.");
    }

    public static float asF(Object obj) {
        if (obj instanceof Number number)
            return number.floatValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getFloat();
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not float-like.");
    }

    public static double asD(Object obj) {
        if (obj instanceof Number number)
            return number.doubleValue();
        else if (obj instanceof Character character)
            return character;
        else if (obj instanceof Boolean bool)
            return Boolean.TRUE.equals(bool)?1:0;
        else if (obj == null)
            return 0;
        else if (obj instanceof Numberlike i)
            return i.getDouble();
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not double-like.");
    }

    public static boolean asZ(Object obj) {
        if (obj instanceof Boolean b)
            return b;
        if (obj instanceof Double d)
            return d!=0;
        else if (obj instanceof Float f)
            return f!=0;
        else if (obj instanceof Zeroable i)
            return !i.isZero();
        try {
            return asI(obj) != 0;
        } catch (ImproperTypeException ignored) {
            return true;
        }
    }

    public static Object fromI(int i) {
        return i;
    }
    public static Object fromS(short s) {
        return s;
    }
    public static Object fromB(byte b) {
        return b;
    }
    public static Object fromC(char c) {
        return c;
    }
    public static Object fromJ(long j) {
        return j;
    }
    public static Object fromF(float f) {
        return f;
    }
    public static Object fromD(double d) {
        return d;
    }
    public static Object fromZ(boolean z) {
        return z;
    }
}
