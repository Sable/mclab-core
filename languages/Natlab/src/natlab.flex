package natlab;

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
WhiteSpace = {LineTerminator} | [ \t\f]
Ellipsis = \.\.\.

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

{WhiteSpace} { /* ignore */ }
{Ellipsis} { /* ignore */ }

{Identifier} { return symbol(NatlabParser.Terminals.IDENTIFIER, yytext()); }

{Number} { return symbol(NatlabParser.Terminals.NUMBER, yytext()); }

\( { return symbol(NatlabParser.Terminals.LPAREN); }
\) { return symbol(NatlabParser.Terminals.RPAREN); }
\[ { return symbol(NatlabParser.Terminals.LSQUARE); }
\] { return symbol(NatlabParser.Terminals.RSQUARE); }
\{ { return symbol(NatlabParser.Terminals.LCURLY); }
\} { return symbol(NatlabParser.Terminals.RCURLY); }

/* error fallback */
.|\n          { throw new Scanner.Exception("Illegal character '" + yytext() + "'"); }

<<EOF>>       { return symbol(NatlabParser.Terminals.EOF); }