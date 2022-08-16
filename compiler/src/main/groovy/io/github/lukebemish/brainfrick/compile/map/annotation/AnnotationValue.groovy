package io.github.lukebemish.brainfrick.compile.map.annotation

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import io.github.lukebemish.brainfrick.compile.map.ObjectType
import org.apache.commons.text.StringEscapeUtils
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Type

@CompileStatic
sealed interface AnnotationValue permits SimpleAnnotationValue, ArrayValue {
    void visitParameter(AnnotationVisitor av, String param)

    static final class Parser {
        private Parser() {}

        static SimpleAnnotationValue parseSimple(BrainMapParser.SimpleValueContext sctx) {
            if (sctx instanceof BrainMapParser.AnnotationStringContext) {
                return new StringValue(sctx.STRING().text)
            } else if (sctx instanceof BrainMapParser.AnnotationFloatContext) {
                return new FloatValue(sctx.DEC_FLOAT().text)
            } else if (sctx instanceof BrainMapParser.AnnotationDoubleContext) {
                return new DoubleValue(sctx.DEC_DOUBLE().text)
            } else if (sctx instanceof BrainMapParser.AnnotationIntContext) {
                return new IntegerValue(sctx.DEC_INT().text)
            } else if (sctx instanceof BrainMapParser.AnnotationShortContext) {
                return new ShortValue(sctx.DEC_SHORT().text)
            } else if (sctx instanceof BrainMapParser.AnnotationByteContext) {
                return new ByteValue(sctx.DEC_BYTE().text)
            } else if (sctx instanceof BrainMapParser.AnnotationLongContext) {
                return new LongValue(sctx.DEC_LONG().text)
            } else if (sctx instanceof BrainMapParser.AnnotationCharContext) {
                return new CharValue(sctx.CHAR_LITERAL().text)
            } else if (sctx instanceof BrainMapParser.AnnotationBooleanContext) {
                return new BooleanValue(sctx.bool().TRUE()!==null)
            } else if (sctx instanceof BrainMapParser.AnnotationClassContext) {
                return new ClassValue(sctx.classname().name().collect {it.text})
            } else if (sctx instanceof BrainMapParser.AnnotationEnumContext) {
                return new EnumValue(sctx.classname().name().collect {it.text},sctx.name().text)
            } else if (sctx instanceof BrainMapParser.AnnotationAnnotationContext) {
                return AnnotationDeclaration.parse(sctx.annotation())
            }
            throw new IllegalArgumentException("Unknown simple annotation parameter type in \"${sctx.text}\"")
        }

        static AnnotationValue parse(BrainMapParser.AssignValueContext ctx) {
            if (ctx instanceof BrainMapParser.AnnotationSimpleContext) {
                var sctx = ctx.simpleValue()
                return parseSimple(sctx)
            } else if (ctx instanceof BrainMapParser.AnnotationArrayContext) {
                return new ArrayValue(ctx.simpleValue().collect {parseSimple(it)})
            }
            throw new IllegalArgumentException("Unknown annotation parameter type in \"${ctx.text}\"")
        }
    }

    static sealed interface SimpleAnnotationValue extends AnnotationValue permits IntegerValue, ShortValue, ByteValue, LongValue, FloatValue, DoubleValue, BooleanValue, StringValue, CharValue, ClassValue, EnumValue, AnnotationDeclaration {
        
    }
    
    static final class IntegerValue implements SimpleAnnotationValue {
        final int value
        IntegerValue(String value) {
            this.value = Integer.valueOf(value.replace('_','').replace('i',''))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class ShortValue implements SimpleAnnotationValue {
        final short value
        ShortValue(String value) {
            this.value = Short.valueOf(value.replace('_','').replace('s',''))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class ByteValue implements SimpleAnnotationValue {
        final byte value
        ByteValue(String value) {
            this.value = Byte.valueOf(value.replace('_','').replace('b',''))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class LongValue implements SimpleAnnotationValue {
        final long value
        LongValue(String value) {
            this.value = Long.valueOf(value.replace('_','').replace('l',''))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class FloatValue implements SimpleAnnotationValue {
        final float value
        FloatValue(String value) {
            this.value = Float.valueOf(value.replace('_','').replace('f',''))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class DoubleValue implements SimpleAnnotationValue {
        final double value
        DoubleValue(String value) {
            this.value = Double.valueOf(value.replace('_','').replace('d',''))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class BooleanValue implements SimpleAnnotationValue {
        final boolean value
        BooleanValue(boolean value) {
            this.value = value
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class StringValue implements SimpleAnnotationValue {
        final String value
        StringValue(String value) {
            this.value = StringEscapeUtils.unescapeJava(value.substring(1,value.size()-1))
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class CharValue implements SimpleAnnotationValue {
        final char value
        CharValue(String value) {
            this.value = StringEscapeUtils.unescapeJava(value.substring(1,value.size()-1)).charAt(0)
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, value)
        }
    }

    static final class ClassValue implements SimpleAnnotationValue {
        final ObjectType clazz
        ClassValue(List<String> names) {
            clazz = new ObjectType(names)
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visit(param, Type.getType(clazz.desc))
        }
    }

    static final class EnumValue implements SimpleAnnotationValue {
        final ObjectType clazz
        final String name
        EnumValue(List<String> names, String name) {
            clazz = new ObjectType(names)
            this.name = name
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            av.visitEnum(param, clazz.desc, name)
        }
    }
    
    static final class ArrayValue implements AnnotationValue {
        final List<SimpleAnnotationValue> values
        ArrayValue(List<SimpleAnnotationValue> values) {
            this.values = values
            ArrayList<Class<? extends SimpleAnnotationValue>> uniques = new ArrayList<>(values.collect {it.class}.unique())
            if (uniques.size() > 1)
                throw new IllegalArgumentException("Attempted to write array as annotation argument with ${uniques.size()} different types of values; 1 required")
            if (uniques.get(0) == EnumValue) {
                var uniqueEnums = values.collect {((EnumValue)it).clazz.name}.unique()
                if (uniqueEnums.size() > 1)
                    throw new IllegalArgumentException("Attempted to write array as annotation argument with non-uniform enum types: ${uniqueEnums}")
            }
        }

        @Override
        void visitParameter(AnnotationVisitor av, String param) {
            AnnotationVisitor arrVisitor = av.visitArray(param)
            values.each {
                it.visitParameter(arrVisitor, null)
            }
            arrVisitor.visitEnd()
        }
    }
}