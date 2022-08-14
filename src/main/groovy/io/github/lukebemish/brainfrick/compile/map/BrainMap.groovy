package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import org.objectweb.asm.Opcodes

@CompileStatic
class BrainMap {
    static BrainMap parse(BrainMapParser.ProgramContext ctx) {
        return parse(ctx, null)
    }

    static BrainMap parse(BrainMapParser.ProgramContext ctx, BrainMap toAppend) {
        if (toAppend == null)
            toAppend = new BrainMap()
        ctx.type().each {
            toAppend.classes.add(parseType(it))
        }
        return toAppend
    }

    static BrainType parseType(BrainMapParser.TypeContext ctx) {
        if (ctx instanceof BrainMapParser.ClassTypeContext) {
            var type = new BrainClass()
            ctx.class_().target().each {
                type.children.add(parseBrainChild(it))
            }
            type.children.each {it.setParent(type)}
            type.type = new ObjectType(ctx.class_().name().collect {it.text})
            return type
        } else if (ctx instanceof BrainMapParser.InterfaceTypeContext) {
            var type = new BrainInterface()
            ctx.interface_().target().each {
                if (it instanceof BrainMapParser.MethodContext) {
                    type.methods.add(parseBrainMethod(it))
                } else {
                    //TODO: throw error
                }
            }
            type.methods.each {it.setParent(type)}
            type.type = new ObjectType(ctx.interface_().name().collect {it.text})
            return type
        }
    }

    static BrainChild parseBrainChild(BrainMapParser.TargetContext ctx) {
        if (ctx instanceof BrainMapParser.MethodTargetContext)
            return parseBrainMethod(ctx.method())
        else if (ctx instanceof BrainMapParser.FieldTargetContext)
            return parseBrainField(ctx.field())
        else if (ctx instanceof BrainMapParser.CtorTargetContext)
            return parseBrainCtor(ctx.ctor())
    }

    static BrainCtor parseBrainCtor(BrainMapParser.CtorContext ctx) {
        BrainCtor ctor = new BrainCtor()
        ctor.args = ctx.argName().collect {ArgType.Parser.parse(it)}
        ctor.accessModifier = Modifier.access(Modifier.parse(ctx.modifier()))
        return ctor
    }

    static BrainMethod parseBrainMethod(BrainMapParser.MethodContext ctx) {
        BrainMethod method = new BrainMethod()
        method.name = ctx.name().text
        method.args = ctx.argName().collect {ArgType.Parser.parse(it)}
        method.out = ReturnType.Parser.parse(ctx.returnName())
        method.accessModifier = Modifier.access(Modifier.parse(ctx.modifier()))
        return method
    }

    static BrainField parseBrainField(BrainMapParser.FieldContext ctx) {
        BrainField field;
        if (ctx.GET()!=null)
            field = new BrainGetter()
        else
            field = new BrainPutter()
        field.accessModifier = Modifier.access(Modifier.parse(ctx.modifier()))
        field.name = ctx.name().text
        field.type = ReturnType.Parser.parse(ctx.returnName())
        return field
    }

    List<BrainType> classes = new ArrayList<>()

    static trait BrainType {
        ObjectType type
    }
    
    static class BrainClass implements BrainType {
        List<BrainChild> children = new ArrayList<>()
    }

    static class BrainInterface implements BrainType {
        List<BrainMethod> methods = new ArrayList<>()
    }
    
    static trait BrainChild {
        private BrainType parent
        int accessModifier

        setParent(BrainType parent) {
            this.parent = parent
            if (parent instanceof BrainInterface) {
                accessModifier |= Opcodes.ACC_ABSTRACT
            }
        }

        getParent() {
            parent
        }
    }

    static class BrainCtor implements BrainChild {
        List<ArgType> args
    }

    static class BrainMethod implements BrainChild {
        String name
        ReturnType out
        List<ArgType> args
    }

    static trait BrainField extends BrainChild {
        String name
        ReturnType type
    }

    static class BrainPutter implements BrainField {
    }

    static class BrainGetter implements BrainField {
    }
}