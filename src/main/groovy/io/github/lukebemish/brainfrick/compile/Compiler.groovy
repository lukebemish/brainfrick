package io.github.lukebemish.brainfrick.compile

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.map.ArgType
import io.github.lukebemish.brainfrick.compile.map.BrainMap
import io.github.lukebemish.brainfrick.lang.runtime.Caller
import io.github.lukebemish.brainfrick.lang.runtime.InvocationUtils
import org.objectweb.asm.*

import java.lang.invoke.LambdaMetafactory

@CompileStatic
class BrainMapCompiler {
    private static final String CLINIT = "<clinit>"
    private static final String CLINIT_DESC = "()V"
    private static final String BRAINMAP_FIELD = "\$BRAINMAP"
    private static final String BRAINMAP_FIELD_DESC = "[L${CALLER_NAME};"
    private static final String OBJECT_NAME = Object.class.getName().replace('.','/')
    private static final String CALLER_PREFIX = "\$brainfrickCaller\$"
    private static final String CALLER_NAME = Caller.class.getName().replace('.','/')
    private static final String LIST_NAME = List.class.name.replace('.','/')
    private static final String CALLER_DESC = "(L${LIST_NAME};)L${OBJECT_NAME};"
    private static final String INIT_NAME = "<init>"
    private static final String BUFFER_UTILS_NAME = InvocationUtils.class.getName().replace('.','/')
    private static final String CHECK_ENOUGH = "checkEnough"
    private static final String CHECK_ENOUGH_DESC = "(II)V"
    private static final String LAMBDAMETAFACTORY_NAME = LambdaMetafactory.class.name.replace('.','/')
    private static final String METAFACTORY_DESC = "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;" +
            "Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"


