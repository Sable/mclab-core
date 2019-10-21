package natlab.tame.builtin.shapeprop;

import beaver.Symbol;
import beaver.Scanner;

import natlab.tame.builtin.shapeprop.ShapePropParser.Terminals;


%%

%class ShapePropScanner
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

Number = -? [:digit:] [:digit:]*
Identifier = [:jletter:] [:jletterdigit:]+
Uppercase = [A-Z]
Lowercase = [a-z]
MathString = \{ [^}]* \}
%%

{WhiteSpace}+   { /* ignore */ }

<YYINITIAL> {
	{Number}     { return newToken(Terminals.NUMBER,    new Integer(yytext())); }
	{Identifier} { return newToken(Terminals.ID,        new String(yytext())); }
	{Uppercase}  { return newToken(Terminals.UPPERCASE, new String(yytext())); }
	{Lowercase}  { return newToken(Terminals.LOWERCASE, new String(yytext())); }
	{MathString}  { return newToken(Terminals.MATHEXPR, new String(yytext())); }
	"$"          { return newToken(Terminals.SCALAR,    yytext()); }
	"#"          { return newToken(Terminals.ANY,       yytext()); }
	"||"         { return newToken(Terminals.OROR,      yytext()); }
	"->"         { return newToken(Terminals.ARROW,     yytext()); }
	","          { return newToken(Terminals.COMMA,     yytext()); }
	"("          { return newToken(Terminals.LRPAREN,   yytext()); }
	")"          { return newToken(Terminals.RRPAREN,   yytext()); }
	"?"          { return newToken(Terminals.QUESTION,  yytext()); }	
	"*"          { return newToken(Terminals.MULT,      yytext()); }
	"+"          { return newToken(Terminals.PLUS,      yytext()); }
	"="          { return newToken(Terminals.EQUAL,     yytext()); }
	"["          { return newToken(Terminals.LSPAREN,   yytext()); }
	"]"          { return newToken(Terminals.RSPAREN,   yytext()); }
	"|"          { return newToken(Terminals.OR,        yytext()); }
    "_"          { return newToken(Terminals.BLANK,     yytext()); }
}

.|\n            { throw new Scanner.Exception("unexpected character '" + yytext() + "'"); }
