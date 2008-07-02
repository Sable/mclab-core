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
  |  AT input_params expr
  ;

short_or_expr :
     short_and_expr (SHORTOR short_and_expr)*
  ;

short_and_expr :
     or_expr (SHORTAND or_expr)*

  ;

or_expr :
     and_expr (OR and_expr)*
  ;

and_expr :
     comp_expr (AND comp_expr)*
  ;

comp_expr :
     colon_expr ((LT | GT | LE | GE | EQ | NE) colon_expr)*
  ;

colon_expr :
     plus_expr (COLON plus_expr (COLON plus_expr)?)?
  ;

plus_expr :
     binary_expr ((PLUS | MINUS) binary_expr)*
  ;

binary_expr :
     prefix_expr ((MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV) prefix_expr)*
  ;

prefix_expr :
     pow_expr
  |  NOT prefix_expr
  |  PLUS prefix_expr
  |  MINUS prefix_expr
  ;

pow_expr :
     postfix_expr ((MPOW | EPOW) postfix_expr)*
  ;

postfix_expr :
     primary_expr (ARRAYTRANSPOSE | MTRANSPOSE)*
  ;

primary_expr :
     literal
  |  LPAREN expr RPAREN
  |  matrix
  |  cell_array
  |  access
  |  AT name
  ;

access :
     cell_access (LPAREN arg_list? RPAREN)?
  ;

cell_access :
     (var_access) (LCURLY arg_list RCURLY)*
  |  name AT name
  ;
  
var_access :
     (name) (DOT name)*
  ;

arg_list :	
     (arg) (COMMA arg)*
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
     LSQUARE optional_row_list RSQUARE
  ;

cell_array :
     LCURLY optional_row_list RCURLY
  ;

optional_row_list :
     row_list? row_separator*
  ;

row_list :
     row (row_separator+ row)*
  ;

row :
     element_list element_separator*
  ;

row_separator :
     LINE_TERMINATOR
  |  SEMICOLON
  ;

element_list :
     element (element_separator+ element)*
  ;
  
element :
     expr
  ;

element_separator :
     COMMA
  ;

input_params :
     LPAREN param_list? RPAREN
  ;

param_list :
     name (COMMA name)*
  ;

name :
     IDENTIFIER
  ;

//NB: not distinguishing between identifiers and keywords at this level - everything is an ID
//NB: not distinguishing between decimal and hex numbers at this level

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
ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR { $channel=HIDDEN; };

LINE_TERMINATOR : '\r' '\n' | '\r' | '\n';
OTHER_WHITESPACE : (' ' | '\t' | '\f') { $channel=HIDDEN; };
