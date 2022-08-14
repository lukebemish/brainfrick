package io.github.lukebemish.brainfrick.test

import io.github.lukebemish.brainfrick.compile.MultipleCompiler

import java.nio.file.Path

class TestFull {
    static Path OUTPATH = Path.of("build/test")
    static Path BRAINFRICK_PATH = Path.of("src/test/brainfrick")
    static Path MAP_PATH = Path.of("src/test/brainmaps")

    static void main(String[] args) {
        MultipleCompiler.compile(BRAINFRICK_PATH, MAP_PATH, OUTPATH)
    }
}
