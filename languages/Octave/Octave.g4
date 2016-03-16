grammar Octave;

@header{
    import java.util.*;
}

@lexer::members {
private static boolean isPreTransposeChar(int ch) {
    switch(ch) {
    case ')':
    case '}':
    case ']':
    case '_': //at the end of an identifier
    case '\'': //at the end of a transpose
               //NB: if this was at the end of a string, it would have been an escape instead of a close quote
        return true;
    default:
        return Character.isLetterOrDigit(ch); //at the end of an identifier
    }
}

private boolean couldBeFieldName = false;

}

@parser::members{

private static boolean isBinaryOperator(Token op) {
    switch(op.getType()) {
    case AT:
    case COLON:
    case DOT:
    case PLUS:
    case MINUS:
    case MTIMES:
    case ETIMES:
    case MDIV:
    case EDIV:
    case MLDIV:
    case ELDIV:
    case MPOW:
    case EPOW:
    case LE:
    case GE:
    case LT:
    case GT:
    case EQ:
    case NE:
    case AND:
    case OR:
    case SHORTAND:
    case SHORTOR:
        return true;
    default:
        return false;
    }
}

private static boolean isPrefixOperator(Token op) {
    switch(op.getType()) {
    case PLUS:
    case MINUS:
    case NOT:
    case AT:
        return true;
    default:
        return false;
    }
}

private static boolean isPostfixOperator(Token op) {
    switch(op.getType()) {
    case MTRANSPOSE:
    case ARRAYTRANSPOSE:
        return true;
    default:
        return false;
    }
}

private static boolean isLParen(Token op) {
    return op.getType() == LPAREN;
}

private static boolean isRParen(Token op) {
    return op.getType() == RPAREN;
}


/*
 * Key helper method: used to determine whether or not a FILLER token is meant
 * to be a column delimiter.
 */
private boolean isElementSeparator() {
    if(!(inCurly() || inSquare())) {
        return false;
    }
    Token prevToken = getTokenStream().LT(-1);
    Token nextToken = getTokenStream().LT(2); //2, not 1 because we haven't matched the FILLER yet
    switch(nextToken.getType()) {
    case PLUS:
    case MINUS:
        if(getTokenStream().LA(3) != FILLER) {
            return true;
        }
        break;
    case AT:
        return true;
    }
    return !(isBinaryOperator(prevToken) || isBinaryOperator(nextToken) ||
             isPrefixOperator(prevToken) || isPostfixOperator(nextToken) ||
             isLParen(prevToken) || isRParen(nextToken));
}

//flag so that Antlr doesn't test all fillers with isCompoundStmtHeaderSeparator()
private boolean wantCompoundStmtHeaderSeparator = false;

private boolean isCompoundStmtHeaderSeparator() {
    if(!wantCompoundStmtHeaderSeparator || getTokenStream().LA(1) != FILLER) {
        return false;
    }
    Token prevToken = getTokenStream().LT(-1);
    Token nextToken = getTokenStream().LT(2); //2, not 1 because we haven't matched the FILLER yet
    return !(isBinaryOperator(prevToken) || isBinaryOperator(nextToken) ||
             isPrefixOperator(prevToken) || isPostfixOperator(nextToken) ||
             isLParen(prevToken) || isRParen(nextToken));
}

private final java.util.Stack<Integer> bracketStack = new java.util.Stack<Integer>();
private boolean inParens() { return !bracketStack.isEmpty() && bracketStack.peek() == LPAREN; }
private boolean inCurly() { return !bracketStack.isEmpty() && bracketStack.peek() == LCURLY; }
private boolean inSquare() { return !bracketStack.isEmpty() && bracketStack.peek() == LSQUARE; }


}

//////////////////////////////////////// LEXER //////////////////////////
//parser rules

//lexer rules
//NB: only unconditional keywords - the rest will be treated as identifiers and handled in the parser

