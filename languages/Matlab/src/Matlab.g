//TODO-AC: I really wanted most of this grammar to be whitespace insensitive,
//  but I couldn't find a way to use whitespace as an element separator

grammar Matlab;

//AC: keep the lexer and the parser together for now because that's the default Antlr layout

options {
    //modify the buffer in place since our changes are small
    output = template;
    rewrite = true;
}

@parser::header {
package matlab;
}

@parser::members {
/* Convert a RecognitionException into a TranslationException. */
private matlab.TranslationProblem makeProblem(RecognitionException e) {
    return makeProblem(getTokenNames(), e);
}

/* Convert a RecognitionException into a TranslationException. */
private matlab.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
    //change column to 1-based
    return new matlab.ArrayTranslationProblem(e.line, e.charPositionInLine + 1, getErrorMessage(e, tokenNames));
}

/**
 * Input: a matrix or cell array with missing or extraneous row and column delimiters.
 * Output: a matrix or cell array with a single comma separating consecutive elements
 * and a single semicolon and/or newline separating consecutive rows.
 */
public static String translate(String text, int baseLine, int baseCol, OffsetTracker offsetTracker, List<matlab.TranslationProblem> problems) {
    ANTLRStringStream in = new ANTLRStringStream(text);
    in.setLine(baseLine);
    in.setCharPositionInLine(baseCol - 1); //since antlr columns are 0-based
    ArrayLexer lexer = new ArrayLexer(in);
    TokenRewriteStream tokens = new TokenRewriteStream(lexer);
    ArrayParser parser = new ArrayParser(tokens);
    parser.offsetTracker = offsetTracker;
    try {
        parser.array();
    } catch (RecognitionException e) {
        parser.problems.add(parser.makeProblem(e));
    }
    problems.addAll(lexer.getProblems());
    problems.addAll(parser.getProblems());
    return tokens.toString();
}

private OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));

private final List<matlab.TranslationProblem> problems = new ArrayList<matlab.TranslationProblem>();

public boolean hasProblem() { 
    return !problems.isEmpty();
}

public List<matlab.TranslationProblem> getProblems() {
    return java.util.Collections.unmodifiableList(problems);
}

//AC: this is a hackish way to prevent messages from being printed to stderr
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    problems.add(makeProblem(tokenNames, e));
}

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
    if(inParens()) {
        return false;
    }
    Token prevToken = input.LT(-1);
    Token nextToken = input.LT(2); //2, not 1 because we haven't matched the FILLER yet
    switch(nextToken.getType()) {
    case PLUS:
    case MINUS:
        if(input.LA(3) != FILLER) {
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

private final java.util.Stack<Integer> bracketStack = new java.util.Stack<Integer>();
private boolean inParens() { return bracketStack.peek() == LPAREN; }
}

@lexer::header {
package matlab;
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

//TODO-AC: This content is duplicated from the parser...maybe it can be factored out
private final List<matlab.TranslationProblem> problems = new ArrayList<matlab.TranslationProblem>();

public boolean hasProblem() { 
    return !problems.isEmpty();
}

public List<matlab.TranslationProblem> getProblems() {
    return java.util.Collections.unmodifiableList(problems);
}

//AC: this is a hackish way to prevent messages from being printed to stderr
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    problems.add(makeProblem(tokenNames, e));
}

private matlab.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
    //change column to 1-based
    return new matlab.ArrayTranslationProblem(e.line, e.charPositionInLine + 1, "LEXER: " + getErrorMessage(e, tokenNames));
}
}

//start symbol
program :
     t_FILLER? //empty
  |  t_FILLER? script
  |  t_FILLER? function_list
  |  class_def
  ;

script :
     (stmt t_FILLER?)+
  ;

stmt :
     (stmt_body t_FILLER?)? {input.LA(1) != EOF}? stmt_separator
  ;

stmt_separator :
     t_LINE_TERMINATOR
  |  t_COMMENT t_LINE_TERMINATOR
  |  t_BRACKET_COMMENT t_LINE_TERMINATOR
  |  t_SEMICOLON
  |  t_COMMA
  ;

