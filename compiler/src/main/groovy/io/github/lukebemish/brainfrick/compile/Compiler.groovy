package io.github.lukebemish.brainfrick.compile

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainfrickParser
import io.github.lukebemish.brainfrick.compile.map.*
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

    void skipClass() {
        ccounter++
    }

    void parseClass(BrainfrickParser.ActualClassContext ctx) {
        BrainMap.BrainType type = map.classes.get(ccounter)
        ccounter++

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
        String[] interfaces = type.interfaces.collect {it->it.name}.toArray(new String[]{})
        cw.visit(Opcodes.V17, type.accessModifier, type.type.name,null,type.parent.name,interfaces)

        type.annotations.each {
            var av = cw.visitAnnotation(it.type().desc, it.runtime())
            it.values().each {key, val ->
                val.visitParameter(av,key)
            }
            av.visitEnd()
        }

        BrainMapCompiler mapCompiler = new BrainMapCompiler()
        mapCompiler.classname = type.type.name
        mapCompiler.cw = cw

        mapCompiler.writeBrainMap(map)

        //Fields
        List<BrainMap.BrainField> staticFields = new ArrayList<>()
        Set<String> knownStatic = new HashSet<>()
        List<BrainMap.BrainField> instanceFields = new ArrayList<>()
        Set<String> knownInstance = new HashSet<>()
        type.children.findAll {it instanceof BrainMap.BrainPutter}.each {
            if (it.isStatic()) {
                staticFields.add((BrainMap.BrainField) it)
                knownStatic.add(((BrainMap.BrainField)it).name)
            } else {
                instanceFields.add((BrainMap.BrainField) it)
                knownInstance.add(((BrainMap.BrainField)it).name)
            }
        }
        type.children.findAll {it instanceof BrainMap.BrainGetter}.each {
            BrainMap.BrainGetter itg = (BrainMap.BrainGetter) it
            if (it.isStatic() && !knownStatic.contains(itg.name)) {
                staticFields.add((BrainMap.BrainField) it)
                knownStatic.add(((BrainMap.BrainField)it).name)
            } else if (!knownInstance.contains(itg.name)) {
                instanceFields.add((BrainMap.BrainField) it)
                knownInstance.add(((BrainMap.BrainField)it).name)
            }
        }
        ArrayList<BrainMap.BrainField> combined = new ArrayList<>()
        combined.addAll(instanceFields)
        combined.addAll(staticFields)
        combined.each {
            var fv = cw.visitField(it.accessModifier, it.name, it.type.desc, null, null)
            ((BrainMap.BrainField)combined).findCombined().each {
                var av = fv.visitAnnotation(it.type().desc, it.runtime())
                it.values().each {key, value ->
                    value.visitParameter(av,key)
                }
                av.visitEnd()
            }
            fv.visitEnd()
        }

        //Methods
        var knownMethods = type.children.findAll {it instanceof BrainMap.BrainMethod || it instanceof BrainMap.BrainCtor}

        int methodCounter = 0
        for (BrainfrickParser.MethodDeclContext mctx : ctx.methodDecl()) {
            BrainMap.BrainChild method = knownMethods.get(methodCounter)
            methodCounter++
            if (mctx instanceof BrainfrickParser.ActualMethodContext) {
                if (method instanceof BrainMap.BrainMethod) {
                    MethodVisitor mv = cw.visitMethod(method.accessModifier, method.name, "(" + method.args.collect { it.desc }.join("") + ")" + method.out.desc, null, null)
                    method.writeAnnotations(mv)
                    parseMethod(mctx.method(), mv, method, type, false)
                } else if (method instanceof BrainMap.BrainCtor) {
                    MethodVisitor mv = cw.visitMethod(method.accessModifier, "<init>", "(" + method.args.collect { it.desc }.join("") + ")V", null, null)
                    method.writeAnnotations(mv)
                    BrainMap.BrainMethod newMethod = new BrainMap.BrainMethod()
                    newMethod.name = "<init>"
                    newMethod.out = VoidType.instance
                    newMethod.args = method.args
                    newMethod.accessModifier = method.accessModifier
                    newMethod.setParent(method.parent)

                    if (method.superCtor != null) {
                        newMethod.superMethod = new BrainMap.BrainMethod.SuperMethod()
                        newMethod.superMethod.args = method.superCtor.args
                        newMethod.superMethod.out = VoidType.instance
                        newMethod.superMethod.name = "<init>"
                        newMethod.superMethod.type = method.superCtor.type
                    }

                    parseMethod(mctx.method(), mv, newMethod, type, true)
                }
            } else if (mctx instanceof BrainfrickParser.AbstractMethodContext) {
                if (method instanceof BrainMap.BrainMethod) {
                    MethodVisitor mv = cw.visitMethod(method.accessModifier | Opcodes.ACC_ABSTRACT, method.name, "(" + method.args.collect { it.desc }.join("") + ")" + method.out.desc, null, null)
                    method.writeAnnotations(mv)
                    mv.visitEnd()
                } else if (method instanceof BrainMap.BrainCtor) {
                    throw new IllegalArgumentException("Attempted to abstractly define constructor")
                }
            } else if (mctx instanceof BrainfrickParser.SkipMethodContext) {
                //Skip this method
            }
        }

        //Static blocks
        BrainMap.BrainMethod clinit = new BrainMap.BrainMethod()
        clinit.args = List.of()
        clinit.out = VoidType.instance
        clinit.name = "<clinit>"
        clinit.accessModifier = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC
        clinit.setParent(type)
        for (BrainfrickParser.MethodContext mctx : ctx.method()) {
            parseMethod(mctx, mapCompiler.partialClinit, clinit, type, true)
        }
        mapCompiler.partialClinit.visitInsn(Opcodes.RETURN)
        mapCompiler.partialClinit.visitMaxs(-1, -1)
        mapCompiler.partialClinit.visitEnd()

        cw.visitEnd()

        Path outpath = outdir.resolve(type.type.name+".class")
        Files.createDirectories(outpath.getParent())
        Files.write(outpath,cw.toByteArray())
    }

    private void parseMethod(BrainfrickParser.MethodContext ctx, MethodVisitor mv, BrainMap.BrainMethod method, BrainMap.BrainType parent, boolean isinit) {

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
            if (i==0 && isinit) {
                mv.visitInsn(Opcodes.ACONST_NULL)
            } else {
                arg.readArg(mv, i)
            }
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "add", "(L${OBJECT_NAME};)Z", true)
            mv.visitInsn(Opcodes.POP)
        }

        ctx.code().each {
            parseCode(it, mv, numArgs, parent.type.name, method, isinit)
        }

        if (!(method.isStatic() && isinit)) {
            ret(mv, method.out, numArgs)

            mv.visitMaxs(-1, -1)
            mv.visitEnd()
        }
    }


    private void parseCode(BrainfrickParser.CodeContext ctx, MethodVisitor mv, int cells, String classname, BrainMap.BrainMethod method, boolean isinit) {
        /**
         * cells: Cells
         * cells+1: pointer
         * cells+2: buffer
         * cells+3: args
         * cells+4: tmp; int
         */
        if (ctx.cond()!=null) {
            mv.visitVarInsn(Opcodes.ALOAD, cells)
            mv.visitVarInsn(Opcodes.ILOAD, cells+1)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "isZero", "(I)Z", false)

            Label afterFirst = new Label()
            Label afterLast = new Label()
            mv.visitJumpInsn(Opcodes.IFNE, afterLast)
            mv.visitLabel(afterFirst)

            // Code in the middle
            ctx.cond().code().each {
                parseCode(it, mv, cells, classname, method, isinit)
            }

            mv.visitVarInsn(Opcodes.ALOAD, cells)
            mv.visitVarInsn(Opcodes.ILOAD, cells+1)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "isZero", "(I)Z", false)

            mv.visitJumpInsn(Opcodes.IFEQ, afterFirst)
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
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "incr", "(I)V", false)
            } else if (instr instanceof BrainfrickParser.DecrContext) {
                mv.visitVarInsn(Opcodes.ALOAD, cells)
                mv.visitVarInsn(Opcodes.ILOAD, cells+1)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Cells.class.name.replace('.','/'), "decr", "(I)V", false)
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
            } else if (instr instanceof BrainfrickParser.SuperContext) {
                if (method.superMethod !== null && !method.isStatic()) {
                    BrainMap.BrainMethod.SuperMethod s = method.superMethod
                    int argCount = s.args.size()+1
                    if (isinit)
                        argCount--
                    if (argCount!=0) {
                        mv.visitVarInsn(Opcodes.ALOAD, cells + 2)
                        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, BrainMapCompiler.LIST_NAME, "size", "()I", true)
                        mv.visitInsn(Opcodes.DUP)
                        BrainMapCompiler.constantInt(mv, argCount)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, BrainMapCompiler.BUFFER_UTILS_NAME, BrainMapCompiler.CHECK_ENOUGH, BrainMapCompiler.CHECK_ENOUGH_DESC, false)
                        BrainMapCompiler.constantInt(mv, -argCount)
                        mv.visitInsn(Opcodes.IADD)
                        mv.visitVarInsn(Opcodes.ISTORE, cells + 4)
                    }
                    for (int i = 0; i < s.args.size()+1; i++) {
                        if (i==0 && isinit) {
                            mv.visitVarInsn(Opcodes.ALOAD, 0)
                        } else {
                            mv.visitVarInsn(Opcodes.ALOAD, cells + 2)
                            mv.visitVarInsn(Opcodes.ILOAD, cells + 4)
                            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, BrainMapCompiler.LIST_NAME, "remove", "(I)L${OBJECT_NAME};", true)
                        }
                    }
                    ObjectType t = s.type
                    List<BrainMap.BrainType> matching = map.classes.findAll {it.type==t}
                    if (matching.size()==0) {
                        throw new IllegalStateException(String.format("Method %s in class %s attempts to use super method, but references class %s not defined in the brain map.",method.name,classname,t.name))
                    }
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, s.type.name, s.name, "(${s.args.collect {it.desc}.join("")})${s.out.desc}", matching.get(0).isinterface)
                    if (isinit) {
                        mv.visitVarInsn(Opcodes.ALOAD, cells + 3)
                        mv.visitInsn(Opcodes.ICONST_0)
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, List.class.name.replace('.','/'), "set", "(IL${OBJECT_NAME};)L${OBJECT_NAME};", true)
                        mv.visitInsn(Opcodes.POP)
                    }
                } else if (method.isStatic()) {
                    throw new IllegalStateException(String.format("Method %s in class %s attempts to use super method, but is static.",method.name,classname))
                } else {
                    throw new IllegalStateException(String.format("Method %s in class %s attempts to call undefined super.",method.name,classname))
                }
            }
        }
    }

    private static void ret(MethodVisitor mv, ReturnType outType, int cells) {
        outType.writeReturn(mv,cells)
    }
}