BREAK: { !couldBeFieldName }? 'break';
CASE: { !couldBeFieldName }? 'case';
CATCH: { !couldBeFieldName }? 'catch';
CLASSDEF: { !couldBeFieldName }? 'classdef';
CONTINUE: { !couldBeFieldName }? 'continue';
DO: { !couldBeFieldName }? 'do';
ELSE: { !couldBeFieldName }? 'else';
ELSEIF: { !couldBeFieldName }? 'elseif';
END: { !couldBeFieldName }? 'end';
END_TRY_CATCH: { !couldBeFieldName }? 'end_try_catch';
END_UNWIND_PROJECT: { !couldBeFieldName }? 'end_unwind_project';
ENDCLASSDEF: { !couldBeFieldName }? 'endclassdef';
ENDENUMERATION: { !couldBeFieldName }? 'endenumeration';
ENDEVENTS: { !couldBeFieldName }? 'endevents';
ENDFOR: { !couldBeFieldName }? 'endfor';
ENDFUNCTION: { !couldBeFieldName }? 'endfunction';
ENDIF: { !couldBeFieldName }? 'endif';
ENDMETHODS: { !couldBeFieldName }? 'endmethods';
ENDPARFOR: { !couldBeFieldName }? 'endparfor';
ENDPROPERTIES: { !couldBeFieldName }? 'endproperties';
ENDSWITCH: { !couldBeFieldName }? 'endswitch';
ENDWHILE: { !couldBeFieldName }? 'endwhile';
ENUMERATION: { !couldBeFieldName }? 'enumeration';
EVENTS: { !couldBeFieldName }? 'events';
FILE: { !couldBeFieldName }? '__FILE__';
FOR: { !couldBeFieldName }? 'for';
FUNCTION: { !couldBeFieldName }? 'function';
GLOBAL: { !couldBeFieldName }? 'global';
IF: { !couldBeFieldName }? 'if';
METHODS: { !couldBeFieldName }? 'METHODS';
LINE: { !couldBeFieldName }? '__LINE__';
OTHERWISE: { !couldBeFieldName }? 'otherwise';
PARFOR: { !couldBeFieldName }? 'parfor';
PERSISTENT: { !couldBeFieldName }? 'persistent';
PROPERTIES: { !couldBeFieldName }? 'properties';
RETURN: { !couldBeFieldName }? 'return';
SWITCH: { !couldBeFieldName }? 'switch';
TRY: { !couldBeFieldName }? 'try';
UNTIL: { !couldBeFieldName }? 'until';
UNWIND_PROTECT: { !couldBeFieldName }? 'unwind_project';
UNWIND_PROTECT_CLEANUP: { !couldBeFieldName }? 'unwind_project_cleanup';
WHILE: { !couldBeFieldName }? 'while';



fragment LETTER : 'a'..'z' | 'A'..'Z';
fragment DIGIT : '0'..'9';
IDENTIFIER : ('_' | '$' | LETTER) ('_' | '$' | LETTER | DIGIT)* { couldBeFieldName = false; };
fragment INT_NUMBER : DIGIT+;
fragment SCI_EXP : ('d' | 'D' | 'e' | 'E') ('+' | '-')? DIGIT+;
fragment FP_NUMBER : (DIGIT+ '.' DIGIT*) | ('.' DIGIT+);
NUMBER : (INT_NUMBER | FP_NUMBER) SCI_EXP? ('i' | 'I' | 'j' | 'J')? { couldBeFieldName = false; };

PLUS : '+' { couldBeFieldName = false; };
MINUS : '-' { couldBeFieldName = false; };
MTIMES : '*' { couldBeFieldName = false; };
ETIMES : '.*' { couldBeFieldName = false; };
MDIV : '/' { couldBeFieldName = false; };
EDIV : './' { couldBeFieldName = false; };
MLDIV : '\\' { couldBeFieldName = false; };
ELDIV : '.\\' { couldBeFieldName = false; };
MPOW : ('^' | '**') { couldBeFieldName = false; };
EPOW : '.^' { couldBeFieldName = false; };
MTRANSPOSE : /*{!isPreTransposeChar(getToken().getText().charAt(-1))}?*/ '\'' { couldBeFieldName = false; };
ARRAYTRANSPOSE : '.\'' { couldBeFieldName = false; };
LE : '<=' { couldBeFieldName = false; };
GE : '>=' { couldBeFieldName = false; };
LT : '<' { couldBeFieldName = false; };
GT : '>' { couldBeFieldName = false; };
EQ : '==' { couldBeFieldName = false; };
NE : '!=' { couldBeFieldName = false; };
AND : '&' { couldBeFieldName = false; };
OR : '|' { couldBeFieldName = false; };
NOT : '!' { couldBeFieldName = false; };
SHORTAND : '&&' { couldBeFieldName = false; };
SHORTOR : '||' { couldBeFieldName = false; };
DOT : '.' { couldBeFieldName = true; }; //NB: TRUE
COMMA : ',' { couldBeFieldName = false; };
SEMICOLON : ';' { couldBeFieldName = false; };
COLON : ':' { couldBeFieldName = false; };
AT : '@' { couldBeFieldName = false; };
LPAREN : '(' { couldBeFieldName = false; };
RPAREN : ')' { couldBeFieldName = false; };
LCURLY : '{' { couldBeFieldName = false; };
RCURLY : '}' { couldBeFieldName = false; };
LSQUARE : '[' { couldBeFieldName = false; };
RSQUARE : ']' { couldBeFieldName = false; };
INCR: '++' {couldBeFieldName = false; };
DECR: '--' {couldBeFieldName = false; };
PLUSEQ : '+=' { couldBeFieldName = false; };
MINUSEQ : '-=' { couldBeFieldName = false; };
DIVEQ : '/=' { couldBeFieldName = false; };
MULEQ : '*=' { couldBeFieldName = false; };