stmt_body :
     expr (t_FILLER? t_ASSIGN t_FILLER? expr)?
  |  (t_GLOBAL | t_PERSISTENT) (t_FILLER? name)+
  |  t_SHELL_COMMAND
  |  t_TRY sep_stmt_list (t_CATCH sep_stmt_list)? t_END
  |  t_SWITCH t_FILLER? expr t_FILLER? stmt_separator t_FILLER? (t_CASE t_FILLER? expr sep_stmt_list)* (t_OTHERWISE sep_stmt_list)? t_END
  |  t_IF t_FILLER? expr sep_stmt_list (t_ELSEIF t_FILLER? expr sep_stmt_list)* (t_ELSE sep_stmt_list)? t_END
  |  t_BREAK
  |  t_CONTINUE
  |  t_RETURN
  |  t_WHILE t_FILLER? expr sep_stmt_list t_END
  |  t_FOR t_FILLER? (name t_FILLER? ASSIGN t_FILLER? expr | LPAREN t_FILLER? name t_FILLER? ASSIGN t_FILLER? expr t_FILLER? RPAREN) sep_stmt_list t_END
  ;

sep_stmt_list :
     t_FILLER? stmt_separator t_FILLER? (stmt t_FILLER?)*
  ;

function_list :
     (function t_FILLER?)+
  ;

function :
     function_body t_FILLER? {input.LA(1) != EOF}? stmt_separator
  ;

function_body :
     t_FUNCTION (t_FILLER? output_params t_FILLER? t_ASSIGN)? t_FILLER? name t_FILLER? input_params t_FILLER? stmt_separator (t_FILLER? stmt_or_function)* t_FILLER? t_END
  ;

input_params :
     t_LPAREN (t_FILLER? param_list)? t_FILLER? t_RPAREN
  ;

output_params :
     t_LSQUARE (t_FILLER? param_list)? t_FILLER? t_RSQUARE
  |  name
  ;

param_list :
     name (t_FILLER? t_COMMA t_FILLER? name)*
  ;

stmt_or_function :
     stmt
  |  function
  ;

class_def :
     t_CLASSDEF attributes? t_IDENTIFIER (t_LT superclass_list)? stmt_separator+ class_body* t_END 
  ;

attributes :
     t_LPAREN attribute (t_COMMA attribute)* t_RPAREN
  ;

attribute :
     t_IDENTIFIER
  |  t_NOT t_IDENTIFIER
  |  t_IDENTIFIER ASSIGN expr
  ;

superclass_list :
     t_IDENTIFIER (t_AND t_IDENTIFIER)*
  ;

class_body :
     properties_block stmt_separator*
  |  methods_block stmt_separator*
  |  events_block stmt_separator*
  ;

properties_block :
     t_PROPERTIES attributes? stmt_separator+ properties_body* t_END
  ;

properties_body :
     t_IDENTIFIER (t_ASSIGN expr)? stmt_separator+
  ;

methods_block :
     t_METHODS attributes? stmt_separator+ methods_body* t_END
  ;

methods_body :
     function
  |  function_signature
  |  property_access stmt_separator*
  ;

events_block :
     t_EVENTS attributes? stmt_separator+ events_body* t_END 
  ;

events_body :
     t_IDENTIFIER stmt_separator+
  ;

function_signature :
     (output_params t_ASSIGN)? t_IDENTIFIER input_params? stmt_separator+
  ;

property_access :
     t_FUNCTION (output_params t_ASSIGN)? t_IDENTIFIER t_DOT t_IDENTIFIER input_params? stmt_separator stmt_or_function* t_END
  ;

expr :
     expr_or_arg[false]
  ;

arg :
     expr_or_arg[true]
  ;

//precedence from: http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
//all binary operators are left associative so no special handling is required
expr_or_arg
  [boolean p_isArg]
  scope { boolean isArg; }
  @init { $expr_or_arg::isArg = $p_isArg; } :
     short_or_expr
  |  t_AT t_FILLER? input_params t_FILLER? expr
  |  {$expr_or_arg::isArg}? t_COLON
  ;

short_or_expr :
     short_and_expr (t_FILLER? t_SHORTOR t_FILLER? short_and_expr)*
  ;

short_and_expr :
     or_expr (t_FILLER? t_SHORTAND t_FILLER? or_expr)*

  ;

or_expr :
     and_expr (t_FILLER? t_OR t_FILLER? and_expr)*
  ;

and_expr :
     comp_expr (t_FILLER? t_AND t_FILLER? comp_expr)*
  ;

comp_expr :
     colon_expr (t_FILLER? (t_LT | t_GT | t_LE | t_GE | t_EQ | t_NE) t_FILLER? colon_expr)*
  ;

