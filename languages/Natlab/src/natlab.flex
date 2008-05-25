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
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength());
  }
  private Symbol symbol(short type, Object value) {
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength(), value);
  }
%}

LineTerminator = \r|\n|\r\n
OtherWhiteSpace = [ \t\f]

Ellipsis = \.\.\.
EscapedLineTerminator = {Ellipsis}{LineTerminator}

Imaginary = [iIjJ]

Letter = [a-zA-Z]
Digit = [0-9]
HexDigit = {Digit} | [a-fA-F]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*
SciExp = [Ee][+-]?{Digit}+
DecimalNumber = ({Digit}+\.?{Digit}*{SciExp}?) | (\.?{Digit}+{SciExp}?)
HexNumber = (0[xX]{HexDigit}+)
Number = {DecimalNumber} | {HexNumber}

%%

{OtherWhiteSpace} { /* ignore */ }
{EscapedLineTerminator} { /* ignore */ }

{LineTerminator} {return symbol(LINE_TERMINATOR); }

{Identifier} { return symbol(IDENTIFIER, yytext()); }

{Number} { return symbol(NUMBER, yytext()); }

\( { return symbol(LPAREN); }
\) { return symbol(RPAREN); }
\[ { return symbol(LSQUARE); }
\] { return symbol(RSQUARE); }
\{ { return symbol(LCURLY); }
\} { return symbol(RCURLY); }

/* error fallback */
.|\n          { throw new Scanner.Exception("Illegal character '" + yytext() + "'"); }

<<EOF>>       { return symbol(EOF); }