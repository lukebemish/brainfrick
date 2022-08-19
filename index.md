---
layout: default
---

## What is brainfrick?

Brainfuck is an esoteric programming language based on only 8 different instructions; two to move a pointer, one each to increment or decrement the value at the pointer, two that allow for control flow, and one each for pulling input and pushing output. Brainfrick is an extension of brainfuck that can be compiled for and run on the JVM; it extends brainfuck's simple syntax with a minimal set of instructions and syntax necessary for interacting with classes.

## Using

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

## Syntax

To allow references to JVM classes and methods within a brainfuck-like environment, brainfrick requires the user to define any number of `brainmaps`.
These are ordered lists of classes, methods, and field getters or setters. They take a form such as shown below:
```brainmap
// At index 0, we have the class TestFunction that implements Function.
public class brainfrick.test.TestFunction implements java.util.function.Function
  // At index 1, we define a method on that class. We can give methods any number of modifiers,
  // including most of those you're familiar with from java - "public", "private", "abstract",
  // and the like
  public java.lang.Object apply(java.lang.Object)
  // Constructors are defined similarly. The "->" syntax defines the super method or constructor,
  // and is optional in methods.
  public new() -> java.lang.Object new()
// Any class referenced for a super method or constructor must be present elsewhere in the map.
class java.lang.Object
abstract public class brainfrick.test.TestField
  // Field getters are defined with the "get" keyword:
  get java.lang.Object storedObject
  // And can be made static as well:
  static get java.lang.String staticString
  // Setters are defined with "put":
  put java.lang.Object storedObject
  abstract java.lang.Object someMethod()
```

Brainmaps are referenced at the beginning of a brainfrick source file, and are integrated into classes at compile time. For instance, if the previous
map was saved as `brainmap.map`, a corresponding brainfrick source file might be:
```brainfrick
// Brainmaps to use are declared before anything else. Any number can be declared; they will
// be appended together at compile time.
"brainmap.map"
// This syntax opens a class definition - in this case, the class at index 0
;{
    // And this opens up a method definition
    ;{
        // Your code implementing "apply" would go here
    }
    // Methods and constructors are declared the same way
    ;{
        ;
    }
}
// A "-" tells the compiler to skip this class or method - in this case, we don't want to redefine Object!
-
;{
    // A ";" on its own for a method definition defines, but skips, an abstract method.
    ;
}
```

The code itself is an extended version of normal brainfuck syntax:

| Command | Description                                                                                 |
| :-------| :------------------------------------------------------------------------------------------ |
| `>`     | Moves the pointer one to the right. Pointer locations can be any integer value. |
| `<`     | Moves the pointer one to the left |
| `+`     | Increments the pointed to location by one. Numbers will be increased by one, `null` will become `1`, `false` will become `true`, and objects that implement `Incrementable` will be incremented. |
| `-`     | Decrements the pointed to location by one. Numbers will be decreased by one, `null` will become `-1`, `true` will become `false`, and objects that implement `Decrementable` will be decremented. |
| `[`     | If the value at the pointer is zero-like (zero, `false`, `null`, or a `Zeroable` where `isZero` is true), jumps to after the matching `]` instruction. |
| `]`     | If the value at the pointer is not zero-like, jumps to after the matching `[` instruction. |
| `,`     | Using the value at the pointer as an index, gets an argument of the method and fills the pointed cell with it. |
| `.`     | Pushes the value at the pointed cell to the buffer. |
| `/`     | Returns the method early; for non-`void` methods, whether returned early or at the end of execution, the top value of the buffer will be returned. Methods that return `String` will return the top value if it is a `String` or `null`, and use the whole buffer to construct a `char` array and then a `String` otherwise. |
| `:`     | {::nomarkdown}Using the value at the pointer as an index, invokes the corresponding value from the declared brainmaps using the top of the buffer as arguments, if necessary. The cell is set to:<br><ul><li>The corresponding <code>Class</code> object, if the index corresponds to a class. <li>The returned object, if the index is a non-`void` method, constructor, or field getter. <li><code>null</code>, if the index is a <code>void</code> method or field putter.</ul>{:/nomarkdown} |
| `;`     | Invokes the super method or constructor for this method using the top of the buffer. Will always be called at least one in constructors; in constructors, the `this` argument will not be consumed, but is necessary in methods.                                                                                                                                                                                                  |

All cells start out with a value of `null`. All characters other than those with specific meanings are ignored; however, it is recommended to use comments for other non-alphanumeric characters, in case the syntax ever has to be expanded further. Single-line and multi-line comments work the same as in java, with `//` and `/* ... */` respectively.