//TODO-AC: uncomment
colon_expr :
     plus_expr //(t_FILLER? t_COLON t_FILLER? plus_expr (t_FILLER? t_COLON t_FILLER? plus_expr)?)?
  ;

//TODO-AC: uncomment
plus_expr :
     binary_expr //(t_FILLER? (t_PLUS | t_MINUS) t_FILLER? binary_expr)*
  ;

binary_expr :
     prefix_expr (t_FILLER? (t_MTIMES | t_ETIMES | t_MDIV | t_EDIV | t_MLDIV | t_ELDIV) t_FILLER? prefix_expr)*
  ;

prefix_expr :
     pow_expr
  |  t_NOT t_FILLER? prefix_expr
  |  t_PLUS t_FILLER? prefix_expr
  |  t_MINUS t_FILLER? prefix_expr
  ;

pow_expr :
     postfix_expr (t_FILLER? (t_MPOW | t_EPOW) t_FILLER? postfix_expr)*
  ;

postfix_expr :
     primary_expr (t_FILLER? (t_ARRAYTRANSPOSE | t_MTRANSPOSE))*
  ;

primary_expr :
     literal
  |  t_LPAREN t_FILLER? expr t_FILLER? t_RPAREN
  |  matrix
  |  cell_array
  |  access
  |  t_AT t_FILLER? name
  //|  {$expr_or_arg::isArg}? t_END
  ;

access :
     paren_access (t_FILLER? t_DOT t_FILLER? paren_access)*
  ;

paren_access :
     cell_access (t_FILLER? t_LPAREN t_FILLER? (arg_list t_FILLER?)? t_RPAREN)?
  ;

cell_access :
     name (t_FILLER? t_LCURLY t_FILLER? arg_list t_FILLER? t_RCURLY)*
  |  {inParens()}? name t_FILLER? t_AT t_FILLER? name
  |  {!inParens()}? name t_AT name //TODO-AC: fix error message for name AT FILLER name case
  ;

arg_list :  
     arg (t_FILLER? t_COMMA t_FILLER? arg)*
  ;

literal :
     t_NUMBER
  |  t_STRING
  ;

matrix :
     t_LSQUARE optional_row_list t_RSQUARE
  ;

cell_array :
     t_LCURLY optional_row_list t_RCURLY
  ;

optional_row_list :
     
  |  quiet_element_separator_list
  |  (quiet_element_separator_list? quiet_row_separator)+ quiet_element_separator_list?
  |  (quiet_element_separator_list? quiet_row_separator)* row_list quiet_row_separator_list?
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
     t_LINE_TERMINATOR
  |  t_SEMICOLON
  |  t_COMMENT t_LINE_TERMINATOR
  |  t_BRACKET_COMMENT t_LINE_TERMINATOR
  ;

quiet_row_separator : //match and delete row_separator
     dt_LINE_TERMINATOR -> template() ""
  |  dt_SEMICOLON -> template() ""
  |  t_COMMENT t_LINE_TERMINATOR //can't delete in this case
  |  t_BRACKET_COMMENT t_LINE_TERMINATOR //can't delete in this case
  ;

element_list :
     element (element_separator_list element)*
  ;

element :
     expr
  ;

//non-empty
element_separator_list :
     t_FILLER? t_COMMA t_FILLER? //just echo
  |  {isElementSeparator()}? { offsetTracker.recordOffsetChange(0, -1); offsetTracker.advanceInLine(1); } t_FILLER -> template(filler={$text}) ",<filler>" //insert comma
  ;

//non-empty
quiet_element_separator_list :
     t_FILLER? quiet_element_separator_comma t_FILLER? //just echo
  |  {isElementSeparator()}? t_FILLER
  ;

quiet_element_separator_comma :
     dt_COMMA -> template() "" //delete comma
  ;

name :
     t_IDENTIFIER
  ;

//// Terminal Rules ////////////////////////////////////////////////////////////

//NB: always call these instead of the real tokens in order to keep the offsetTracker position up to date

//TODO-AC: is it possible to attach these actions to the actual lexer rules?  I don't think the lexer and
//  the parser are kept in sync (buffering, backtracking, etc)

