package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser

@CompileStatic
interface ReturnType extends ThingType {
    static class Parser {
        static ReturnType parse(BrainMapParser.ReturnNameContext ctx) {
            if (ctx instanceof BrainMapParser.PrimitiveOutContext) {
                ReturnType out = PrimitiveType.valueOf(ctx.text.toUpperCase(Locale.ROOT))
                for (int i = 0; i < ctx.ARRAY().size(); i++)
                    out = new ArrayType(out)
                return out
            } else if (ctx instanceof BrainMapParser.ObjOutContext) {
                ReturnType out = new ObjectType(ctx.name().collect { it.text })
                for (int i = 0; i < ctx.ARRAY().size(); i++)
                    out = new ArrayType(out)
                return out
            } else if (ctx instanceof BrainMapParser.VoidOutContext)
                return VoidType.instance
        }
    }
}