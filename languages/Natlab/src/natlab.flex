package natlab;

import static natlab.NatlabParser.Terminals.*;

import beaver.Symbol;
import beaver.Scanner;

%%

%public
%final
%class NatlabScanner
%extends Scanner
%unicode
%function nextToken
%type Symbol
%yylexthrow Scanner.Exception
%line
%column

%{
  private Symbol symbol(short type) {
    if(yystate() == FIELD_NAME) {
        yybegin(YYINITIAL);
    }
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength());
  }
  private Symbol symbol(short type, Object value) {
    if(yystate() == FIELD_NAME) {
        yybegin(YYINITIAL);
    }
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength(), value);
  }
  
  private void error(String msg) throws Scanner.Exception {
    throw new Scanner.Exception(yyline + 1, yycolumn + 1, msg);
  }
  
  private final StringBuffer stringLitBuf = new StringBuffer();
%}

LineTerminator = \r|\n|\r\n
OtherWhiteSpace = [ \t\f]

Ellipsis = \.\.\.
//NB: require preceding whitespace to prevent '....' case.
EscapedLineTerminator = {OtherWhiteSpace}+{Ellipsis}.*{LineTerminator}

Letter = [a-zA-Z]
Digit = [0-9]
HexDigit = {Digit} | [a-fA-F]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*
SciExp = [Ee][+-]?{Digit}+
Imaginary = [iIjJ]
DecimalNumber = (({Digit}+\.?{Digit}*) | (\.?{Digit}+)){SciExp}?{Imaginary}?
HexNumber = 0[xX]{HexDigit}+{Imaginary}?
Number = {DecimalNumber} | {HexNumber}

CommentSymbol = %
HelpComment={CommentSymbol}{CommentSymbol}.*
Comment={CommentSymbol}.*

EscapeSequence = \\[bfnrt\"\\]

%state FIELD_NAME
%xstate STRING_LITERAL

%%

{EscapedLineTerminator} { return symbol(COMMENT, yytext().substring(yytext().indexOf("..."), yylength() - 1)); }
{OtherWhiteSpace} { /* ignore */ }

{LineTerminator} { return symbol(LINE_TERMINATOR); }

{Number} { return symbol(NUMBER, yytext()); }

{HelpComment} { return symbol(HELP_COMMENT, yytext()); }
{Comment} { return symbol(COMMENT, yytext()); }

\( { return symbol(LPAREN); }
\) { return symbol(RPAREN); }
\[ { return symbol(LSQUARE); }
\] { return symbol(RSQUARE); }
\{ { return symbol(LCURLY); }
\} { return symbol(RCURLY); }

, { return symbol(COMMA); }
; { return symbol(SEMICOLON); }

: { return symbol(COLON); }
@ { return symbol(AT); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/ref/arithmeticoperators.html
"+" { return symbol(PLUS); }
"-" { return symbol(MINUS); }
"*" { return symbol(MTIMES); }
".*" { return symbol(ETIMES); }
"/" { return symbol(MDIV); }
"./" { return symbol(EDIV); }
"\\" { return symbol(MLDIV); }
".\\" { return symbol(ELDIV); }
"^" { return symbol(MPOW); }
".^" { return symbol(EPOW); }
"'" { return symbol(MTRANSPOSE); }
".'" { return symbol(ARRAYTRANSPOSE); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/ref/relationaloperators.html
"<=" { return symbol(LE); }
">=" { return symbol(GE); }
"<" { return symbol(LT); }
">" { return symbol(GT); }
"==" { return symbol(EQ); }
"~=" { return symbol(NE); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
"&" { return symbol(AND); }
"|" { return symbol(OR); }
"~" { return symbol(NOT); }
"&&" { return symbol(SHORTAND); }
"||" { return symbol(SHORTOR); }

\" { stringLitBuf.setLength(0); stringLitBuf.append(yytext()); yybegin(STRING_LITERAL); }

<STRING_LITERAL> {
    {EscapeSequence} { stringLitBuf.append(yytext()); }
    \\. { error(yytext() + " is not a valid escape sequence"); }
    \" { stringLitBuf.append(yytext()); yybegin(YYINITIAL); return symbol(STRING, stringLitBuf.toString()); }
    . { stringLitBuf.append(yytext()); }
}

<YYINITIAL> {
    //from matlab "iskeyword" function
    break { return symbol(BREAK); }
    case { return symbol(CASE); }
    catch { return symbol(CATCH); }
    continue { return symbol(CONTINUE); }
    else { return symbol(ELSE); }
    elseif { return symbol(ELSEIF); }
    end { return symbol(END); }
    for { return symbol(FOR); }
    function { return symbol(FUNCTION); }
    global { return symbol(GLOBAL); }
    if { return symbol(IF); }
    otherwise { return symbol(OTHERWISE); }
    persistent { return symbol(PERSISTENT); }
    return { return symbol(RETURN); }
    switch { return symbol(SWITCH); }
    try { return symbol(TRY); }
    while { return symbol(WHILE); }
    
    //NB: lower precedence than keywords
    {Identifier} { return symbol(IDENTIFIER, yytext()); }
    
    //NB: lower precedence than ellipsis
    \. {
            //NB: have to change the state AFTER calling symbol
            Symbol result = symbol(DOT);
            yybegin(FIELD_NAME);
            return result;
       }
}

<FIELD_NAME> {
    {Identifier} { return symbol(IDENTIFIER, yytext()); }
    
    //NB: lower precedence than ellipsis
    \. { return symbol(DOT); }
}

/* error fallback */
.|\n          { error("Illegal character '" + yytext() + "'"); }

<<EOF>>       { return symbol(EOF); }