    ClassWriter cw
    String classname

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
                if (val <= Byte.MAX_VALUE && val >= Byte.MIN_VALUE)
                    mv.visitIntInsn(Opcodes.BIPUSH, val)
                else if (val <= Short.MAX_VALUE && val >= Short.MIN_VALUE)
                    mv.visitIntInsn(Opcodes.SIPUSH, val)
                else
                    mv.visitLdcInsn(val)
            }
        }
    }

    static void writeBrainMethod(BrainMap.BrainMethod method, MethodVisitor mv, BrainMap.BrainType parent) {
        mv.visitCode()

        boolean isinterface = parent.isinterface

        StringBuilder callDesc = new StringBuilder("(")
        method.args.each {callDesc.append(it.desc)}
        callDesc.append(')')
        callDesc.append(method.out.desc)

        int size = method.args.size()
        if ((method.accessModifier & Opcodes.ACC_STATIC) == 0)
            size+=1


        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
        mv.visitInsn(Opcodes.DUP)
        constantInt(mv, size)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
        constantInt(mv, -size)
        mv.visitInsn(Opcodes.IADD)
        mv.visitVarInsn(Opcodes.ISTORE,1)

        if ((method.accessModifier & Opcodes.ACC_STATIC) == 0) {
            ArgType type = parent.type
            mv.visitVarInsn(Opcodes.ALOAD,0)
            mv.visitVarInsn(Opcodes.ILOAD,1)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            type.castTo(mv)
        }
        for (int i = 0; i < method.args.size(); i++) {
            ArgType type = method.args.get(i)
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

    static void writeClassCaller(BrainMap.BrainType type, MethodVisitor mv) {
        mv.visitCode()
        mv.visitLdcInsn(Type.getType(type.type.names.join('/')))
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(-1,-1)
        mv.visitEnd()
    }

    static void writeFieldGetter(BrainMap.BrainGetter getter, MethodVisitor mv, BrainMap.BrainType parent) {
        mv.visitCode()

        if ((getter.accessModifier & Opcodes.ACC_STATIC) == 0) {
            mv.visitVarInsn(Opcodes.ALOAD,0)

            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
            mv.visitInsn(Opcodes.DUP)
            constantInt(mv, 1)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
            mv.visitInsn(Opcodes.ICONST_M1)
            mv.visitInsn(Opcodes.IADD)

            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            parent.type.castTo(mv)

            mv.visitFieldInsn(Opcodes.GETFIELD, parent.type.names.join('/'), getter.name, getter.type.desc)
        } else {
            mv.visitFieldInsn(Opcodes.GETSTATIC, parent.type.names.join('/'), getter.name, getter.type.desc)
        }

        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(-1,-1)
        mv.visitEnd()
    }

    static void writeFieldPutter(BrainMap.BrainPutter putter, MethodVisitor mv, BrainMap.BrainType parent) {
        mv.visitCode()

        if ((putter.accessModifier & Opcodes.ACC_STATIC) == 0) {
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
            mv.visitInsn(Opcodes.DUP)
            constantInt(mv, 2)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
            mv.visitInsn(Opcodes.ICONST_M1)
            mv.visitInsn(Opcodes.IADD)
            mv.visitVarInsn(Opcodes.ISTORE,1)

            mv.visitVarInsn(Opcodes.ALOAD,0)
            mv.visitVarInsn(Opcodes.ILOAD,1)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            parent.type.castTo(mv)

            mv.visitVarInsn(Opcodes.ALOAD,0)
            mv.visitVarInsn(Opcodes.ILOAD,1)
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            putter.type.castTo(mv)

            mv.visitFieldInsn(Opcodes.PUTFIELD, parent.type.names.join('/'), putter.name, putter.type.desc)
        } else {
            mv.visitVarInsn(Opcodes.ALOAD,0)

            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
            mv.visitInsn(Opcodes.DUP)
            constantInt(mv, 1)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
            mv.visitInsn(Opcodes.ICONST_M1)
            mv.visitInsn(Opcodes.IADD)

            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
            putter.type.castTo(mv)

            mv.visitFieldInsn(Opcodes.PUTSTATIC, parent.type.names.join('/'), putter.name, putter.type.desc)
        }

        mv.visitInsn(Opcodes.ACONST_NULL)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitMaxs(-1,-1)
        mv.visitEnd()
    }

    static void writeBrainCtor(BrainMap.BrainCtor ctor, MethodVisitor mv, BrainMap.BrainType parent) {
        mv.visitCode()

        StringBuilder callDesc = new StringBuilder("(")
        ctor.args.each {callDesc.append(it.desc)}
        callDesc.append(')V')

        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST_NAME, "size", "()I", true)
        mv.visitInsn(Opcodes.DUP)
        constantInt(mv, ctor.args.size())
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BUFFER_UTILS_NAME, CHECK_ENOUGH, CHECK_ENOUGH_DESC, false)
        constantInt(mv, -ctor.args.size())
        mv.visitInsn(Opcodes.IADD)
        mv.visitVarInsn(Opcodes.ISTORE,1)

        mv.visitTypeInsn(Opcodes.NEW, parent.type.names.join('/'))
        mv.visitInsn(Opcodes.DUP)

        for (int i = 0; i < ctor.args.size(); i++) {
            ArgType type = ctor.args.get(i)
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
        int counter = -1
        for (BrainMap.BrainType type : map.classes) {
            counter++
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, CALLER_PREFIX+counter,CALLER_DESC,null,null)
            writeClassCaller(type, mv)
            for (BrainMap.BrainChild child : type.children) {
                mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, CALLER_PREFIX+counter,CALLER_DESC,null,null)
                counter++
                if (child instanceof BrainMap.BrainMethod) {
                    writeBrainMethod(child, mv, type)
                } else if (child instanceof BrainMap.BrainCtor) {
                    writeBrainCtor(child, mv, type)
                } else if (child instanceof BrainMap.BrainPutter) {
                    writeFieldPutter(child, mv, type)
                } else if (child instanceof BrainMap.BrainGetter) {
                    writeFieldGetter(child, mv, type)
                }
            }
        }
        counter++

        FieldVisitor mapfield = cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                BRAINMAP_FIELD, BRAINMAP_FIELD_DESC, null, null)
        mapfield.visitEnd()

        MethodVisitor clinit = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, CLINIT, CLINIT_DESC, null, null)
        clinit.visitCode()
        constantInt(clinit, counter)
        clinit.visitTypeInsn(Opcodes.ANEWARRAY,CALLER_NAME)
        clinit.visitVarInsn(Opcodes.ASTORE,0)
        for (int i = 0; i < counter; i++) {
            clinit.visitVarInsn(Opcodes.ALOAD,0)
            constantInt(clinit, i)
            Handle bootstrap = new Handle(Opcodes.H_INVOKESTATIC, LAMBDAMETAFACTORY_NAME, "metafactory", METAFACTORY_DESC, false)
            clinit.visitInvokeDynamicInsn("call", "()L${CALLER_NAME};",
                    bootstrap,
                    Type.getType(CALLER_DESC),
                    new Handle(Opcodes.H_INVOKESTATIC, classname, CALLER_PREFIX+i, CALLER_DESC, false),
                    Type.getType(CALLER_DESC)
            )
            clinit.visitInsn(Opcodes.AASTORE)
        }
        clinit.visitVarInsn(Opcodes.ALOAD,0)
        clinit.visitFieldInsn(Opcodes.PUTSTATIC, classname, BRAINMAP_FIELD, BRAINMAP_FIELD_DESC)

        clinit.visitMaxs(-1,-1)
        clinit.visitEnd()
    }
}
