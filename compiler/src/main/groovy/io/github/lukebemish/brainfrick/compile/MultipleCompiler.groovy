package io.github.lukebemish.brainfrick.compile

import groovy.transform.CompileStatic
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapLexer
import io.github.lukebemish.brainfrick.compile.grammar.BrainMapParser
import io.github.lukebemish.brainfrick.compile.grammar.BrainfrickLexer
import io.github.lukebemish.brainfrick.compile.grammar.BrainfrickParser
import io.github.lukebemish.brainfrick.compile.map.BrainMap
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.UnbufferedTokenStream
import org.apache.commons.text.StringEscapeUtils

import java.nio.file.Files
import java.nio.file.Path

@CompileStatic
class MultipleCompiler {
    static final String BRAINFRICK_EXTENSION = "frick"

    static void compile(Path inputBrainfrick, Path outputdir) {
        List<Path> bfPaths = Files.walk(inputBrainfrick).filter({
            !Files.isDirectory(it) && it.getName(it.nameCount-1).toString().toLowerCase(Locale.ROOT).endsWith(BRAINFRICK_EXTENSION)
        }).toList()
        compile(bfPaths, outputdir)
    }

    static void compile(List<Path> inputBrainfrick, Path outputDir) {
        for (Path path : inputBrainfrick) {
            BrainfrickParser.ProgramContext ctx = new BrainfrickParser(new UnbufferedTokenStream<>(new BrainfrickLexer(CharStreams.fromPath(path)))).program()
            List<String> maps = ctx.MAP().collect { StringEscapeUtils.unescapeJava(it.text.substring(1,it.text.size()-1))}
            BrainMap brainMap = new BrainMap()
            for (String mapPath : maps) {
                Path p = path.parent.resolve(mapPath)
                if (Files.exists(p)) {
                    brainMap = BrainMap.parse(new BrainMapParser(new UnbufferedTokenStream<>(new BrainMapLexer(CharStreams.fromPath(p)))).program(), brainMap)
                }
            }
            Compiler compiler = new Compiler()
            compiler.outdir = outputDir
            compiler.map = brainMap
            ctx.class_().each {
                if (it instanceof BrainfrickParser.ActualClassContext) {
                    compiler.parseClass(it)
                } else if (it instanceof BrainfrickParser.SkipClassContext) {
                    compiler.skipClass()
                }
            }
        }
    }
}
