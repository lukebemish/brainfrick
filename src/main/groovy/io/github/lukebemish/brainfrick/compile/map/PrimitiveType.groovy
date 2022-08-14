package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic

@CompileStatic
enum PrimitiveType implements ArgType, ReturnType {
    INT,
    SHORT,
    BYTE,
    CHAR,
    LONG,
    FLOAT,
    DOUBLE
}