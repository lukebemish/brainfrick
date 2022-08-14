package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
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
        String methodName = "to"+desc
        String methodDesc = "(Ljava/lang/Object;)${desc}"
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, InvocationUtils.class.name.replace('.','/'),methodName, methodDesc, false)
    }

    @Override
    void castAsObject(MethodVisitor mv) {
        String methodName = "from"+desc
        String methodDesc = "(${desc})Ljava/lang/Object;"
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, InvocationUtils.class.name.replace('.','/'),methodName, methodDesc, false)
    }
}