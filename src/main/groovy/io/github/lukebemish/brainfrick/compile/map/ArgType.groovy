package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import org.objectweb.asm.MethodVisitor

@CompileStatic
interface ArgType extends ThingType {
    static class Parser {
        static ArgType parse(BrainMapParser.ArgNameContext ctx) {
            if (ctx instanceof BrainMapParser.PrimitiveArgContext) {
                ArgType out = PrimitiveType.valueOf(ctx.text.toUpperCase(Locale.ROOT))
                for (int i = 0; i < ctx.ARRAY().size(); i++)
                    out = new ArrayType(out)
                return out
            } else if (ctx instanceof BrainMapParser.ObjArgContext) {
                ArgType out = new ObjectType(ctx.name().collect { it.text })
                for (int i = 0; i < ctx.ARRAY().size(); i++)
                    out = new ArrayType(out)
                return out
            }
        }
    }

    void readArg(MethodVisitor mv, int idx)
}