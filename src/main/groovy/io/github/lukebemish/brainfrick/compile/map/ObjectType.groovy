package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CompileStatic
class ObjectType implements ArgType, ReturnType {
    final List<String> names
    final String desc

    ObjectType(List<String> names) {
        this.names = names
        this.desc = "L${names.join('/')};"
    }

    @Override
    void castTo(MethodVisitor mv) {
        mv.visitTypeInsn(Opcodes.CHECKCAST, names.join('/'))
    }

    @Override
    void castAsObject(MethodVisitor mv) {

    }
}
