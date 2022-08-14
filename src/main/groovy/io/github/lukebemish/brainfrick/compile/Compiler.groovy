package io.github.lukebemish.brainfrick.compile

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainfrickParser
import io.github.lukebemish.brainfrick.compile.map.ArgType
import io.github.lukebemish.brainfrick.compile.map.BrainMap
import io.github.lukebemish.brainfrick.compile.map.ObjectType
import io.github.lukebemish.brainfrick.compile.map.PrimitiveType
import io.github.lukebemish.brainfrick.compile.map.ThingType
import io.github.lukebemish.brainfrick.compile.map.VoidType
import io.github.lukebemish.brainfrick.lang.runtime.Cells
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

import java.nio.file.Files
import java.nio.file.Path

@CompileStatic
class Compiler {
    private static final String OBJECT_NAME = Object.class.name.replace('.','/')

    BrainMap map
    Path outdir
    int ccounter = 0

    void parseClass(BrainfrickParser.ClassContext ctx) {
        BrainMap.BrainType type = map.classes.get(ccounter)
        ccounter++

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
        cw.visit(Opcodes.V17, type.accessModifier, type.type.name,null,OBJECT_NAME,new String[]{})

        BrainMapCompiler mapCompiler = new BrainMapCompiler()
        mapCompiler.classname = type.type.name
        mapCompiler.cw = cw

        mapCompiler.writeBrainMap(map)

        int methodCounter = 0
        for (BrainfrickParser.MethodContext mctx : ctx.method()) {
            BrainMap.BrainMethod method = type.children.findAll {it instanceof BrainMap.BrainMethod}.collect {(BrainMap.BrainMethod)it}.get(methodCounter)
            methodCounter++
            parseMethod(mctx, cw, method, type)
        }

        cw.visitEnd()

        Path outpath = outdir.resolve(type.type.name+".class")
        Files.createDirectories(outpath.getParent())
        Files.write(outpath,cw.toByteArray())
    }

