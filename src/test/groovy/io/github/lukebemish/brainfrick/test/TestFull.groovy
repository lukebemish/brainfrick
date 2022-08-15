package io.github.lukebemish.brainfrick.test

import io.github.lukebemish.brainfrick.compile.MultipleCompiler

import java.nio.file.Path
import java.util.function.Function

class TestFull {
    static Path OUTPATH = Path.of("build/test")
    static Path BRAINFRICK_PATH = Path.of("src/test/brainfrick")
    static Path MAP_PATH = Path.of("src/test/brainmaps")

    static void main(String[] args) {
        MultipleCompiler.compile(BRAINFRICK_PATH, MAP_PATH, OUTPATH)

        Path classPath = OUTPATH
        ClassLoader cl = new URLClassLoader(new URL[] {classPath.toUri().toURL()})
        println "TestAdd:"
        Class<?> testAdd = cl.loadClass("brainfrick.test.TestAdd")
        println testAdd.getConstructor().newInstance()
        println testAdd.getMethod("add",int.class,int.class).invoke(null, 3,5)
        println testAdd.getMethod("add2",int.class).invoke(null, 3)
        println "TestExtend:"
        Class<?> testExtend = cl.loadClass("brainfrick.test.TestExtend")
        Function newFunction = testExtend.getConstructor().newInstance() as Function
        println newFunction.apply(5)
        println "TestMain:"
        Class<?> testMain = cl.loadClass("brainfrick.test.TestMain")
        testMain.getMethod("main", String.class.arrayType()).invoke(null, new Object[]{new String[]{}})
    }
}
