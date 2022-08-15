package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.BrainMapCompiler
import io.github.lukebemish.brainfrick.lang.runtime.InvocationUtils
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CompileStatic
enum PrimitiveType implements ArgType, ReturnType {
    INT('I'),
    SHORT('S'),
    BYTE('B'),
    CHAR('C'),
    LONG('J'),
    FLOAT('F'),
    DOUBLE('D'),
    BOOLEAN('Z')

    final String desc
    PrimitiveType(String desc) {
        this.desc = desc
    }

    @Override
    void castTo(MethodVisitor mv) {
        String methodName = "as"+desc
        String methodDesc = "(Ljava/lang/Object;)${desc}"
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, InvocationUtils.class.name.replace('.','/'),methodName, methodDesc, false)
    }

    @Override
    void castAsObject(MethodVisitor mv) {
        String methodName = "from"+desc
        String methodDesc = "(${desc})Ljava/lang/Object;"
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, InvocationUtils.class.name.replace('.','/'),methodName, methodDesc, false)
    }

    @Override
    void readArg(MethodVisitor mv, int i) {
        switch (this) {
            case INT -> mv.visitVarInsn(Opcodes.ILOAD, i)
            case SHORT -> mv.visitVarInsn(Opcodes.ILOAD, i)
            case BYTE -> mv.visitVarInsn(Opcodes.ILOAD, i)
            case CHAR -> mv.visitVarInsn(Opcodes.ILOAD, i)
            case LONG -> mv.visitVarInsn(Opcodes.LLOAD, i)
            case FLOAT -> mv.visitVarInsn(Opcodes.FLOAD, i)
            case DOUBLE -> mv.visitVarInsn(Opcodes.DLOAD, i)
            case BOOLEAN -> mv.visitVarInsn(Opcodes.ILOAD, i)
        }
        this.castAsObject(mv)
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
        switch (this) {
            case INT -> mv.visitInsn(Opcodes.IRETURN)
            case SHORT -> mv.visitInsn(Opcodes.IRETURN)
            case BYTE -> mv.visitInsn(Opcodes.IRETURN)
            case CHAR -> mv.visitInsn(Opcodes.IRETURN)
            case LONG -> mv.visitInsn(Opcodes.LRETURN)
            case FLOAT -> mv.visitInsn(Opcodes.FRETURN)
            case DOUBLE -> mv.visitInsn(Opcodes.DRETURN)
            case BOOLEAN -> mv.visitInsn(Opcodes.IRETURN)
        }
    }
}