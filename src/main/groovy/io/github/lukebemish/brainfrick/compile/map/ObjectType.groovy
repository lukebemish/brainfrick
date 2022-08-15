package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CompileStatic
class ObjectType implements ArgType, ReturnType {
    final List<String> names
    final String desc
    final String name

    ObjectType(@NotNull List<String> names) {
        this.names = names
        this.desc = "L${names.join('/')};"
        this.name = names.join('/')
    }

    @Override
    void castTo(MethodVisitor mv) {
        mv.visitTypeInsn(Opcodes.CHECKCAST, names.join('/'))
    }

    @Override
    void castAsObject(MethodVisitor mv) {

    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ObjectType that = (ObjectType) o

        if (names != that.names) return false

        return true
    }

    int hashCode() {
        return names.hashCode()
    }
}
