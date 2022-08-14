package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser

@CompileStatic
interface ArgType {
    static class Parser {
        static ArgType parse(BrainMapParser.ArgNameContext ctx) {
            if (ctx instanceof BrainMapParser.PrimitiveArgContext)
                return PrimitiveType.valueOf(ctx.text.toUpperCase(Locale.ROOT))
            else if (ctx instanceof BrainMapParser.ObjArgContext)
                return new ObjectType(ctx.name().collect {it.text})
        }
    }
}