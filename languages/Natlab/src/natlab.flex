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
WhiteSpace     = {LineTerminator} | [ \t\f]
BackSlash      = \\
Arrow          = ->
LParen         = \(
RParen         = \)

Identifier = [:jletter:] [:jletterdigit:]*

%%

{Identifier}  { return symbol(NatlabParser.Terminals.IDENTIFIER, yytext()); }
{BackSlash}   { return symbol(NatlabParser.Terminals.LAMBDA); }
{Arrow}       { return symbol(NatlabParser.Terminals.ARG_BODY_SEP); }
{LParen}      { return symbol(NatlabParser.Terminals.LPAREN); }
{RParen}      { return symbol(NatlabParser.Terminals.RPAREN); }
{WhiteSpace}  { /* ignore */ }

/* error fallback */
.|\n          { throw new Scanner.Exception("Illegal character <" + yytext() + ">"); }
<<EOF>>       { return symbol(NatlabParser.Terminals.EOF); }