ASSIGN : '=' { couldBeFieldName = false; };

//NB: matched AFTER transpose
fragment STRING_CHAR : ~('\'' | '\r' | '\n') | '\'\'';
STRING : /*{!isPreTransposeChar(getToken().getText().charAt(-1))}?*/ ('\'' | '\"' ) STRING_CHAR* ('\'' | '\"' | (LINE_TERMINATOR)) { couldBeFieldName = false; };
//TODO VSD: eliminated a { $type = MISC } here, might have been needed.

fragment ANNOTATION_FILLER : ~'*' | '*' ~')';
ANNOTATION : '(*' ANNOTATION_FILLER* '*)' { couldBeFieldName = false; };

fragment BRACKET_COMMENFILLER : ~('%' | '#') | ('%' | '#') ~('{' | '}');
BRACKET_COMMENT : ('%{' | '#{') BRACKET_COMMENFILLER* (BRACKET_COMMENT BRACKET_COMMENFILLER*)* ('%}' | '#}');

fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
COMMENT : ('%' | '#') | ('#' | '%') ~('{' | '\r' | '\n') NOT_LINE_TERMINATOR*;

SHELL_COMMAND : 'system' NOT_LINE_TERMINATOR* { couldBeFieldName = false; };

LINE_TERMINATOR : '\r' '\n' | '\r' | '\n' { couldBeFieldName = false; };

fragment ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;
fragment OTHER_WHITESPACE : (' ' | '\t' | '\f')+;
FILLER : (( ELLIPSIS_COMMENT) | OTHER_WHITESPACE)+; //NB: putting the predicate on the fragment doesn't work

//TODO-AC: this is a huge hack to get around an antlr issue.
//  When the lexer sees lookahead '..', it attempts to match FILLER, regardless of whether or not there's a third '.'
//  If there isn't, it simply fails instead of falling back to match DOT.
//  This prevents that from happening.  It is safe to return a nonsense token because '..' is never a valid sequence in matlab.
//TODO-AC: getting this rule to emit two DOT tokens would be much more satisfactory.
DOUBLE_DOT : '..' { couldBeFieldName = false; };

MISC : . { couldBeFieldName = false; };


//////// PARSER /////////////////////////////////////////////////////////////////////////


//start symbol
program :
     FILLER? EOF //empty
  |  FILLER? script scripending EOF
  |  function_beginning function_list function_ending EOF
  |  function_beginning class_def function_ending EOF
  ;

script :
     (stmt FILLER?)+
  ;

scripending :
     (COMMENT | BRACKET_COMMENT)?
  ;

stmt :
     stmt_body (stmt_separator | EOF)
  |  stmt_separator
  ;

stmt_separator :
     LINE_TERMINATOR
  |  COMMENT LINE_TERMINATOR
  |  BRACKET_COMMENT LINE_TERMINATOR
  |  SEMICOLON
  |  COMMA
  ;

