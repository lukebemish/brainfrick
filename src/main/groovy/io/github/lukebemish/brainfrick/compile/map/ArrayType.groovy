package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CompileStatic
class ArrayType implements ArgType, ReturnType {
    final ThingType wrapped

    ArrayType(ThingType wrapped) {
        this.wrapped = wrapped
    }

    @Override
    String getDesc() {
        return "["+wrapped.desc
    }

    @Override
    void castAsObject(MethodVisitor mv) {

    }

    @Override
    void castTo(MethodVisitor mv) {
        mv.visitTypeInsn(Opcodes.CHECKCAST, this.desc)
    }

    @Override
    void readArg(MethodVisitor mv, int idx) {
        mv.visitVarInsn(Opcodes.ALOAD, idx)
    }
}
