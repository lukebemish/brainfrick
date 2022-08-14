package io.github.lukebemish.brainfrick.compile

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.map.ArgType
import io.github.lukebemish.brainfrick.compile.map.BrainMap
import io.github.lukebemish.brainfrick.lang.runtime.Caller
import io.github.lukebemish.brainfrick.lang.runtime.InvocationUtils
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CompileStatic
class Compiler {
    private static final String CLINIT = "<clinit>"
    private static final String CLINIT_DESC = "()V"
    private static final String BRAINMAP_FIELD = "\$BRAINMAP"
    private static final String OBJECT_NAME = Object.class.getName().replace('.','/')
    private static final String CALLER_PREFIX = "\$brainfrickCaller\$"
    private static final String CALLER_NAME = Caller.class.getName().replace('.','/')
    private static final String LIST_NAME = List.class.name.replace('.','/')
    private static final String CALLER_DESC = "(L${LIST_NAME};)L${OBJECT_NAME};"
    private static final String INIT_NAME = "<init>"
    private static final String BUFFER_UTILS_NAME = InvocationUtils.class.getName().replace('.','/')
    private static final String CHECK_ENOUGH = "checkEnough"
    private static final String CHECK_ENOUGH_DESC = "(II)V"


    ClassWriter cw

    private static void constantInt(MethodVisitor mv, int val) {
        switch (val) {
            case -1 -> mv.visitInsn(Opcodes.ICONST_M1)
            case 0 -> mv.visitInsn(Opcodes.ICONST_0)
            case 1 -> mv.visitInsn(Opcodes.ICONST_1)
            case 2 -> mv.visitInsn(Opcodes.ICONST_2)
            case 3 -> mv.visitInsn(Opcodes.ICONST_3)
            case 4 -> mv.visitInsn(Opcodes.ICONST_4)
            case 5 -> mv.visitInsn(Opcodes.ICONST_5)
            default -> {
                if (val < Byte.MAX_VALUE)
                    mv.visitIntInsn(Opcodes.BIPUSH, val)
                else if (val < Short.MAX_VALUE)
                    mv.visitIntInsn(Opcodes.SIPUSH, val)
                else
                    mv.visitLdcInsn(val)
            }
        }
    }

    static void writeBrainMethod(BrainMap.BrainMethod method, MethodVisitor mv, boolean isinterface, BrainMap.BrainType parent) {
        mv.visitCode()

        StringBuilder callDesc = new StringBuilder("(")
        method.args.each {callDesc.append(it.desc)}
        callDesc.append(')')
        callDesc.append(method.out.desc)

        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
        mv.visitInsn(Opcodes.DUP)
        constantInt(mv, method.args.size())
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
        mv.visitVarInsn(Opcodes.ISTORE,1)

        for (int i = 0; i < method.args.size(); i++) {
            ArgType type = method.args.get(i)
            mv.visitIincInsn(1,-1)
            mv.visitVarInsn(Opcodes.ALOAD,0)
            mv.visitVarInsn(Opcodes.ILOAD,1)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            type.castTo(mv)
        }

        int opcode = Opcodes.INVOKEVIRTUAL
        if ((method.accessModifier & Opcodes.ACC_STATIC) != 0)
            opcode=Opcodes.INVOKESTATIC
        else if (isinterface)
            opcode=Opcodes.INVOKEINTERFACE
        mv.visitMethodInsn(opcode, parent.type.names.join('/'), method.name, callDesc.toString(), isinterface)

        method.out.castAsObject(mv)

        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(-1,-1)
        mv.visitEnd()
    }

    static void writeBrainCtor(BrainMap.BrainCtor ctor, MethodVisitor mv, BrainMap.BrainClass parent) {
        mv.visitCode()

        StringBuilder callDesc = new StringBuilder("(")
        ctor.args.each {callDesc.append(it.desc)}
        callDesc.append(')V')

        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
        mv.visitInsn(Opcodes.DUP)
        constantInt(mv, ctor.args.size())
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
        mv.visitVarInsn(Opcodes.ISTORE,1)

        mv.visitTypeInsn(Opcodes.NEW, parent.type.names.join('/'))
        mv.visitInsn(Opcodes.DUP)

        for (int i = 0; i < ctor.args.size(); i++) {
            ArgType type = ctor.args.get(i)
            mv.visitIincInsn(1,-1)
            mv.visitVarInsn(Opcodes.ALOAD,0)
            mv.visitVarInsn(Opcodes.ILOAD,1)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            type.castTo(mv)
        }

        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, parent.type.names.join('/'), INIT_NAME, callDesc.toString(), false)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(-1,-1)
        mv.visitEnd()
    }

    void writeBrainMap(BrainMap map) {
        int counter = 0
        for (BrainMap.BrainType type : map.classes) {
            counter++
            if (type instanceof BrainMap.BrainClass) {
                for (BrainMap.BrainChild child : type.children) {
                    MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, CALLER_PREFIX+counter,CALLER_DESC,null,null)
                    counter++
                    if (child instanceof BrainMap.BrainMethod) {
                        writeBrainMethod(child, mv, false, type)
                    } else if (child instanceof BrainMap.BrainCtor) {
                        writeBrainCtor(child, mv, type)
                    } else if (child instanceof BrainMap.BrainPutter) {

                    } else if (child instanceof BrainMap.BrainGetter) {

                    }
                }
            } else if (type instanceof BrainMap.BrainInterface) {
                for (BrainMap.BrainMethod method : type.methods) {
                    counter++
                    MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, CALLER_PREFIX+counter,CALLER_DESC,null,null)
                    writeBrainMethod(method, mv, true, type)
                }
            }
        }

        FieldVisitor mapfield = cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                BRAINMAP_FIELD, "[L${CALLER_NAME};", null, null)

        MethodVisitor clinit = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, CLINIT, CLINIT_DESC, null, null)

    }
}