stmt_body :
     maybe_cmd
  |  (GLOBAL | PERSISTENT) (FILLER? name)+ FILLER?
  |  SHELL_COMMAND FILLER?
  |  TRY sep_stmt_list (CATCH sep_stmt_list)? t_END FILLER?
  |  SWITCH FILLER? expr {wantCompoundStmtHeaderSeparator = true;} compound_stmt_header_sep {wantCompoundStmtHeaderSeparator = false;} (CASE FILLER? expr sep_stmt_list)* (OTHERWISE sep_stmt_list)? t_END FILLER?
  |  IF FILLER? expr sep_stmt_list (ELSEIF FILLER? expr sep_stmt_list)* (ELSE sep_stmt_list)? t_END FILLER?
  |  BREAK FILLER?
  |  CONTINUE FILLER?
  |  RETURN FILLER?
  |  WHILE FILLER? expr sep_stmt_list t_END FILLER?
  |  FOR FILLER? (name FILLER? ASSIGN FILLER? expr | LPAREN FILLER? name FILLER? ASSIGN FILLER? expr FILLER? RPAREN) sep_stmt_list t_END FILLER?
  |  PARFOR FILLER? (name FILLER? ASSIGN FILLER? expr | LPAREN FILLER? name FILLER? ASSIGN FILLER? expr FILLER? RPAREN) sep_stmt_list t_END FILLER?
  |  ANNOTATION FILLER?
  ;

maybe_cmd : //k = 1 forces reliance on the syntactic predicate
//todo vsd Removed option k=1
     expr (FILLER? ASSIGN FILLER? expr)? FILLER? #ASSIGN
  |  IDENTIFIER cmd_args #IGNORE
  ;

//TODO-AC: official doc suggests that this list is not complete but does not elaborate
//lookahead only
not_cmd_lookahead :
     ~IDENTIFIER
  |  IDENTIFIER ~FILLER
  |  IDENTIFIER FILLER LPAREN
  |  IDENTIFIER FILLER ASSIGN
  |  IDENTIFIER FILLER op FILLER after_op
  |  IDENTIFIER EOF
  ;

//lookahead only
op :
     PLUS
  |  MINUS
  |  MTIMES
  |  ETIMES
  |  MDIV
  |  EDIV
  |  MLDIV
  |  ELDIV
  |  MPOW
  |  EPOW
  //not MTRANSPOSE
  //not ARRAYTRANSPOSE
  |  LE
  |  GE
  |  LT
  |  GT
  |  EQ
  |  NE
  |  AND
  |  OR
  // not NOT
  |  SHORTAND
  |  SHORTOR
  |  AT //NB
  |  COLON
  // not DOT
  ;

//lookahead only
after_op :
     IDENTIFIER //includes PROPERTIES, METHODS, EVENTS
  |  NUMBER
  |  LPAREN
  |  LSQUARE
  |  LCURLY
  |  BREAK
  |  CASE
  |  CATCH
  |  CLASSDEF
  |  CONTINUE
  |  ELSE
  |  ELSEIF
  |  t_END
  |  FOR
  |  FUNCTION
  |  GLOBAL
  |  IF
  |  OTHERWISE
  |  PARFOR
  |  PERSISTENT
  |  RETURN
  |  SWITCH
  |  TRY
  |  WHILE
  |  PLUS
  |  MINUS
  |  t_NOT
  //not AT
  |  STRING //NB
  ;

cmd_args :
     //cmd_args_helper -> template(formatted={CommandFormatter.format($text, $cmd_args_helper.start.getLine(), $cmd_args_helper.start.getCharPositionInLine() + 1, offsetTracker, problems)}) "<formatted>"
     cmd_args_helper #CMD_ARGS
    //TODO VSD
  ;

//NB: use actual tokens, not t_ non-terminals
cmd_args_helper :
     FILLER (~(LPAREN | ASSIGN | COMMA | SEMICOLON | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT) cmd_args_tail)?
  |  ~(FILLER | MTRANSPOSE | ARRAYTRANSPOSE | LCURLY | LPAREN | ASSIGN | COMMA | SEMICOLON | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT) cmd_args_tail
  ;

//NB: use actual tokens, not t_ non-terminals
cmd_args_tail :
     (~(COMMA | SEMICOLON | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT))*
  ;

compound_stmt_header_sep :
     stmt_separator FILLER? #NO_COMMA
  |  FILLER stmt_separator FILLER? #NO_COMMA //don't insert a comma if we don't have to
  //|  {isCompoundStmtHeaderSeparator()}? { offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); } FILLER -> template(filler={$text}) ",<filler>" //insert comma
  | {isCompoundStmtHeaderSeparator()}? /*{ offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); }*/ FILLER #COMMA_INSERT
  //TODO VSD
  ;

sep_stmt_list :
     {wantCompoundStmtHeaderSeparator = true;} compound_stmt_header_sep {wantCompoundStmtHeaderSeparator = false;} (stmt FILLER?)*
  ;

