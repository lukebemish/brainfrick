grammar BrainMap;

@header {
package io.github.lukebemish.brainfrick.compile.grammar;
}

program     : type* ;
type        : modifier* interface #interfaceType
            | modifier* class #classType
            ;

interface   : INTERFACE name ('.' name)* target* ;
class       : CLASS name ('.' name)* target* ;

target      : ctor #ctorTarget
            | method #methodTarget
            | field #fieldTarget
            ;

ctor        : modifier* NEW '(' (argName (',' argName)*)? ')' ;
method      : modifier* returnName name '(' (argName (',' argName)*)? ')' ;
field       : modifier* (GET | PUT) returnName name ;

modifier    : PROTECTED
            | PRIVATE
            | PUBLIC
            | STATIC
            | FINAL
            ;


argName     : primitive #primitiveArg
            | name ('.' name)* # objArg
            ;

returnName  : primitive #primitiveOut
            | VOID #voidOut
            | name ('.' name)* # objOut
            ;

primitive   : INT
            | SHORT
            | BYTE
            | CHAR
            | LONG
            | FLOAT
            | DOUBLE
            | BOOLEAN
            ;


name        : CLASS
            | INTERFACE
            | PUBLIC
            | PRIVATE
            | PROTECTED
            | FINAL
            | GET
            | PUT
            | STATIC
            | NEW
            | NAME
            ;

CLASS
    : 'class' ;
INTERFACE
    : 'interface' ;
PUBLIC
    : 'public' ;
PRIVATE
    : 'private' ;
PROTECTED
    : 'protected' ;
FINAL
    : 'final' ;
GET
    : 'get' ;
PUT
    : 'put' ;
STATIC
    : 'static' ;
NEW
    : 'new' ;

COMMA
    : ',' ;
DOT
    : '.' ;
LPAREN
    : '(' ;
RPAREN
    : ')' ;

BOOL
    : 'bool' ;
INT
    : 'int' ;
LONG
    : 'long' ;
FLOAT
    : 'float' ;
DOUBLE
    : 'double' ;
CHAR
    : 'char' ;
SHORT
    : 'short' ;
BYTE
    : 'byte' ;
BOOLEAN
    : 'boolean' ;
VOID
    : 'void' ;

NAME
    : Letter LetterDigit* ;

LINE_BREAK
    : ('\r'? '\n' | '\r')+ -> skip;
LINE_COMMENT
    : '//' ~[\r\n]* -> skip ;
MULTI_COMMENT
    : '/*' .*? '*/' -> skip ;
WS
    : [ \t\u000C]+ -> skip ;


fragment Letter
    : [a-zA-Z$_] | ~[\u0000-\u007F\uD800-\uDBFF] | [\uD800-\uDBFF] [\uDC00-\uDFFF] ;
fragment LetterDigit
    : (Letter | [0-9] ) ;