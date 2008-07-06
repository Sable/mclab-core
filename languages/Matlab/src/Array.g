//TODO-AC: I really wanted most of this grammar to be whitespace insensitive,
//  but I couldn't find a way to use whitespace as an element separator

grammar Array;

//AC: keep the lexer and the parser together for now because that's the default Antlr layout

options {
    output = template;
    rewrite = true;
}

@parser::header {
package matlab;
}

@parser::members {
private matlab.TranslationProblem makeProblem(RecognitionException e) {
    return makeProblem(getTokenNames(), e);
}

private matlab.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
    //change column to 1-based
    return new matlab.ArrayTranslationProblem(e.line, e.charPositionInLine + 1, getErrorMessage(e, tokenNames));
}

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

array :
     matrix
  |  cell_array
  ;

//precedence from: http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
//all binary operators are left associative so no special handling is required
expr :
     short_or_expr
  |  t_AT t_FILLER? input_params t_FILLER? expr
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

colon_expr :
     plus_expr (t_FILLER? t_COLON t_FILLER? plus_expr (t_FILLER? t_COLON t_FILLER? plus_expr)?)?
  ;

plus_expr :
     binary_expr (t_FILLER? (t_PLUS | t_MINUS) t_FILLER? binary_expr)*
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
     (arg) (t_FILLER? t_COMMA t_FILLER? arg)*
  ;
  
arg :
     expr
  |  t_COLON
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
     dt_LINE_TERMINATOR {System.err.println("deleting newline before " + input.LT(1));}-> template() ""
  |  dt_SEMICOLON {System.err.println("deleting semicolon before " + input.LT(1));}-> template() ""
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
  |  {isElementSeparator()}? { offsetTracker.recordOffsetChange(0, -1); } t_FILLER {System.err.println("inserting comma before " + input.LT(1));}-> template(filler={$text}) ",<filler>" //insert comma
  ;

//non-empty
quiet_element_separator_list :
     t_FILLER? quiet_element_separator_comma t_FILLER? //just echo
  |  {isElementSeparator()}? t_FILLER
  ;

quiet_element_separator_comma :
     dt_COMMA {System.err.println("deleting comma " + retval.start);}-> template() "" //delete comma
  ;

input_params :
     t_LPAREN t_FILLER? (param_list t_FILLER?)? t_RPAREN
  ;

param_list :
     name (t_FILLER? t_COMMA t_FILLER? name)*
  ;

name :
     t_IDENTIFIER
  ;

//// Terminal Rules ////////////////////////////////////////////////////////////

//NB: always call these instead of the real tokens in order to keep the offsetTracker position up to date

//TODO-AC: is it possible to attach these actions to the actual lexer rules?  I don't think the lexer and
//  the parser are kept in sync (buffering, backtracking, etc)

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

t_FILLER : FILLER { offsetTracker.advanceByTextSize($text); };

t_LINE_TERMINATOR : LINE_TERMINATOR { offsetTracker.advanceToNewLine(1, 1); };

dt_COMMA : COMMA { offsetTracker.recordOffsetChange(0, 1); };
dt_SEMICOLON : SEMICOLON { offsetTracker.recordOffsetChange(0, 1); };
dt_LINE_TERMINATOR : LINE_TERMINATOR { offsetTracker.recordOffsetChange(1, -1 * $LINE_TERMINATOR.pos); }; //NB: end pos of newline is 0-based

//// LEXER /////////////////////////////////////////////////////////////////////

//NB: not distinguishing between identifiers and keywords at this level - everything is an ID
//NB: not distinguishing between decimal and hex numbers at this level

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
 
//NB: matched AFTER transpose
fragment STRING_CHAR : ~('\'' | '\r' | '\n') | '\'\'';
STRING : '\'' STRING_CHAR* '\'';

fragment BRACKET_COMMENT_FILLER : ~'%' | '%' ~('{' | '}');
BRACKET_COMMENT : '%{' BRACKET_COMMENT_FILLER* (BRACKET_COMMENT BRACKET_COMMENT_FILLER*)* '%}'; //TODO-AC: test this

fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
COMMENT : '%' | '%' ~'{' NOT_LINE_TERMINATOR*;

LINE_TERMINATOR : '\r' '\n' | '\r' | '\n';

fragment ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;
fragment OTHER_WHITESPACE : (' ' | '\t' | '\f')+;
FILLER : ((('...')=> ELLIPSIS_COMMENT) | OTHER_WHITESPACE)+; //NB: putting the predicate on the fragment doesn't work