    static void parseMethod(BrainfrickParser.MethodContext ctx, ClassWriter cw, BrainMap.BrainMethod method, BrainMap.BrainType parent) {
        MethodVisitor mv = cw.visitMethod(method.accessModifier, method.name, "("+method.args.collect {it.desc}.join("")+")"+method.out.desc,null, null)

        int numArgs = method.args.size()+(method.isStatic()?0:1)

        mv.visitCode()
        mv.visitTypeInsn(Opcodes.NEW, Cells.class.name.replace('.','/'))
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Cells.class.name.replace('.','/'),"<init>","()V",false)
        mv.visitVarInsn(Opcodes.ASTORE,numArgs)
        mv.visitInsn(Opcodes.ICONST_0)
        mv.visitVarInsn(Opcodes.ISTORE,numArgs+1)
        mv.visitTypeInsn(Opcodes.NEW, ArrayList.class.name.replace('.','/'))
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ArrayList.class.name.replace('.','/'),"<init>","()V",false)
        mv.visitVarInsn(Opcodes.ASTORE,numArgs+2)
        mv.visitTypeInsn(Opcodes.NEW, ArrayList.class.name.replace('.','/'))
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ArrayList.class.name.replace('.','/'),"<init>","()V",false)
        mv.visitVarInsn(Opcodes.ASTORE,numArgs+3)

        for (int i = 0; i < numArgs; i++) {
            mv.visitVarInsn(Opcodes.ALOAD, numArgs + 3)
            ArgType arg
            if (!method.isStatic()) {
                if (i==0)
                    arg = parent.type
                else
                    arg = method.args.get(i-1)
            } else
                arg = method.args.get(i)
            if (arg instanceof PrimitiveType) {
                switch (arg) {
                    case PrimitiveType.INT -> mv.visitVarInsn(Opcodes.ILOAD, i)
                    case PrimitiveType.SHORT -> mv.visitVarInsn(Opcodes.ILOAD, i)
                    case PrimitiveType.BYTE -> mv.visitVarInsn(Opcodes.ILOAD, i)
                    case PrimitiveType.CHAR -> mv.visitVarInsn(Opcodes.ILOAD, i)
                    case PrimitiveType.LONG -> mv.visitVarInsn(Opcodes.LLOAD, i)
                    case PrimitiveType.FLOAT -> mv.visitVarInsn(Opcodes.FLOAD, i)
                    case PrimitiveType.DOUBLE -> mv.visitVarInsn(Opcodes.DLOAD, i)
                    case PrimitiveType.BOOLEAN -> mv.visitVarInsn(Opcodes.ILOAD, i)
                }
                arg.castAsObject(mv)
            } else if (arg instanceof ObjectType) {
                mv.visitVarInsn(Opcodes.ALOAD, i)
            }
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "add", "(L${OBJECT_NAME};)Z", true)
            mv.visitInsn(Opcodes.POP)
        }

        ctx.code().each {
            parseCode(it, mv, numArgs, parent.type.name, method)
        }

        ret(mv, method.out, numArgs)

        mv.visitMaxs(-1,-1)
        mv.visitEnd()
    }


    static void parseCode(BrainfrickParser.CodeContext ctx, MethodVisitor mv, int cells, String classname, BrainMap.BrainMethod method) {
        /**
         * cells: Cells
         * cells+1: pointer
         * cells+2: buffer
         * cells+3: args
         */
        if (ctx.cond()!=null) {
            mv.visitVarInsn(Opcodes.ALOAD, cells)
            mv.visitVarInsn(Opcodes.ILOAD, cells+1)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "isZero", "(I)Z", false)

            Label afterFirst = new Label()
            Label afterLast = new Label()
            mv.visitJumpInsn(Opcodes.IFEQ, afterLast)
            mv.visitLabel(afterFirst)

            // Code in the middle
            ctx.cond().code().each {
                parseCode(it,mv,cells,classname,method)
            }

            mv.visitVarInsn(Opcodes.ALOAD, cells)
            mv.visitVarInsn(Opcodes.ILOAD, cells+1)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "isZero", "(I)Z", false)

            mv.visitJumpInsn(Opcodes.IFNE, afterFirst)
            mv.visitLabel(afterLast)

        } else {
            var instr = ctx.instr()
            if (instr instanceof BrainfrickParser.PushContext) {
                mv.visitVarInsn(Opcodes.ALOAD, cells+2)
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)

                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "get", "(I)L${OBJECT_NAME};", false)
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "add", "(L${OBJECT_NAME};)Z", true)
                mv.visitInsn(Opcodes.POP)
            } else if (instr instanceof BrainfrickParser.PullContext) {
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)

                mv.visitVarInsn(Opcodes.ALOAD, cells+3)

                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "asInt", "(I)I", false)

                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "get", "(I)L${OBJECT_NAME};", true)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "set", "(IL${OBJECT_NAME};)V", false)
            } else if (instr instanceof BrainfrickParser.PincrContext) {
                mv.visitIincInsn(cells+1,1)
            } else if (instr instanceof BrainfrickParser.PdecrContext) {
                mv.visitIincInsn(cells+1, -1)
            } else if (instr instanceof BrainfrickParser.IncrContext) {
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "incr", "(I)V", true)
            } else if (instr instanceof BrainfrickParser.DecrContext) {
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "decr", "(I)V", true)
            } else if (instr instanceof BrainfrickParser.OperateContext) {
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)
                mv.visitFieldInsn(Opcodes.GETSTATIC, classname, BrainMapCompiler.BRAINMAP_FIELD, BrainMapCompiler.BRAINMAP_FIELD_DESC)
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "asInt", "(I)I", false)
                mv.visitInsn(Opcodes.AALOAD)
                mv.visitVarInsn(Opcodes.ALOAD, cells+2)
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, BrainMapCompiler.CALLER_NAME, "call", BrainMapCompiler.CALLER_DESC, true)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "set", "(IL${OBJECT_NAME};)V", false)
            } else if (instr instanceof BrainfrickParser.ReturnContext) {
                ret(mv, method.out, cells)
            }
        }
    }

    static void ret(MethodVisitor mv, ThingType outType, int cells) {
        mv.visitVarInsn(Opcodes.ALOAD, cells+2)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "size", "()I", true)
        mv.visitInsn(Opcodes.ICONST_M1)
        mv.visitInsn(Opcodes.IADD)
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "get", "(I)L${OBJECT_NAME};", true)
        outType.castTo(mv)

        if (outType instanceof ObjectType) {
            mv.visitInsn(Opcodes.ARETURN)
        } else if (outType instanceof VoidType) {
            mv.visitInsn(Opcodes.RETURN)
        } else if (outType instanceof PrimitiveType) {
            switch (outType) {
                case PrimitiveType.INT -> mv.visitInsn(Opcodes.IRETURN)
                case PrimitiveType.SHORT -> mv.visitInsn(Opcodes.IRETURN)
                case PrimitiveType.BYTE -> mv.visitInsn(Opcodes.IRETURN)
                case PrimitiveType.CHAR -> mv.visitInsn(Opcodes.IRETURN)
                case PrimitiveType.LONG -> mv.visitInsn(Opcodes.LRETURN)
                case PrimitiveType.FLOAT -> mv.visitInsn(Opcodes.FRETURN)
                case PrimitiveType.DOUBLE -> mv.visitInsn(Opcodes.DRETURN)
                case PrimitiveType.BOOLEAN -> mv.visitInsn(Opcodes.IRETURN)
            }
        }
    }
}
