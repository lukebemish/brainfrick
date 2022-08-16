grammar BrainMap;

@header {
package io.github.lukebemish.brainfrick.compile.grammar;
}

program     : type* ;
type        : annotation* modifier* interface_ #interfaceType
            | annotation* modifier* class_ #classType
            ;

interface_   : INTERFACE name ('.' name)* (EXTENDS implementDef)? target* ;
class_       : CLASS name ('.' name)* (IMPLEMENTS implementDef)? (EXTENDS extendDef)? (IMPLEMENTS implementDef)? target* ;

extendDef   : classname ;
implementDef: classname (',' classname)* ;

target      : ctor #ctorTarget
            | method #methodTarget
            | field #fieldTarget
            ;

ctor        : annotation* modifier* NEW '(' (annotArg (',' annotArg)*)? ')' superCtor? ;
method      : annotation* modifier* returnName name '(' (annotArg (',' annotArg)*)? ')' superMethod? ;
field       : annotation* modifier* (GET | PUT) returnName name ;

superCtor   : '->' classname NEW '('(argName (',' argName)*)?')' ;
superMethod : '->' classname returnName name '('(argName (',' argName)*)?')' ;

classname   : name ('.' name)* ;

modifier    : PROTECTED
            | PRIVATE
            | PUBLIC
            | STATIC
            | FINAL
            | ABSTRACT
            ;

annotArg    : annotation* argName ;

argName     : primitive '[]'* #primitiveArg
            | name ('.' name)* '[]'* #objArg
            ;

returnName  : primitive '[]'* #primitiveOut
            | VOID #voidOut
            | name ('.' name)* '[]'* # objOut
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
            | EXTENDS
            | IMPLEMENTS
            | ABSTRACT
            | TRUE
            | FALSE
            | NAME
            ;

// Annotation
annotation  : '@' classname ('(' (assignment (',' assignment)* )? ')')? ;
assignment  : name '=' assignValue ;

assignValue : simpleValue #annotationSimple
            | '{' (simpleValue (',' simpleValue)*)? '}' #annotationArray
            ;
simpleValue : STRING #annotationString
            | DEC_FLOAT #annotationFloat
            | DEC_INT #annotationInt
            | CHAR_LITERAL #annotationChar
            | DEC_SHORT #annotationShort
            | DEC_BYTE #annotationByte
            | DEC_DOUBLE #annotationDouble
            | DEC_LONG #annotationLong
            | bool #annotationBoolean
            | classname #annotationClass
            | annotation #annotationAnnotation
            | classname '#' name #annotationEnum
            ;

bool        : (TRUE | FALSE) ;

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
ABSTRACT
    : 'abstract' ;
NEW
    : 'new' ;
EXTENDS
    : 'extends' ;
IMPLEMENTS
    : 'implements' ;

COMMA
    : ',' ;
DOT
    : '.' ;
LPAREN
    : '(' ;
RPAREN
    : ')' ;
SUPER
    : '->' ;
ARRAY
    : '[]' ;
ANNOTATE
    : '@' ;
ASSIGN
    : '=' ;
LCURLY
    : '{' ;
RCURLY
    : '}' ;
GETTER
    : '#' ;

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
TRUE
    : 'true' ;
FALSE
    : 'false' ;

NAME
    : Letter LetterDigit* ;

STRING
    : '"' (StrEsc | ~["\\])* '"' ;
DEC_INT
    : DecInt 'i'? ;
DEC_LONG
    : DecInt 'l' ;
DEC_BYTE
    : DecInt 'b' ;
DEC_SHORT
    : DecInt 's' ;
CHAR_LITERAL
    : '\'' (StrEsc | ~['\\\r\n]) '\'' ;
DEC_FLOAT
    : DecFloat 'f' ;
DEC_DOUBLE
    : DecFloat 'd'? ;
DecInt
    : ('+' | '-')? Non0Digit ('_'? Digit)* ;
DecFloat
    : ('+' | '-')? ( Digit ('_'? Digit)* ('.' ('_'? Digit)*)
                   | ('.' Digit ('_'? Digit)*)) ;

LINE_BREAK
    : ('\r'? '\n' | '\r')+ -> skip;
LINE_COMMENT
    : '//' ~[\r\n]* -> skip ;
MULTI_COMMENT
    : '/*' .*? '*/' -> skip ;
WS
    : [ \t\u000C]+ -> skip ;

fragment StrEsc
    : '\\' [btnfr"'\\`]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit ;

fragment Letter
    : [a-zA-Z$_] | ~[\u0000-\u007F\uD800-\uDBFF] | [\uD800-\uDBFF] [\uDC00-\uDFFF] ;
fragment LetterDigit
    : (Letter | [0-9] ) ;
fragment HexDigit
    : [0-9a-fA-F] ;
fragment Digit
    : [0-9] ;
fragment Non0Digit
    : [1-9] ;