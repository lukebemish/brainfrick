package io.github.lukebemish.brainfrick.compile.map

import org.objectweb.asm.MethodVisitor

interface ThingType {
    String getDesc()
    void castAsObject(MethodVisitor mv)
    void castTo(MethodVisitor mv)
}