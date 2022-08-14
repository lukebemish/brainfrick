package io.github.lukebemish.brainfrick.test

import io.github.lukebemish.brainfrick.compile.BrainMapCompiler
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapLexer
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import io.github.lukebemish.brainfrick.compile.map.BrainMap
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.UnbufferedTokenStream
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.nio.file.Files
import java.nio.file.Path

class TestBrainMap {
    static String MAP = """
class java.lang.Object
new ()
int hashCode()
boolean equals(Object)
"""
    static String OUTNAME = "brainfrick/test/Test1"
    static Path OUTPATH = Path.of("build/test/",OUTNAME+".class")

    static void main(String[] args) {
        BrainMap map = BrainMap.parse(new BrainMapParser(new UnbufferedTokenStream<>(new BrainMapLexer(CharStreams.fromString(MAP)))).program())
        BrainMapCompiler compiler = new BrainMapCompiler()
        compiler.classname = OUTNAME
        compiler.cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES)
        compiler.cw.visit(Opcodes.V17,Opcodes.ACC_PUBLIC,OUTNAME,null,Object.class.name.replace('.','/'),new String[]{})

        compiler.writeBrainMap(map)
        compiler.cw.visitEnd()
        Files.createDirectories(OUTPATH.getParent())
        Files.write(OUTPATH, compiler.cw.toByteArray())
    }
}
