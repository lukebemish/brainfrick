package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.BrainMapCompiler
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

    @Override
    void writeReturn(MethodVisitor mv, int cells) {
        mv.visitVarInsn(Opcodes.ALOAD, cells+2)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "size", "()I", true)
        mv.visitInsn(Opcodes.ICONST_M1)
        mv.visitInsn(Opcodes.IADD)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "get", "(I)L${BrainMapCompiler.OBJECT_NAME};", true)
        this.castTo(mv)
        mv.visitInsn(Opcodes.ARETURN)
    }
}
