
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



Space = [ \t\f]
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | {Space}

Elipsis       = \.\.\.    
Continuation  = {Elipsis}  
Imaginaryunit = [iIjJ]
CommentChar = [#%]
Comment    = {CommentChar} .* {LineTerminator}

/* identifiers  Identifier = [:jletter:][:jletterdigit:]*  */
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*

/* Number literals */
DecimalNumber = {DecimalInteger} | {DecimalDouble}
Number = {DecimalNumber} | {HexNumber}

/* integer literals */
DecimalInteger = {Digit}+

HexDigit = {Digit} | [a-fA-F]
HexNumber = (0[xX]{HexDigit}+)

/* floating point literals */
DecimalDouble = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = DecimalInteger \. {Digit}*
FLit2    = \. {Digit}+
FLit3    = DecimalInteger
Exponent = [eE] [+-]? {Digit}+

/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

%state DQ_STRING, SQ_STRING, MATRIX_START

%%

<YYINITIAL> {

  /* boolean literals 
  "true"                         { return token(MatrixParser.Terminals.BOOLEAN_LITERAL, new Boolean(true)); }
  "false"                        { return token(MatrixParser.Terminals.BOOLEAN_LITERAL, new Boolean(false)); }

  "null"                         { return token(MatrixParser.Terminals.NULL_LITERAL); }
*/

  /* separators */
  "("                            { return token(MatrixParser.Terminals.LPAREN); }
  ")"                            { return token(MatrixParser.Terminals.RPAREN); }
  
/*	// following three symbols are used by cell/structure, 
  "{"                            { return token(MatrixParser.Terminals.LBRACE); }
  "}"                            { return token(MatrixParser.Terminals.RBRACE); }
  "."                            { return token(MatrixParser.Terminals.DOT); }
*/

  "["                            { return token(MatrixParser.Terminals.LSQUARE); }
  "]"                            { return token(MatrixParser.Terminals.RSQUARE); }
  ","                            { return token(MatrixParser.Terminals.COMMA); }
  ";"                            { return token(MatrixParser.Terminals.SEMICOLON); }
  ":"                            { return token(MatrixParser.Terminals.COLON); }
  "@"                            { return token(MatrixParser.Terminals.AT); }

  /* operators */
  "="                            { return token(MatrixParser.Terminals.ASSIGN); }
  /* Arithmetic Operators */  
/*************************************************************/
  
  "+"                            { return token(MatrixParser.Terminals.PLUS); }
  "-"                            { return token(MatrixParser.Terminals.MINUS); }
  "*"                            { return token(MatrixParser.Terminals.MTIMES); }
  "/"                            { return token(MatrixParser.Terminals.MDIV); }
  "\\"                           { return token(MatrixParser.Terminals.MLDIV); }
  "^"                            { return token(MatrixParser.Terminals.MPOW); }
  "**"                           { return token(MatrixParser.Terminals.MPOW); }
  
//  ".+"                           { return token(MatrixParser.Terminals.EPLUS); }
//  ".-"                           { return token(MatrixParser.Terminals.EMINUS); }
  ".*"                           { return token(MatrixParser.Terminals.ETIMES); }
  "./"                           { return token(MatrixParser.Terminals.EDIV); }
  ".\\"                          { return token(MatrixParser.Terminals.ELDIV); }
  ".^"                           { return token(MatrixParser.Terminals.EPOW); }
  ".**"                          { return token(MatrixParser.Terminals.EPOW); }
  ".'"                           { return token(MatrixParser.Terminals.ARRAYTRANSPOSE); }
  
  // Matlab doesn't support ++, --
//  "++"                           { return token(MatrixParser.Terminals.PLUSPLUS); }
//  "--"                           { return token(MatrixParser.Terminals.MINUSMINUS); }
  
  // Comparision Operators 
  ">"                            { return token(MatrixParser.Terminals.GT); }
  "<"                            { return token(MatrixParser.Terminals.LT); }
  "=="                           { return token(MatrixParser.Terminals.EQ); }
  "<="                           { return token(MatrixParser.Terminals.LE); }
  ">="                           { return token(MatrixParser.Terminals.GE); }
  "!="                           { return token(MatrixParser.Terminals.NE); }
  "~="                           { return token(MatrixParser.Terminals.NE); }
  
  // Boolean/Logical Operators 
  "!"                            { return token(MatrixParser.Terminals.NOT); }
  "~"                            { return token(MatrixParser.Terminals.NOT); }
  "&"                            { return token(MatrixParser.Terminals.AND); }
  "|"                            { return token(MatrixParser.Terminals.OR); }
  "&&"                           { return token(MatrixParser.Terminals.SHORTAND); }
  "||"                           { return token(MatrixParser.Terminals.SHORTOR); }
  
/***************** Octave extension *********************************
  "<<"                           { return token(MatrixParser.Terminals.LSHIFT); }
  ">>"                           { return token(MatrixParser.Terminals.RSHIFT); }

  "+="                           { return token(MatrixParser.Terminals.PLUSEQ); }
  "-="                           { return token(MatrixParser.Terminals.MINUSEQ); }
  "*="                           { return token(MatrixParser.Terminals.MULTEQ); }
  "/="                           { return token(MatrixParser.Terminals.DIVEQ); }
  "\\="                          { return token(MatrixParser.Terminals.LEFTDIVEQ); }
  "^="                           { return token(MatrixParser.Terminals.POWEQ); }
  "**="                          { return token(MatrixParser.Terminals.POWEQ); }

  ".+="                          { return token(MatrixParser.Terminals.EPLUSEQ); }
  ".-="                          { return token(MatrixParser.Terminals.EMINUSEQ); }
  ".*="                          { return token(MatrixParser.Terminals.EMULTEQ); }
  "./="                          { return token(MatrixParser.Terminals.EDIVEQ); }
  ".\\="                         { return token(MatrixParser.Terminals.ELEFTDIVEQ); }
  ".^="                          { return token(MatrixParser.Terminals.EPOWEQ); }
  ".**="                         { return token(MatrixParser.Terminals.EPOWEQ); }

  "&="                           { return token(MatrixParser.Terminals.ANDEQ); }
  "|="                           { return token(MatrixParser.Terminals.OREQ); }
  "<<="                          { return token(MatrixParser.Terminals.LSHIFTEQ); }
  ">>="                          { return token(MatrixParser.Terminals.RSHIFTEQ); }
********************************************************************/

  /* string literal */
  \"                             { yybegin(DQ_STRING); string.setLength(0); }

  /* character literal */
  \'                             { yybegin(SQ_STRING); string.setLength(0); }

  /* numeric literals */

  {DecimalInteger}               { return token(MatrixParser.Terminals.INT_NUMBER, yytext()); }

  {HexNumber}                    { return token(MatrixParser.Terminals.INT_NUMBER, (Integer.valueOf(yytext().substring(2),16)).toString()); }

  {DecimalDouble}                { return token(MatrixParser.Terminals.FP_NUMBER, yytext()); }

  /* comments */
  {Comment}                      { return token(MatrixParser.Terminals.COMMENT, yytext());  }

  /* identifiers */
  {Identifier}                   { return token(MatrixParser.Terminals.IDENTIFIER, yytext()); }

  /* continuation */
  {Continuation}                 { /* ignore */ }
  
  {LineTerminator}               { return token(MatrixParser.Terminals.LINE_TERMINATOR, yytext()); }
  
  /* whitespace */
  {Space}                   { /* ignore */ }

}

<DQ_STRING> {
  \"                             { yybegin(YYINITIAL); return token(MatrixParser.Terminals.STRING, string.toString()); }

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
  \'                             { yybegin(YYINITIAL); return token(MatrixParser.Terminals.STRING, string.toString()); }

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
<<EOF>>                          { return token(MatrixParser.Terminals.EOF); }
.|\n                             { throw new Scanner.Exception(yyline + 1, yycolumn + 1, "unrecognized character '" + yytext() + "'"); }
