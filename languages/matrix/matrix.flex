
/* Matlab language lexer specification */

/* 2003-12: Modified to work with Beaver parser generator */


import beaver.Symbol;
import beaver.Scanner;

%%

%class MatrixScanner
%extends Scanner
%unicode
%function nextToken
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
    return token(MatrixParser.Terminals.EOF, "end-of-file");
%eofval}
%line
%column

%char
// %debug

%{
  StringBuffer string = new StringBuffer(128);

  private Symbol token(short id)
  {
    return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), yytext());
  }

  private Symbol token(short id, Object value)
  {
    return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), value);
  }

%}


/* main character classes */

Alpha = [_$A-Za-z]
Digit = [0-9] 
Space = [ \t]
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | {Space}

Elipsis          = \.\.\.    
Continuation  = {Elipsis} | \\
Imaginaryunit = [iIjJ]
CommentChar = [#%]
Comment    = {CommentChar} .* {LineTerminator}

/* identifiers  Identifier = [:jletter:][:jletterdigit:]*  */
Identifier = [_$A-Za-z][_$A-Za-z0-9]*

/* integer literals */
IntegerLiteral = 0 | [1-9][0-9]*

HexIntegerLiteral = 0 [xX] [0-9a-fA-F]+

/* floating point literals */
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]*
FLit2    = \. [0-9]+
FLit3    = [0-9]+
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state DQ_STRING, SQ_STRING, MATRIX_START

%%

<YYINITIAL> {

  /* boolean literals */
  "true"                         { return token(MatrixParser.Terminals.BOOLEAN_LITERAL, new Boolean(true)); }
  "false"                        { return token(MatrixParser.Terminals.BOOLEAN_LITERAL, new Boolean(false)); }

  /* null literal */
  "null"                         { return token(MatrixParser.Terminals.NULL_LITERAL); }


  /* separators */
  "("                            { return token(MatrixParser.Terminals.LPAREN); }
  ")"                            { return token(MatrixParser.Terminals.RPAREN); }
  
/*	// following three symbols are used by cell/structure, 
  "{"                            { return token(MatrixParser.Terminals.LBRACE); }
  "}"                            { return token(MatrixParser.Terminals.RBRACE); }
  "."                            { return token(MatrixParser.Terminals.DOT); }
*/
  "["                            { return token(MatrixParser.Terminals.LBRACK); }
  "]"                            { return token(MatrixParser.Terminals.RBRACK); }
  ";"                            { return token(MatrixParser.Terminals.SEMICOLON); }
  ","                            { return token(MatrixParser.Terminals.COMMA); }

  /* operators */
  ":"                            { return token(MatrixParser.Terminals.COLON); }

  "="                            { return token(MatrixParser.Terminals.EQ); }
  /* Arithmetic Operators */  
  "+"                            { return token(MatrixParser.Terminals.PLUS); }
  "-"                            { return token(MatrixParser.Terminals.MINUS); }
  "*"                            { return token(MatrixParser.Terminals.MULT); }
  "/"                            { return token(MatrixParser.Terminals.DIV); }
// "\\"                            { return token(MatrixParser.Terminals.LEFTDIV); }
  "^"                            { return token(MatrixParser.Terminals.POW); }
  "**"                           { return token(MatrixParser.Terminals.POW); }
  
  ".+"                           { return token(MatrixParser.Terminals.EPLUS); }
  ".-"                           { return token(MatrixParser.Terminals.EMINUS); }
  ".*"                           { return token(MatrixParser.Terminals.EMULT); }
  "./"                           { return token(MatrixParser.Terminals.EDIV); }
  ".\\"                          { return token(MatrixParser.Terminals.ELEFTDIV); }
  ".^"                           { return token(MatrixParser.Terminals.EPOW); }
  ".**"                          { return token(MatrixParser.Terminals.EPOW); }
  ".'"                           { return token(MatrixParser.Terminals.TRANSPOSE); }
  
  "++"                           { return token(MatrixParser.Terminals.PLUSPLUS); }
  "--"                           { return token(MatrixParser.Terminals.MINUSMINUS); }
  

  /* Comparision Operators */
  ">"                            { return token(MatrixParser.Terminals.GT); }
  "<"                            { return token(MatrixParser.Terminals.LT); }
  "=="                           { return token(MatrixParser.Terminals.EQEQ); }
  "<="                           { return token(MatrixParser.Terminals.LTEQ); }
  ">="                           { return token(MatrixParser.Terminals.GTEQ); }
  "!="                           { return token(MatrixParser.Terminals.NOTEQ); }
  "~="                           { return token(MatrixParser.Terminals.NOTEQ); }
  
  /* Boolean/Logical Operators */
  "!"                            { return token(MatrixParser.Terminals.NOT); }
  "~"                            { return token(MatrixParser.Terminals.NOT); }
  "&"                            { return token(MatrixParser.Terminals.AND); }
  "|"                            { return token(MatrixParser.Terminals.OR); }
  "&&"                           { return token(MatrixParser.Terminals.ANDAND); }
  "||"                           { return token(MatrixParser.Terminals.OROR); }

  
  "<<"                           { return token(MatrixParser.Terminals.LSHIFT); }
  ">>"                           { return token(MatrixParser.Terminals.RSHIFT); }
  
  "+="                           { return token(MatrixParser.Terminals.PLUSEQ); }
  "-="                           { return token(MatrixParser.Terminals.MINUSEQ); }
  "*="                           { return token(MatrixParser.Terminals.MULTEQ); }
  "/="                           { return token(MatrixParser.Terminals.DIVEQ); }
//  "\\="                          { return token(MatrixParser.Terminals.LEFTDIVEQ); }
  "^="                           { return token(MatrixParser.Terminals.POWEQ); }
  "**="                          { return token(MatrixParser.Terminals.POWEQ); }

  ".+="                          { return token(MatrixParser.Terminals.EPLUSEQ); }
  ".-="                          { return token(MatrixParser.Terminals.EMINUSEQ); }
  ".*="                          { return token(MatrixParser.Terminals.EMULTEQ); }
  "./="                          { return token(MatrixParser.Terminals.EDIVEQ); }
//  ".\\="                         { return token(MatrixParser.Terminals.ELEFTDIVEQ); }
  ".^="                          { return token(MatrixParser.Terminals.EPOWEQ); }
  ".**="                         { return token(MatrixParser.Terminals.EPOWEQ); }

  "&="                           { return token(MatrixParser.Terminals.ANDEQ); }
  "|="                           { return token(MatrixParser.Terminals.OREQ); }
  "<<="                          { return token(MatrixParser.Terminals.LSHIFTEQ); }
  ">>="                          { return token(MatrixParser.Terminals.RSHIFTEQ); }

  /* string literal */
  \"                             { yybegin(DQ_STRING); string.setLength(0); }

  /* character literal */
  \'                             { yybegin(SQ_STRING); string.setLength(0); }

  /* numeric literals */

  {IntegerLiteral}               { return token(MatrixParser.Terminals.INTEGER_LITERAL, Integer.valueOf(yytext())); }

  {HexIntegerLiteral}            { return token(MatrixParser.Terminals.INTEGER_LITERAL, Integer.valueOf(yytext().substring(2),16)); }

  {DoubleLiteral}                { return token(MatrixParser.Terminals.FLOATING_POINT_LITERAL, Double.valueOf(yytext())); }

  /* comments */
  {Comment}                      { return token(MatrixParser.Terminals.COMMENT, yytext());  }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */
  {Identifier}                   { return token(MatrixParser.Terminals.IDENTIFIER, yytext()); }
}

