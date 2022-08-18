# brainfrick

A modified brainfuck implementation that can compile to JVM bytecode, and allow for object-oriented programming.

## Using
[![Docs](https://shields.io/badge/-docs-informational?style=for-the-badge)](https://lukebemish.github.io/brainfrick/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lukebemish.brainfrick/brainfrick-runtime?style=for-the-badge)](https://search.maven.org/artifact/io.github.lukebemish.brainfrick/brainfrick-runtime)
[![javadoc](https://javadoc.io/badge2/io.github.lukebemish.brainfrick/brainfrick-runtime/javadoc.svg?style=for-the-badge&prefix=v)](https://javadoc.io/doc/io.github.lukebemish.brainfrick/brainfrick-runtime)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.lukebemish.brainfrick?style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.lukebemish.brainfrick)

Brainfrick provides a gradle plugin for compiling brainfrick code, and the runtime libraries are available on maven central. To use, apply the plugin and add the runtime libraries as a dependency:

```gradle
plugins {
    id 'io.github.lukebemish.brainfrick' version '<plugin-version>'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.lukebemish.brainfrick:brainfrick-runtime:<version>'
}
```

Brainfrick code can be written in `.frick` files inside of the `brainfrick` folder within a source set: for instance, `src/main/brainfrick/helloWorld.frick`. Brainmaps are referenced from `.frick` files by relative path, and should have the `.map` extension.
