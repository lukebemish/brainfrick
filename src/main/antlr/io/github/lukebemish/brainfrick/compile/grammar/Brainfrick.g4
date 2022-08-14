grammar Brainfrick;

@header {
package io.github.lukebemish.brainfrick.compile.grammar;
}

program     : MAP* class* ;

class       : DECL '{' method* '}' ;
method      : DECL '{' code* '}' ;

code        : cond | instr ;

cond        : BEGIN code* END ;

instr       : PINCR #pincr
            | PDECR #pdecr
            | INCR #incr
            | DECR #decr
            | PULL #pull
            | PUSH #push
            | OPERATE #operate
            | RETURN #return
            ;

MAP
    : '"' (StrEsc | ~["\\])* '"' ;

PINCR:      '>' ;
PDECR:      '<' ;
INCR:       '+' ;
DECR:       '-' ;
BEGIN:      '[' ;
END:        ']' ;
PULL:       ',' ;
PUSH:       '.' ;
DECL:       ';' ;
OPERATE:    ':' ;
OPEN:       '{' ;
CLOSE:      '}' ;
RETURN:     '/' ;

MULTI_COMMENT
    : '/*' .*? '*/' -> skip ;
UNKNOWN
    : .+? -> skip ;


fragment StrEsc
    : '\\' [btnfr"'\\`]
    | '\\' ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit ;

fragment HexDigit
    : [0-9a-fA-F] ;