<DQ_STRING> {
  \"                             { yybegin(YYINITIAL); return token(MatrixParser.Terminals.DQ_STRING_LITERAL, string.toString()); }

  {StringCharacter}+             { string.append( yytext() ); }

  /* escape sequences */
  "\\0"                          { string.append( '\0' ); }
  "\\b"                          { string.append( '\b' ); }
  "\\f"                          { string.append( '\f' ); }
  "\\n"                          { string.append( '\n' ); }
  "\\r"                          { string.append( '\r' ); }
  "\\t"                          { string.append( '\t' ); }
  "\\\""                         { string.append( '\"' ); }
  "\\'"                          { string.append( '\'' ); }
  "\\\\"                         { string.append( '\\' ); }    /* probablly should skip this backslash */

  {Continuation}                 { /* ignore */ }

  /* error cases */
  \\.                            { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "Unterminated string at end of line"); }
}

<SQ_STRING> {
  \'                             { yybegin(YYINITIAL); return token(MatrixParser.Terminals.SQ_STRING_LITERAL, string.toString()); }

  {SingleCharacter}+             { string.append( yytext() ); }

  /* escape sequences */
  "\\0"                          { string.append( '\0' ); }
  "\\b"                          { string.append( '\b' ); }
  "\\f"                          { string.append( '\f' ); }
  "\\n"                          { string.append( '\n' ); }
  "\\r"                          { string.append( '\r' ); }
  "\\t"                          { string.append( '\t' ); }
  "\\\""                         { string.append( '\"' ); }
  "\\'"                          { string.append( '\'' ); }
  "\\\\"                         { string.append( '\\' ); }    /* probablly should skip this backslash */

  {Continuation}                 { /* ignore */ }

  /* error cases */
  \\.                            { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}               { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "Unterminated single-quoted string literal at end of line"); }
}


/* error fallback */
.|\n                             { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "unrecognized character '" + yytext() + "'"); }
<<EOF>>                          { return token(MatrixParser.Terminals.EOF); }