function_list :
     function (function_separator function)*
  ;

function :
     function_body
  ;

function_beginning :
     //(((dFILLER | dt_LINE_TERMINATOR | dt_COMMENT | dt_BRACKET_COMMENT) { appendToLeadingComments($text, $start.getCharPositionInLine()); }) -> template() "")*
     (((FILLER | LINE_TERMINATOR | COMMENT | BRACKET_COMMENT) /*{ appendToLeadingComments($text, $start.getCharPositionInLine()); }*/) )*
     //TODO VSD
  ;

function_separator :
     /*{
        //first inserted newline
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceToNewLine(1, 1);

        TextPosition eofPos = LengthScanner.getLength(getTokenStream().LT(-1).getText());

        //second inserted newline
        int startCol = eofPos.getLine() == 1 ? getTokenStream().LT(-1).getCharPositionInLine() : 0;
        int lineLength = eofPos.getColumn() - 1 + startCol;
        offsetTracker.recordOffsetChange(-1, lineLength - 1);
        offsetTracker.advanceToNewLine(1, 1);

        //fudge factor to point following stuff back to location preceding inserted newlines
        offsetTracker.recordOffsetChange(-1, lineLength);
     }*/
     //((function_separator_blob { appendToLeadingComments($text, $start.getCharPositionInLine()); }) -> template() "")* -> template(gap={"\n\n"}) "<gap>"
     ((function_separator_blob /*{ appendToLeadingComments($text, $start.getCharPositionInLine()); }*/) )* #GAP
     //TODO VSD x2
  ;

function_separator_blob :
     FILLER
  |  LINE_TERMINATOR
  |  COMMENT LINE_TERMINATOR
  |  BRACKET_COMMENT LINE_TERMINATOR
  ;

function_ending :
     (FILLER? stmt_separator)* (COMMENT | BRACKET_COMMENT)?
  ;

function_body :
     FUNCTION (FILLER? output_params FILLER? ASSIGN)? FILLER? name FILLER? (input_params FILLER?)?
     //(stmt_separator -> template(original={$text}, extra={insertDeletedComments()}) "<original><extra>")
     (stmt_separator)
     (FILLER? stmt_or_function)* FILLER?
     t_END
     //TODO VSD
  ;

input_params :
     LPAREN FILLER? (input_param_list FILLER?)? RPAREN
  ;

input_param_list :
     name_or_tilde (FILLER? COMMA FILLER? name_or_tilde)*
  ;

output_params :
     LSQUARE output_param_list RSQUARE
  |  name
  ;

output_param_list :
     quiet_element_separator_list? (name_list quiet_element_separator_list?)?
  ;

name_list :
     name (element_separator_list name)*
  ;

stmt_or_function :
     stmt
  |  function_body
  ;

class_def :
     CLASSDEF (FILLER? attributes)? FILLER? IDENTIFIER (FILLER? LT FILLER? superclass_list)? fill_sep+ (FILLER? class_body)* FILLER? t_END
  ;

fill_sep :
     FILLER? stmt_separator
  ;

attributes :
     LPAREN FILLER? attribute (FILLER? COMMA FILLER? attribute)* FILLER? RPAREN
  ;

attribute :
     IDENTIFIER
  |  t_NOT FILLER? IDENTIFIER
  |  IDENTIFIER FILLER? ASSIGN FILLER? expr
  ;

superclass_list :
     IDENTIFIER (FILLER? AND FILLER? IDENTIFIER)*
  ;

class_body :
     properties_block fill_sep*
  |  methods_block fill_sep*
  |  events_block fill_sep*
  ;

properties_block :
     t_PROPERTIES (FILLER? attributes)? fill_sep+ (FILLER? properties_body)* FILLER? t_END
  ;

properties_body :
     IDENTIFIER (FILLER? ASSIGN FILLER? expr)? fill_sep+
  ;

methods_block :
     t_METHODS (FILLER? attributes)? fill_sep+ (FILLER? methods_body)* FILLER? t_END
  ;

methods_body :
     function
  |  function_signature
  |  property_access fill_sep*
  ;

events_block :
     t_EVENTS (FILLER? attributes)? fill_sep+ (FILLER? events_body)* FILLER? t_END
  ;

events_body :
     IDENTIFIER fill_sep+
  ;

function_signature :
     (output_params FILLER? ASSIGN FILLER?)? IDENTIFIER (FILLER? input_params)? fill_sep+
  ;

