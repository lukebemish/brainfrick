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
            var type = new BrainType()
            ctx.class_().target().each {
                type.children.add(parseBrainChild(it))
            }
            type.children.each {it.setParent(type)}
            type.type = new ObjectType(ctx.class_().name().collect {it.text})
            type.accessModifier = Modifier.access(Modifier.parse(ctx.modifier()))
            if (ctx.class_().implementDef() !== null) {
                type.interfaces = ctx.class_().implementDef().collectMany {it.classname()}.collect {new ObjectType(it.name().collect {it.text})}
            } else {
                type.interfaces = List.of()
            }
            if (ctx.class_().extendDef() !== null)
                type.parent = new ObjectType(ctx.class_().extendDef().classname().name().collect {it.text})
            else
                type.parent = new ObjectType(Object.class)
            return type
        } else if (ctx instanceof BrainMapParser.InterfaceTypeContext) {
            var type = new BrainType()
            type.isinterface = true
            ctx.interface_().target().each {
                if (it instanceof BrainMapParser.MethodContext) {
                    type.children.add(parseBrainMethod(it))
                } else if (it instanceof BrainMapParser.FieldContext) {
                    type.children.add(parseBrainField(it))
                } else {
                    //TODO: throw error
                }
            }
            type.children.each {it.setParent(type)}
            type.type = new ObjectType(ctx.interface_().name().collect {it.text})
            type.accessModifier = Modifier.access(Modifier.parse(ctx.modifier())) | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE
            if (ctx.interface_().implementDef() !== null) {
                type.interfaces = ctx.interface_().implementDef().classname().collect {new ObjectType(it.name().collect {it.text})}
            } else {
                type.interfaces = List.of()
            }
            type.parent = new ObjectType(Object.class)
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
        if (ctx.superCtor() !== null) {
            ctor.superCtor = new BrainCtor.SuperCtor()
            ctor.superCtor.args = ctx.superCtor().argName().collect {ArgType.Parser.parse(it)}
            ctor.superCtor.type = new ObjectType(ctx.superCtor().classname().name().collect {it.text})
        }
        return ctor
    }

    static BrainMethod parseBrainMethod(BrainMapParser.MethodContext ctx) {
        BrainMethod method = new BrainMethod()
        method.name = ctx.name().text
        method.args = ctx.argName().collect {ArgType.Parser.parse(it)}
        method.out = ReturnType.Parser.parse(ctx.returnName())
        method.accessModifier = Modifier.access(Modifier.parse(ctx.modifier()))
        if (ctx.superMethod() !== null) {
            method.superMethod = new BrainMethod.SuperMethod()
            method.superMethod.name = ctx.superMethod().name().text
            method.superMethod.args = ctx.superMethod().argName().collect {ArgType.Parser.parse(it)}
            method.superMethod.out = ReturnType.Parser.parse(ctx.returnName())
            method.superMethod.type = new ObjectType(ctx.superMethod().classname().name().collect {it.text})
        }
        return method
    }

    static BrainField parseBrainField(BrainMapParser.FieldContext ctx) {
        BrainField field
        if (ctx.GET()!=null)
            field = new BrainGetter()
        else
            field = new BrainPutter()
        ((BrainChild)field).setAccessModifier(Modifier.access(Modifier.parse(ctx.modifier())))
        field.name = ctx.name().text
        ThingType type = ReturnType.Parser.parse(ctx.returnName())
        if (field instanceof BrainGetter && type instanceof ReturnType)
            field.type = type
        else if (field instanceof BrainPutter && type instanceof ArgType)
            field.type = type
        return field
    }

    List<BrainType> classes = new ArrayList<>()

    static class BrainType {
        ObjectType type
        List<BrainChild> children = new ArrayList<>()
        ObjectType parent
        List<ObjectType> interfaces
        boolean isinterface = false
        int accessModifier
    }
    
    static trait BrainChild {
        private BrainType parent
        private int accessModifier

        void setParent(BrainType parent) {
            this.parent = parent
        }

        BrainType getParent() {
            return this.parent
        }

        void setAccessModifier(int accessModifier) {
            this.accessModifier = accessModifier
        }

        int getAccessModifier() {
            return this.accessModifier
        }

        boolean isStatic() {
            return (accessModifier & Opcodes.ACC_STATIC) != 0
        }
    }

    static class BrainCtor implements BrainChild {
        List<ArgType> args
        SuperCtor superCtor

        static class SuperCtor {
            List<ArgType> args
            ObjectType type
        }
    }

    static class BrainMethod implements BrainChild {
        String name
        ReturnType out
        List<ArgType> args
        SuperMethod superMethod

        static class SuperMethod {
            String name
            ReturnType out
            List<ArgType> args
            ObjectType type
        }
    }

    static trait BrainField extends BrainChild {
        String name
        abstract ThingType getType()
    }

    static class BrainPutter implements BrainField {
        public ArgType type

        @Override
        ArgType getType() {
            return type
        }
    }

    static class BrainGetter implements BrainField {
        public ReturnType type

        @Override
        ReturnType getType() {
            return type
        }
    }
}
