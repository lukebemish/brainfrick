package io.github.lukebemish.brainfrick.lang.runtime;

import io.github.lukebemish.brainfrick.lang.Numberlike;
import io.github.lukebemish.brainfrick.lang.Zeroable;
import io.github.lukebemish.brainfrick.lang.exception.BufferTooSmallException;
import io.github.lukebemish.brainfrick.lang.exception.ImproperTypeException;

/**
 * Contains methods used while invoking {@link Caller} instances from a compiled brainmap, or when attempting to box or
 * unbox values in order to create integer indices during {@link Cells#asInt(int)}.
 */
public final class InvocationUtils {
    private InvocationUtils() {}

    /**
     * Checks whether the given buffer size is larger than the required buffer size.
     * @param size The number of values the buffer can provide.
     * @param needed The number of values needed to invoke the method.
     * @exception BufferTooSmallException if the provided buffer size is less than the required size.
     */
    public static void checkEnough(int size, int needed) {
        if (size < needed)
            throw new BufferTooSmallException("Buffer contains "+size+" elements; "+needed+" needed.",needed,size);
    }

    /**
     * Attempts to unbox or convert a value to an int.
     * @param obj An object to unbox.
     * @return An integer representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
    public static int asI(Object obj) {
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
        else if (obj instanceof Zeroable z && z.isZero())
            return 0;
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not integer-like.",obj.getClass(),int.class);
    }

    /**
     * Attempts to unbox or convert a value to a short.
     * @param obj An object to unbox.
     * @return A short representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
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
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not short-like.",obj.getClass(),short.class);
    }

    /**
     * Attempts to unbox or convert a value to a byte.
     * @param obj An object to unbox.
     * @return A byte representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
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
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not byte-like.",obj.getClass(),byte.class);
    }

    /**
     * Attempts to unbox or convert a value to a char.
     * @param obj An object to unbox.
     * @return A char representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
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
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not char-like.",obj.getClass(),char.class);
    }

    /**
     * Attempts to unbox or convert a value to a long.
     * @param obj An object to unbox.
     * @return A long representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
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
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not long-like.",obj.getClass(),long.class);
    }

    /**
     * Attempts to unbox or convert a value to a float.
     * @param obj An object to unbox.
     * @return A float representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
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
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not float-like.",obj.getClass(),float.class);
    }

    /**
     * Attempts to unbox or convert a value to a double.
     * @param obj An object to unbox.
     * @return A double representation of the object.
     * @exception ImproperTypeException if the value cannot be unboxed or converted.
     * @see Numberlike
     */
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
        throw new ImproperTypeException("Object of type "+obj.getClass()+" is not double-like.",obj.getClass(),double.class);
    }

    /**
     * Attempts to unbox or convert a value to a boolean.
     * @param obj An object to unbox.
     * @return A boolean representation of the object.
     * @see Numberlike
     * @see Zeroable
     */
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
}
