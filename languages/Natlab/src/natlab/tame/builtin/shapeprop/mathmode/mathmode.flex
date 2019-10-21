package natlab.tame.builtin.shapeprop.mathmode;

import beaver.Symbol;
import beaver.Scanner;

import natlab.tame.builtin.shapeprop.mathmode.ShapePropMathModeParser.Terminals;


%%

%class ShapePropMathModeScanner
%extends Scanner
%function nextToken
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return newToken(Terminals.EOF, "end-of-file");
%eofval}
%unicode
%line
%column
%{
	private Symbol newToken(short id)
	{
		return new Symbol(id, yyline + 1, yycolumn + 1, yylength());
	}

	private Symbol newToken(short id, Object value)
	{
		return new Symbol(id, yyline + 1, yycolumn + 1, yylength(), value);
	}
%}
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Number = [:digit:] [:digit:]*
Identifier = [:jletter:] [:jletterdigit:]+
Lowercase = [a-z]
%%

{WhiteSpace}+   { /* ignore */ }

<YYINITIAL> {
	{Number}     { return newToken(Terminals.NUMBER,    new Integer(yytext())); }
	{Identifier} { return newToken(Terminals.ID,        new String(yytext())); }
	{Lowercase}  { return newToken(Terminals.LOWERCASE, new String(yytext())); }
	","          { return newToken(Terminals.COMMA, new String(yytext())); }
	"<"         { return newToken(Terminals.LT,      yytext()); }
	">"         { return newToken(Terminals.GT,     yytext()); }
	"<="          { return newToken(Terminals.LE,     yytext()); }
	">="          { return newToken(Terminals.GE,  yytext()); }
	"=="          { return newToken(Terminals.EQ,  yytext()); }
	"!="          { return newToken(Terminals.NE,  yytext()); }
	"&&"          { return newToken(Terminals.AND,  yytext()); }
	"("          { return newToken(Terminals.LPARENT,   yytext()); }
	")"          { return newToken(Terminals.RPARENT,   yytext()); }
	"+"          { return newToken(Terminals.PLUS,      yytext()); }
	"-"          { return newToken(Terminals.MINUS,      yytext()); }
	"*"          { return newToken(Terminals.TIMES,      yytext()); }
	"/"          { return newToken(Terminals.DIV,      yytext()); }
	"^"          { return newToken(Terminals.POWER,     yytext()); }
	"{"          { return newToken(Terminals.LBRACKET,  yytext()); }
	"}"          { return newToken(Terminals.RBRACKET,  yytext()); }
}

.|\n            { throw new Scanner.Exception("unexpected character '" + yytext() + "'"); }
