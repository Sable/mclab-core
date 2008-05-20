package lambda;

import beaver.Symbol;
import beaver.Scanner;

%%

%public
%final
%class LambdaScanner
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

{Identifier}  { return symbol(LambdaParser.Terminals.IDENTIFIER, yytext()); }
{BackSlash}   { return symbol(LambdaParser.Terminals.LAMBDA); }
{Arrow}       { return symbol(LambdaParser.Terminals.ARG_BODY_SEP); }
{LParen}      { return symbol(LambdaParser.Terminals.LPAREN); }
{RParen}      { return symbol(LambdaParser.Terminals.RPAREN); }
{WhiteSpace}  { /* ignore */ }

/* error fallback */
.|\n          { throw new Scanner.Exception("Illegal character <" + yytext() + ">"); }
<<EOF>>       { return symbol(LambdaParser.Terminals.EOF); }