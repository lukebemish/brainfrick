package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.BrainMapCompiler
import io.github.lukebemish.brainfrick.lang.runtime.StringUtils
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

    ObjectType(@NotNull Class<?> clazz) {
        this(clazz.getName().split('\\.').toList())
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

    @Override
    void readArg(MethodVisitor mv, int idx) {
        mv.visitVarInsn(Opcodes.ALOAD, idx)
    }

    @Override
    void writeReturn(MethodVisitor mv, int cells) {
        if (name != new ObjectType(String.class).name) {
            mv.visitVarInsn(Opcodes.ALOAD, cells + 2)
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.', '/'), "size", "()I", true)
            mv.visitInsn(Opcodes.ICONST_M1)
            mv.visitInsn(Opcodes.IADD)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.', '/'), "get", "(I)L${BrainMapCompiler.OBJECT_NAME};", true)
            this.castTo(mv)
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, cells + 2)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, StringUtils.class.name.replace('.','/'), "fromChars", "(L${BrainMapCompiler.LIST_NAME};)L${String.class.name.replace('.','/')};", false)
        }
        mv.visitInsn(Opcodes.ARETURN)
    }
}
