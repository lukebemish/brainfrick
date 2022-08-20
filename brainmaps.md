---
layout: default
title: "Brainmap Specification"
permalink: /spec/brainmap
---

## Brainmap Specification

Brainmaps are the means by which brainfrick code can access methods, classes,
fields, constructors, and other such object oriented aspects of the JVM. The
brainmap contains a series of entries (generally one per line, though the parser
ignores all whitespace), indexed from 0 onwards. By using the `:` operator in a
number with an integer-like value, the corresponding member of the brainmaps the
code is compiled against is called, with the top values of the buffer. For
instance, if the buffer consisted of `1, 2, 4, 7, 3`, with `3` being the most
recently added value, and the `:` operator was called invoking a brainmap member
that expects two inputs, it would be given the inputs `7` and `3`, in that order.

Brainmaps define both what the code can call and what classes, methods, and fields
it can define using the `;` and `;{ ... }` syntax. Each top-level
`;{` in a `.frick` file corresponds with a class from the brainmap or brainmaps
provided to the compiler; each inner `;` or `;{` corresponds to a method or constructor
in the class. If you wish to make a method abstract, it must be given the
`abstract` keyword in the brainmap and must not have a body defined in code; this
can be done by using a `;` without brackets. To skip the definition of a method
or class, use a `-`. Fields are defined by default if their class is defined, and
cannot be skipped.

## Entries

The entries of a brainmap can take the following forms:

### Class
```brainmap
public class fully.qualified.Name
```
Classes can be given the `public`, `abstract`, or `final` modifiers. They are
considered to be package-private unless otherwise specified. Classes you are not
defining but only calling or accessing do not need access modifiers specified.

Invoking a class from the brainmap places the corresponding `Class` object at the
pointer.

Classes can be made to extend or implement other classes or interfaces much as in
java, using the `extends` and `implements` keywords with the fully qualified names
of the super classes or interfaces:
```brainmap
public class fully.qualified.Name implements java.util.function.Function, java.util.function.Consumer
        extends java.util.AbstractList
```
Classes extend `java.lang.Object` unless otherwise specified.

### Interface
```brainmap
public interface fully.qualified.Name
```
Interfaces can be given the `public` modifier. They are
considered to be package-private unless otherwise specified. Interfaces you are not
defining but only calling or accessing do not need access modifiers specified.

Invoking an interface from the brainmap places the corresponding `Class` object at the
pointer. 

Similar to classes, interfaces can be made to extend other interfaces using the
`extends` keyword:
```brainmap
public interface fully.qualified.Name extends java.util.List
```

### Method
```brainmap
public class fully.qualified.Name extends fully.qualified.NameSuper
    protected final void methodName(java.lang.String, int, char)
    public void callMethod() -> fully.qualified.NameSuper callMethod()
class fully.qualified.NameSuper
```

Methods can be given the `public`, `protected`, `private`, `final`, `static`, or
`abstract` modifiers. They are package-private unless otherwise specified. Methods
you are not defining but only calling do not need access modifiers specified.
Methods will be defined in the last class listed in the brainmap prior to the
method declaration. They are defined with comma-separated arguments taking the
form of either a fully qualified class name or one of `int`, `short`, `byte`,
`char`, `long`, `float`, `double`, or `boolean`. The return type can be anything
the argument typs can be, with the addition of `void`.

Invoking a method from the brainmap consumes the same number of values as
the method has arguments, plus one if the method is non-static. These arguments
are consumed in order, with the instance for non-static methods being first. The
return value is placed at the pointer; if the method returns `void`, then `null`
is placed at the pointer.

The `->` syntax can be used to define a super method of the method; this does not
change what method the declared method overrides (as that is determined by the signature),
but it does allow the use of the `;` operator to call the super method using values
from the buffer. The instance that the method is being called on must be included
on the buffer in such a super call. The class the super method is taken from
*must* be present elsewhere in the brainmap.

### Field Getters and Setters
```brainmap
public class fully.qualified.Name
    get static final java.util.List LIST
    put int i
```
Field getters and setters can be given the `public`, `protected`, `private`,
or `static` modifiers. They are package-private unless otherwise specified.
Currently, fields being declared cannot be final, as there is no way to
ensure that the fields are properly initialized. Accessing final fields, however,
should work fine.
Fields you are not defining but only using do not need access modifiers specified.
Fields will be defined if either a getter or setter (or both) is present in the
last class listed in the brainmap prior to the field declaration, and if that
class is being defined. Their return type can be a fully qualified class name or
a primitive type, the same as method arguments.

Invoking a static field getter places the value of the static field at the pointer.

Invoking a non-static field getter consumes an instance and places the value of
the field at the pointer.

Invoking a static field setter consumes a value and sets the field to that value,
placing `null` at the pointer.

Invoking a non-static field setter consumes an instance and a value and sets the
field on that instance to the provided value, placing `null` at the pointer.

### Constructors
```brainmap
public class fully.qualified.Name
    protected new(float) -> java.lang.Object new()
class java.lang.Object
```
Constructors can be given the `public`, `protected`, or `private` modifiers.
They are package-private unless otherwise specified. Constructors you are not
defining but only calling do not need access modifiers specified. Constructors
will be defined in the last class listed in the brainmap prior to the constructor
declaration. They are defined with comma-separated arguments taking the same
possible types as method arguments.

Invoking a constructor from the brainmap consumes the same number of values as
the constructor has arguments. These arguments are consumed in order, and the
constructed object is placed at the pointer.

The `->` syntax can be used to define a super constructor of the constructor;
this allows the use of the `;` operator to call the super constructor using values
from the buffer. The instance that the method is being called on *cannot* be included
on the buffer in such a super call. Unlike for methods, this syntax is not optional;
every constructor must call some super constructor. The class the super constructor
is taken from *must* be present elsewhere in the brainmap.

### Annotations
```brainmap
@?fully.qualified.AnnotationName(string="String", enumVal=fully.qualified.EnumName#VALUE)
public class fully.qualified.Name extends fully.qualified.SuperName
    @java.lang.Deprecated(since="1.0", forRemoval=true)
    protected new(float) -> java.lang.Object new()
    @?org.jetbrains.annotations.ApiStatus$Experimental
    get int intField
    @javax.annotation.Nullable
    public void someMethod(@javax.annotation.Nonnull java.lang.String)
class java.lang.Object
```
All brainmap entries, as well as method or constructor parameters, can be annotated.
These annotations should use `@` for annotations which ought to be runtime-visible,
and `@?` for annotations which should be in the class file but invisible at runtime.
Annotations may optionally take a comma-separated list of name-value pairs, with
values of the following forms:

| Type        | Example                                                                 |
| :------     | :---------------------------------------------------------------------- |
| int         | `1` or `1i`                                                             |
| short       | `3s`                                                                    |
| byte        | `4b`                                                                    |
| char        | `'a'`                                                                   |
| long        | `-2l`                                                                   |
| float       | `3.5f`                                                                  |
| double      | `2.0` or `2.0d`                                                         |
| String      | `"Hello, world!"`                                                       |
| Class       | `java.lang.Object`                                                      |
| Enum Value  | `fully.qualified.EnumName#ENUM_VALUE`                                   |
| Annotation  | `@fully.qualified.AnnotationName...` (same as the outer notation)       |
| Array       | `{1,2,3}` (Can be of any of the types above, though not a nested array) |

