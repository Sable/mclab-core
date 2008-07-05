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
    offsetTracker.advanceByTextSize(text); //TODO-AC: update during translation
    ANTLRStringStream in = new ANTLRStringStream(text);
    in.setLine(baseLine);
    in.setCharPositionInLine(baseCol - 1); //since antlr columns are 0-based
    ArrayLexer lexer = new ArrayLexer(in);
    TokenRewriteStream tokens = new TokenRewriteStream(lexer);
    ArrayParser parser = new ArrayParser(tokens);
    try {
        parser.array();
    } catch (RecognitionException e) {
        parser.problems.add(parser.makeProblem(e));
    }
    problems.addAll(parser.getProblems());
    return tokens.toString();
}

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

private boolean prevTokenIsFiller(Token currToken) {
    int absIndex = currToken.getTokenIndex();
    Token prevToken = input.get(absIndex - 1);
    switch(prevToken.getType()) {
    case ELLIPSIS_COMMENT:
    case OTHER_WHITESPACE:
        return true;
    default:
        return false;
    }
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

private boolean isPrevTokenElementSeparator() {
    switch(input.LA(-1)) {
    case COMMA:
    case OTHER_WHITESPACE:
        return true;
    default:
        return false;
    }
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
}

array :
     matrix
  |  cell_array
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

plus_expr :
     binary_expr (FILLER? (PLUS | MINUS) FILLER? binary_expr)*
  ;

binary_expr :
     prefix_expr (FILLER? (MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV) FILLER? prefix_expr)*
  ;

prefix_expr :
     pow_expr
  |  NOT FILLER? prefix_expr
  |  PLUS FILLER? prefix_expr
  |  MINUS FILLER? prefix_expr
  ;

pow_expr :
     postfix_expr (FILLER? (MPOW | EPOW) FILLER? postfix_expr)*
  ;

postfix_expr :
     primary_expr (FILLER? (ARRAYTRANSPOSE | MTRANSPOSE))*
  ;

primary_expr :
     literal
  |  lparen FILLER? expr FILLER? rparen
  |  matrix
  |  cell_array
  |  access
  |  AT FILLER? name
  |  END
  ;

access :
     paren_access (FILLER? DOT FILLER? paren_access)*
  ;

paren_access :
     cell_access (FILLER? lparen FILLER? (arg_list FILLER?)? rparen)?
  ;

cell_access :
     name (FILLER? lcurly FILLER? arg_list FILLER? rcurly)*
  |  {inParens()}? name FILLER? AT FILLER? name
  |  {!inParens()}? name AT name //TODO-AC: fix error message for name AT FILLER name case
  ;

arg_list :  
     (arg) (FILLER? COMMA FILLER? arg)*
  ;
  
arg :
     expr
  |  COLON
  ;

literal :
     NUMBER
  |  STRING
  ;

matrix :
     lsquare optional_row_list rsquare
  ;

cell_array :
     lcurly optional_row_list rcurly
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

//TODO-AC: allow SEMICOLON LINE_TERMINATOR
row_separator :
     LINE_TERMINATOR
  |  SEMICOLON
  ;

quiet_row_separator : //match and delete row_separator
     LINE_TERMINATOR {System.err.println("deleting newline before " + input.LT(1));}-> template() ""
  |  SEMICOLON {System.err.println("deleting semicolon before " + input.LT(1));}-> template() ""
  ;

element_list :
     element (element_separator_list element)*
  ;

element :
     expr
  ;

//non-empty
element_separator_list :
     FILLER? COMMA FILLER? //just echo
  |  {isElementSeparator()}? FILLER {System.err.println("inserting comma before " + input.LT(1));}-> template(filler={$text}) ",<filler>" //insert comma
  ;

//non-empty
quiet_element_separator_list :
     FILLER? quiet_element_separator_comma FILLER? //just echo
  |  {isElementSeparator()}? FILLER
  ;

quiet_element_separator_comma :
     COMMA {System.err.println("deleting comma " + retval.start);}-> template() "" //delete comma
  ;

input_params :
     LPAREN FILLER? (param_list FILLER?)? RPAREN
  ;

param_list :
     name (FILLER? COMMA FILLER? name)*
  ;

name :
     IDENTIFIER
  ;

lparen : LPAREN { bracketStack.push(LPAREN); };
rparen : RPAREN { bracketStack.pop(); };
lcurly : LCURLY { bracketStack.push(LCURLY); };
rcurly : RCURLY { bracketStack.pop(); };
lsquare : LSQUARE { bracketStack.push(LSQUARE); };
rsquare : RSQUARE { bracketStack.pop(); };

//NB: not distinguishing between identifiers and keywords at this level - everything is an ID (except END)
//NB: not distinguishing between decimal and hex numbers at this level

END : 'end'; //meaning "last index"

fragment LETTER : 'a'..'z' | 'A'..'Z';
fragment DIGIT : '0'..'9';
fragment HEX_DIGIT : DIGIT | 'a'..'f' | 'A'..'F';
IDENTIFIER : ('_' | '$' | LETTER) ('_' | '$' | LETTER | DIGIT)*;
fragment HEX_NUMBER : HEX_DIGIT+;
fragment SCI_EXP : ('e' | 'E') ('+' | '-')? DIGIT+;
fragment FP_NUMBER : (DIGIT+ '.' DIGIT*) | ('.' DIGIT+) SCI_EXP;
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
BRACKET_COMMENT : '%{' BRACKET_COMMENT_FILLER* (BRACKET_COMMENT BRACKET_COMMENT_FILLER*)* '%}' { $channel=HIDDEN; }; //TODO-AC: test this

fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
COMMENT : '%' | '%' ~'{' NOT_LINE_TERMINATOR* { $channel=HIDDEN; };

LINE_TERMINATOR : '\r' '\n' | '\r' | '\n';

fragment ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;
fragment OTHER_WHITESPACE : (' ' | '\t' | '\f')+;
FILLER : (ELLIPSIS_COMMENT | OTHER_WHITESPACE)+;