property_access :
     FUNCTION (FILLER? output_params FILLER? ASSIGN)? FILLER? IDENTIFIER FILLER? DOT FILLER? IDENTIFIER (FILLER? input_params)? stmt_separator (FILLER? stmt_or_function)* FILLER? t_END
  ;

//precedence from: http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
//all binary operators are left associative so no special handling is required
expr :
     short_or_expr
  |  AT FILLER? input_params FILLER? expr
  ;

short_or_expr :
     short_and_expr (FILLER? SHORTOR FILLER? short_and_expr)*
  ;

short_and_expr :
     or_expr (FILLER? SHORTAND FILLER? or_expr)*
  ;

or_expr :
     and_expr (FILLER? OR FILLER? and_expr)*
  ;

and_expr :
     comp_expr (FILLER? AND FILLER? comp_expr)*
  ;

comp_expr :
     colon_expr (FILLER? (LT | GT | LE | GE | EQ | NE) FILLER? colon_expr)*
  ;

colon_expr :
     plus_expr (FILLER? COLON FILLER? plus_expr (FILLER? COLON FILLER? plus_expr)?)?
  ;

//TODO-AC: antlr periodically has trouble with this (because of unary plus?)
plus_expr :
     binary_expr (FILLER? (PLUS | MINUS) FILLER? binary_expr)*
  ;

binary_expr :
     prefix_expr (FILLER? (MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV) FILLER? prefix_expr)*
  ;

prefix_expr :
     pow_expr
  |  t_NOT FILLER? prefix_expr
  |  (PLUS | MINUS) FILLER? prefix_expr //NB: plus_expr breaks if this is written as two rules
  ;

pow_expr :
     postfix_expr (FILLER? (MPOW | EPOW) FILLER? prefix_postfix_expr)*
  ;

prefix_postfix_expr :
     postfix_expr
  |  (t_NOT | PLUS | MINUS) FILLER? prefix_postfix_expr
  ;

postfix_expr :
     primary_expr (FILLER? (ARRAYTRANSPOSE | MTRANSPOSE))*
  ;

primary_expr :
     literal
  |  LPAREN FILLER? expr FILLER? RPAREN
  |  matrix
  |  cell_array
  |  access
  |  AT FILLER? name
  ;

//TODO-AC: This is separate from expr because it allows COLON and END
//  This shouldn't be necessary - dynamic scopes should handle this.
//  See revision 709 for an implementation with dynamic scopes.
arg :
     short_or_arg
  |  AT FILLER? input_params FILLER? arg
  |  COLON
  ;

short_or_arg :
     short_and_arg (FILLER? SHORTOR FILLER? short_and_arg)*
  ;

short_and_arg :
     or_arg (FILLER? SHORTAND FILLER? or_arg)*
  ;

or_arg :
     and_arg (FILLER? OR FILLER? and_arg)*
  ;

and_arg :
     comp_arg (FILLER? AND FILLER? comp_arg)*
  ;

comp_arg :
     colon_arg (FILLER? (LT | GT | LE | GE | EQ | NE) FILLER? colon_arg)*
  ;

colon_arg :
     plus_arg (FILLER? COLON FILLER? plus_arg (FILLER? COLON FILLER? plus_arg)?)?
  ;

//TODO-AC: antlr periodically has trouble with this (because of unary plus?)
plus_arg :
     binary_arg (FILLER? (PLUS | MINUS) FILLER? binary_arg)*
  ;

binary_arg :
     prefix_arg (FILLER? (MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV) FILLER? prefix_arg)*
  ;

prefix_arg :
     pow_arg
  |  t_NOT FILLER? prefix_arg
  |  (PLUS | MINUS) FILLER? prefix_arg //NB: plus_arg may break if this is written as two rules
  ;

pow_arg :
     postfix_arg (FILLER? (MPOW | EPOW) FILLER? prefix_postfix_arg)*
  ;

prefix_postfix_arg :
     postfix_arg
  |  (t_NOT | PLUS | MINUS) FILLER? prefix_postfix_arg
  ;

postfix_arg :
     primary_arg (FILLER? (ARRAYTRANSPOSE | MTRANSPOSE))*
  ;

primary_arg :
     literal
  |  LPAREN FILLER? arg FILLER? RPAREN
  |  matrix
  |  cell_array
  |  access
  |  AT FILLER? name
  |  t_END
  ;

