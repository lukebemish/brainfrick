package io.github.lukebemish.brainfrick.compile.map

import groovy.transform.CompileStatic

@CompileStatic
class ObjectType implements ArgType, ReturnType {
    final List<String> names

    ObjectType(List<String> names) {
        this.names = names
    }
}
