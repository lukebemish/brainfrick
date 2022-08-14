package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import org.objectweb.asm.MethodVisitor

@CompileStatic
interface ReturnType extends ThingType {
    static class Parser {
        static ReturnType parse(BrainMapParser.ReturnNameContext ctx) {
            if (ctx instanceof BrainMapParser.PrimitiveOutContext)
                return PrimitiveType.valueOf(ctx.text.toUpperCase(Locale.ROOT))
            else if (ctx instanceof BrainMapParser.ObjOutContext)
                return new ObjectType(ctx.name().collect {it.text})
            else if (ctx instanceof BrainMapParser.VoidOutContext)
                return VoidType.instance
        }
    }
    void castAsObject(MethodVisitor mv)
}