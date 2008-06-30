grammar Array;

//AC: keep the lexer and the parser together for now because that's the default Antlr layout

@parser::header {
package matlab;
}

@lexer::header {
package matlab;
}

array :
     matrix
  |  cell_array
  ;

//input parameter list for a function
input_params :
     LPAREN nbf* param_list? RPAREN nbf*
  ;

//Non-empty, comma-separated list of parameters (note: no trailing comma)
//shared by input and output parameters
param_list :
     name (COMMA nbf* name)*
  ;

name :
     IDENTIFIER nbf*
  ;

//precedence from: http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
expr :
     logic_expr
  |  AT nbf* input_params expr
  ;

logic_expr :
     colon_expr ((LT | GT | LE | GE | EQ | NE | AND | OR | SHORTAND | SHORTOR) nbf* colon_expr)*
  ;

colon_expr :
     binary_expr (COLON nbf* binary_expr (COLON nbf* binary_expr)?)?
  ;

tricky_expr :
     binary_expr ((PLUS | MINUS) nbf* binary_expr)*
  ;

binary_expr :
     prefix_expr ((MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV) nbf* prefix_expr)*
  ;

prefix_expr :
     pow_expr
  |  NOT nbf* prefix_expr
  |  PLUS nbf* prefix_expr
  |  MINUS nbf* prefix_expr
  ;

pow_expr :
     (postfix_expr) ((MPOW | EPOW) nbf* postfix_expr)*
  ;

postfix_expr :
     (primary_expr) (ARRAYTRANSPOSE nbf* | MTRANSPOSE nbf*)*
  ;

primary_expr :
     literal
  |  LPAREN nbf* expr RPAREN nbf*
  |  matrix
  |  cell_array
  |  access
  |  AT nbf* name
  |  name AT nbf* name input_params?
  ;

access :
     cell_access (LPAREN nbf* arg_list? RPAREN nbf*)?
  ;

cell_access :
     (var_access) (LCURLY nbf* arg_list RCURLY nbf*)*
  ;

arg_list :	
     (arg) (COMMA nbf* arg)*
  ;
  
arg :
     expr
  |  COLON nbf*
  ;

var_access :
     (name) (DOT nbf* name)*
  ;

literal :
     NUMBER nbf*
  |  STRING nbf*
  ;

matrix :
     LSQUARE nbf* optional_row_list RSQUARE nbf*
  ;

cell_array :
     LCURLY nbf* optional_row_list RCURLY nbf*
  ;

optional_row_list :
     row_list? row_separator*
  ;

row_list :
     (row) (row_separator+ row)*
  ;

row :
     element_list (element_separator+)?
  ;

row_separator :
     LINE_TERMINATOR nbf*
  |  SEMICOLON nbf*
  |  comment LINE_TERMINATOR
  ;

element_list :
     (element) (element_separator+ element)*
  ;
  
element :
     expr
  ;

element_separator :
     COMMA
  //|  nbf
  ;

comment :
     COMMENT
  |  BRACKET_COMMENT
  ;

nbf :
     OTHER_WHITESPACE
  |  ELLIPSIS_COMMENT
  ;

fragment LETTER : 'a'..'z' | 'A'..'Z';
fragment DIGIT : '0'..'9';
fragment HEX_DIGIT : DIGIT | 'a'..'f' | 'A'..'F';
IDENTIFIER : ('_' | '$' | LETTER) ('_' | '$' | LETTER | DIGIT)*;
fragment HEX_NUMBER : HEX_DIGIT+;
fragment SCI_EXP : ('e' | 'E') ('+' | '-')? DIGIT+;
fragment FP_NUMBER : (DIGIT+ '.' DIGIT*) | ('.' DIGIT+) SCI_EXP;
NUMBER : (HEX_NUMBER | FP_NUMBER) ('i' | 'I' | 'j' | 'J')?;

BRACKET_COMMENT : '%{%}';

fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
COMMENT : '%' NOT_LINE_TERMINATOR*;
ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;

STRING : '\'\'';

PLUS : '+';
MINUS : '-';
MTIMES : '*';
ETIMES : '.*';
MDIV : '/';
EDIV : './';
MLDIV : '\\';
ELDIV : '.\\';
MPOW : '^';
EPOW : '.^';
MTRANSPOSE : '\'';
ARRAYTRANSPOSE : '.\'';
LE : '<=';
GE : '>=';
LT : '<';
GT : '>';
EQ : '==';
NE : '~=';
AND : '&';
OR : '|';
NOT : '~';
SHORTAND : '&&';
SHORTOR : '||';
DOT : '.';
COMMA : ',';
SEMICOLON : ';';
LINE_TERMINATOR : '\r' '\n' | '\r' | '\n';
COLON : ':';
AT : '@';
OTHER_WHITESPACE : ' ' | '\t' | '\f';
LPAREN : '(';
RPAREN : ')';
LCURLY : '{';
RCURLY : '}';
LSQUARE : '[';
RSQUARE : ']';
