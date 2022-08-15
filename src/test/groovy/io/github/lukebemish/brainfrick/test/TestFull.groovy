package io.github.lukebemish.brainfrick.test

import io.github.lukebemish.brainfrick.compile.MultipleCompiler

import java.nio.file.Path

class TestFull {
    static Path OUTPATH = Path.of("build/test")
    static Path BRAINFRICK_PATH = Path.of("src/test/brainfrick")
    static Path MAP_PATH = Path.of("src/test/brainmaps")

    static void main(String[] args) {
        MultipleCompiler.compile(BRAINFRICK_PATH, MAP_PATH, OUTPATH)

        Path classPath = OUTPATH
        ClassLoader cl = new URLClassLoader(new URL[] {classPath.toUri().toURL()})
        Class<?> test1 = cl.loadClass("brainfrick.test.Test1")
        println test1.getConstructor().newInstance()
        println test1.getMethod("add",int.class,int.class).invoke(null, 3,5)
        println test1.getMethod("add2",int.class).invoke(null, 3)
    }
}
