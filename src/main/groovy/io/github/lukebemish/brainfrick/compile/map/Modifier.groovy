package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser.ModifierContext
import org.objectweb.asm.Opcodes

@CompileStatic
enum Modifier {
    PROTECTED(Opcodes.ACC_PROTECTED),
    PRIVATE(Opcodes.ACC_PRIVATE),
    PUBLIC(Opcodes.ACC_PUBLIC),
    STATIC(Opcodes.ACC_STATIC),
    FINAL(Opcodes.ACC_FINAL)

    final int accessModifier
    Modifier(int accessModifier) {
        this.accessModifier = accessModifier
    }

    static Modifier parse(ModifierContext ctx) {
        if (ctx.PROTECTED() !== null)
            return PROTECTED
        else if (ctx.PRIVATE() !== null)
            return PRIVATE
        else if (ctx.PUBLIC() !== null)
            return PUBLIC
        else if (ctx.STATIC() !== null)
            return STATIC
        else if (ctx.FINAL() !== null)
            return FINAL
    }

    static Set<Modifier> parse(List<ModifierContext> ctxs) {
        return ctxs.collect {parse(it)}.toSet()
    }

    static int access(Set<Modifier> modifiers) {
        int access = 0
        for (Modifier m : modifiers)
            access+=m.accessModifier
        return access
    }
}