access :
     paren_access (FILLER? DOT FILLER? paren_access)*
  ;

paren_access :
     cell_access (FILLER? LPAREN FILLER? (arg_list FILLER?)? RPAREN)?
  ;

cell_access :
     name (FILLER? LCURLY FILLER? arg_list FILLER? RCURLY)*
  |  {!(inSquare() || inCurly())}? name FILLER? AT FILLER? name
  |  {inSquare() || inCurly()}? name AT name //TODO-AC: fix error message for name AT FILLER name case
  ;

arg_list :
     arg (FILLER? COMMA FILLER? arg)*
  ;

literal :
     NUMBER
  |  STRING
  ;

matrix :
     LSQUARE optional_row_list RSQUARE
  ;

cell_array :
     LCURLY optional_row_list RCURLY
  ;

optional_row_list :

  |  quiet_element_separator_list
  |  (quiet_element_separator_list? quiet_row_separator)+ quiet_element_separator_list?
  |  (quiet_element_separator_list? quiet_row_separator)* row_list (quiet_row_separator_list quiet_element_separator_list?)?
  ;

row_list :
     row (row_separator_list row)*
  ;

row :
     quiet_element_separator_list? element_list quiet_element_separator_list?
  ;

//non-empty
row_separator_list :
     row_separator (quiet_element_separator_list? quiet_row_separator)*
  ;

//possibly empty
quiet_row_separator_list :
     quiet_row_separator (quiet_element_separator_list? quiet_row_separator)*
  ;

//TODO-AC: allow t_SEMICOLON t_LINE_TERMINATOR
row_separator :
     LINE_TERMINATOR
  |  SEMICOLON
  |  COMMENT LINE_TERMINATOR
  |  BRACKET_COMMENT LINE_TERMINATOR
  ;

quiet_row_separator : //match and delete row_separator
     //dt_LINE_TERMINATOR -> template() ""
     LINE_TERMINATOR
     //TODO VSD
  //|  dt_SEMICOLON -> template() ""
  |  SEMICOLON
  //TODO VSD
  |  COMMENT LINE_TERMINATOR //can't delete in this case
  |  BRACKET_COMMENT LINE_TERMINATOR //can't delete in this case
  ;

element_list :
     element (element_separator_list element)*
  ;

element :
     expr_or_tilde
  ;

expr_or_tilde :
//TODO VSD removed option k=2
     expr
  |  t_NOT
  ;

//non-empty
element_separator_list :
     FILLER? COMMA FILLER? #ECHO //just echo
  //|  {isElementSeparator()}? { offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); } FILLER -> template(filler={$text}) ",<filler>" //insert comma
  |  {isElementSeparator()}? /*{ offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); }*/ FILLER #COMMA_INSERT2
  //TODO VSD
  ;

//non-empty
quiet_element_separator_list :
     FILLER? quiet_element_separator_comma FILLER? //#COMMA
  |  FILLER //#COMMA
  ;

quiet_element_separator_comma :
     //dt_COMMA -> template() "" //delete comma
     COMMA //delete comma
     //TODO VSD
  ;

name :
     IDENTIFIER
  ;

name_or_tilde :
     IDENTIFIER
  |  t_NOT
  ;


//TODO-AC: technically there should also be a condition that we're in a class and the preceding non-filler wasn't DOT
//class-specific keywords (IDENTIFIER + predicate)
t_EVENTS : { ((CommonTokenStream) getTokenStream()).LT(1).getText().equals("events") }? IDENTIFIER /*{ offsetTracker.advanceInLine(6); }*/;
t_METHODS : { ((CommonTokenStream) getTokenStream()).LT(1).getText().equals("methods") }? IDENTIFIER /*{ offsetTracker.advanceInLine(7); }*/;
t_PROPERTIES : { ((CommonTokenStream) getTokenStream()).LT(1).getText().equals("properties") }? IDENTIFIER /*{ offsetTracker.advanceInLine(10); }*/;



t_NOT: NOT;
t_END: END
    | END_TRY_CATCH
    | END_UNWIND_PROJECT
    | ENDCLASSDEF
    | ENDENUMERATION
    | ENDEVENTS
    | ENDFOR
    | ENDFUNCTION
    | ENDIF
    | ENDMETHODS
    | ENDPARFOR
    | ENDPROPERTIES
    | ENDSWITCH
    | ENDWHILE
    ;

