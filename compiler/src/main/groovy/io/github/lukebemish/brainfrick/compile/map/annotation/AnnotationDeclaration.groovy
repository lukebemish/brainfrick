package io.github.lukebemish.brainfrick.compile.map.annotation

import groovy.transform.CompileStatic
import groovy.transform.ImmutableOptions
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import io.github.lukebemish.brainfrick.compile.map.ObjectType
import org.objectweb.asm.AnnotationVisitor

@CompileStatic
@ImmutableOptions(knownImmutableClasses = [ObjectType])
record AnnotationDeclaration(ObjectType type, Map<String,AnnotationValue> values, boolean runtime) implements AnnotationValue.SimpleAnnotationValue {
    static AnnotationDeclaration parse(BrainMapParser.AnnotationContext ctx) {
        ObjectType type = new ObjectType(ctx.classname().name().collect {it.text})
        Map<String, AnnotationValue> values = new HashMap<>()
        ctx.assignment().each {values.put(it.name().text, AnnotationValue.Parser.parse(it.assignValue()))}
        return new AnnotationDeclaration(type, values, ctx.RUNTIME_HIDDEN_ANNOTATE()==null)
    }

    @Override
    void visitParameter(AnnotationVisitor av, String param) {
        var newAv = av.visitAnnotation(param, type().desc)
        values().each {key,value ->
            value.visitParameter(newAv,key)
        }
        newAv.visitEnd()
    }
}