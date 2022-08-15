package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CompileStatic
@Singleton
class VoidType implements ReturnType {
    final String desc = 'V'

    @Override
    void castAsObject(MethodVisitor mv) {
        mv.visitInsn(Opcodes.ACONST_NULL)
    }

    @Override
    void castTo(MethodVisitor mv) {
        mv.visitInsn(Opcodes.POP)
    }

    @Override
    void writeReturn(MethodVisitor mv, int cells) {
        mv.visitInsn(Opcodes.RETURN)
    }
}
