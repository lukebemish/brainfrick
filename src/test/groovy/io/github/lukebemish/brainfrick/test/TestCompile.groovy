package io.github.lukebemish.brainfrick.test

import io.github.lukebemish.brainfrick.compile.Compiler
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapLexer
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import io.github.lukebemish.brainfrick.compile.grammar.BrainfrickLexer
import io.github.lukebemish.brainfrick.compile.grammar.BrainfrickParser
import io.github.lukebemish.brainfrick.compile.map.BrainMap
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.UnbufferedTokenStream

import java.nio.file.Files
import java.nio.file.Path

class TestCompile {
    static String MAP = """
public class brainfrick.test.Test1
static public int add(int,int)
static public int add2(int)
"""
    static String CODE = """
"brainmap.map"
;{
    ;{
        ,>+,<[->+<]>.
    }
    ;{
        ,.>++.>++:.
    }
}
"""
    static Path OUTPATH = Path.of("build/test")

    static void main(String[] args) {
        BrainMap map = BrainMap.parse(new BrainMapParser(new UnbufferedTokenStream<>(new BrainMapLexer(CharStreams.fromString(MAP)))).program())

        BrainfrickParser.ProgramContext ctx = new BrainfrickParser(new UnbufferedTokenStream<>(new BrainfrickLexer(CharStreams.fromString(CODE)))).program()

        Files.createDirectories(OUTPATH.getParent())

        Compiler compiler = new Compiler()
        compiler.map = map
        compiler.outdir = OUTPATH

        ctx.class_().each {
            compiler.parseClass(it)
        }
    }
}