t_BREAK : BREAK { offsetTracker.advanceInLine(5); };
t_CASE : CASE { offsetTracker.advanceInLine(4); };
t_CATCH : CATCH { offsetTracker.advanceInLine(5); };
t_CLASSDEF : CLASSDEF { offsetTracker.advanceInLine(8); };
t_CONTINUE : CONTINUE { offsetTracker.advanceInLine(8); };
t_ELSE : ELSE { offsetTracker.advanceInLine(4); };
t_ELSEIF : ELSEIF { offsetTracker.advanceInLine(6); };
t_END : END { offsetTracker.advanceInLine(3); };
t_FOR : FOR { offsetTracker.advanceInLine(3); };
t_FUNCTION : FUNCTION { offsetTracker.advanceInLine(8); };
t_GLOBAL : GLOBAL { offsetTracker.advanceInLine(6); };
t_IF : IF { offsetTracker.advanceInLine(2); };
t_OTHERWISE : OTHERWISE { offsetTracker.advanceInLine(9); };
t_PARFOR : PARFOR { offsetTracker.advanceInLine(6); };
t_PERSISTENT : PERSISTENT { offsetTracker.advanceInLine(10); };
t_RETURN : RETURN { offsetTracker.advanceInLine(6); };
t_SWITCH : SWITCH { offsetTracker.advanceInLine(6); };
t_TRY : TRY { offsetTracker.advanceInLine(3); };
t_WHILE : WHILE { offsetTracker.advanceInLine(5); };

//class-specific keywords (IDENTIFIER + predicate)
t_EVENTS : { input.LT(1).getText.equals("events") }? IDENTIFIER { offsetTracker.advanceInLine(6); };
t_METHODS : { input.LT(1).getText.equals("methods") }? IDENTIFIER { offsetTracker.advanceInLine(7); };
t_PROPERTIES : { input.LT(1).getText.equals("properties") }? IDENTIFIER { offsetTracker.advanceInLine(10); };

t_PLUS : PLUS { offsetTracker.advanceInLine(1); };
t_MINUS : MINUS { offsetTracker.advanceInLine(1); };
t_MTIMES : MTIMES { offsetTracker.advanceInLine(1); };
t_ETIMES : ETIMES { offsetTracker.advanceInLine(2); };
t_MDIV : MDIV { offsetTracker.advanceInLine(1); };
t_EDIV : EDIV { offsetTracker.advanceInLine(2); };
t_MLDIV : MLDIV { offsetTracker.advanceInLine(1); };
t_ELDIV : ELDIV { offsetTracker.advanceInLine(2); };
t_MPOW : MPOW { offsetTracker.advanceInLine(1); };
t_EPOW : EPOW { offsetTracker.advanceInLine(2); };
t_MTRANSPOSE : MTRANSPOSE { offsetTracker.advanceInLine(1); };
t_ARRAYTRANSPOSE : ARRAYTRANSPOSE { offsetTracker.advanceInLine(2); };
t_LE : LE { offsetTracker.advanceInLine(2); };
t_GE : GE { offsetTracker.advanceInLine(2); };
t_LT : LT { offsetTracker.advanceInLine(1); };
t_GT : GT { offsetTracker.advanceInLine(1); };
t_EQ : EQ { offsetTracker.advanceInLine(2); };
t_NE : NE { offsetTracker.advanceInLine(2); };
t_AND : AND { offsetTracker.advanceInLine(1); };
t_OR : OR { offsetTracker.advanceInLine(1); };
t_NOT : NOT { offsetTracker.advanceInLine(1); };
t_SHORTAND : SHORTAND { offsetTracker.advanceInLine(2); };
t_SHORTOR : SHORTOR { offsetTracker.advanceInLine(2); };
t_DOT : DOT { offsetTracker.advanceInLine(1); };
t_COMMA : COMMA { offsetTracker.advanceInLine(1); };
t_SEMICOLON : SEMICOLON { offsetTracker.advanceInLine(1); };
t_COLON : COLON { offsetTracker.advanceInLine(1); };
t_AT : AT { offsetTracker.advanceInLine(1); };

t_ASSIGN : ASSIGN { offsetTracker.advanceInLine(1); };

t_LPAREN : LPAREN { offsetTracker.advanceInLine(1); bracketStack.push(LPAREN); };
t_RPAREN : RPAREN { offsetTracker.advanceInLine(1); bracketStack.pop(); };
t_LCURLY : LCURLY { offsetTracker.advanceInLine(1); bracketStack.push(LCURLY); };
t_RCURLY : RCURLY { offsetTracker.advanceInLine(1); bracketStack.pop(); };
t_LSQUARE : LSQUARE { offsetTracker.advanceInLine(1); bracketStack.push(LSQUARE); };
t_RSQUARE : RSQUARE { offsetTracker.advanceInLine(1); bracketStack.pop(); };

t_IDENTIFIER : IDENTIFIER { offsetTracker.advanceByTextSize($text); };
t_NUMBER : NUMBER { offsetTracker.advanceByTextSize($text); };
t_STRING : STRING { offsetTracker.advanceByTextSize($text); };

t_BRACKET_COMMENT : BRACKET_COMMENT { offsetTracker.advanceByTextSize($text); };
t_COMMENT : COMMENT { offsetTracker.advanceByTextSize($text); };

t_SHELL_COMMAND : SHELL_COMMAND { offsetTracker.advanceByTextSize($text); };

t_FILLER : FILLER { offsetTracker.advanceByTextSize($text); };

t_LINE_TERMINATOR : LINE_TERMINATOR { offsetTracker.advanceToNewLine(1, 1); };

//special "delete" versions of the t_TOKEN non-terminals

dt_COMMA : COMMA { offsetTracker.recordOffsetChange(0, 1); };
dt_SEMICOLON : SEMICOLON { offsetTracker.recordOffsetChange(0, 1); };
dt_LINE_TERMINATOR : LINE_TERMINATOR { offsetTracker.recordOffsetChange(1, -1 * $LINE_TERMINATOR.pos); }; //NB: end pos of newline is 0-based

//// LEXER /////////////////////////////////////////////////////////////////////

//NB: not distinguishing between decimal and hex numbers at this level

//NB: only unconditional keywords - the rest will be treated as identifiers and handled in the parser

BREAK: 'break';
CASE: 'case';
CATCH: 'catch';
CLASSDEF: 'classdef';
CONTINUE: 'continue';
ELSE: 'else';
ELSEIF: 'elseif';
END: 'end';
FOR: 'for';
FUNCTION: 'function';
GLOBAL: 'global';
IF: 'if';
OTHERWISE: 'otherwise';
PARFOR: 'parfor';
PERSISTENT: 'persistent';
RETURN: 'return';
SWITCH: 'switch';
TRY: 'try';
WHILE: 'while';

fragment LETTER : 'a'..'z' | 'A'..'Z';
fragment DIGIT : '0'..'9';
fragment HEX_DIGIT : DIGIT | 'a'..'f' | 'A'..'F';
IDENTIFIER : ('_' | '$' | LETTER) ('_' | '$' | LETTER | DIGIT)*;
fragment HEX_NUMBER : HEX_DIGIT+;
fragment SCI_EXP : ('e' | 'E') ('+' | '-')? DIGIT+;
fragment FP_NUMBER : (DIGIT+ '.' DIGIT*) | ('.' DIGIT+) SCI_EXP?;
NUMBER : (HEX_NUMBER | FP_NUMBER) ('i' | 'I' | 'j' | 'J')?;

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
MTRANSPOSE : {isPreTransposeChar(input.LA(-1))}? '\'';
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
COLON : ':';
AT : '@';
LPAREN : '(';
RPAREN : ')';
LCURLY : '{';
RCURLY : '}';
LSQUARE : '[';
RSQUARE : ']';

ASSIGN : '=';
 
//NB: matched AFTER transpose
fragment STRING_CHAR : ~('\'' | '\r' | '\n') | '\'\'';
STRING : '\'' STRING_CHAR* '\'';

fragment BRACKET_COMMENT_FILLER : ~'%' | '%' ~('{' | '}');
BRACKET_COMMENT : '%{' BRACKET_COMMENT_FILLER* (BRACKET_COMMENT BRACKET_COMMENT_FILLER*)* '%}';

fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
COMMENT : '%' | '%' ~'{' NOT_LINE_TERMINATOR*;

SHELL_COMMAND : '!' NOT_LINE_TERMINATOR*;

LINE_TERMINATOR : '\r' '\n' | '\r' | '\n';

fragment ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;
fragment OTHER_WHITESPACE : (' ' | '\t' | '\f')+;
FILLER : ((('...')=> ELLIPSIS_COMMENT) | OTHER_WHITESPACE)+; //NB: putting the predicate on the fragment doesn't work
