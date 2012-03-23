package natlab.tame.builtin.classprop;

import beaver.Symbol;
import beaver.Scanner;

import natlab.tame.builtin.classprop.ClassPropParser.Terminals;


%%

%class ClassPropScanner
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
Identifier = [:jletter:] [:jletterdigit:]*
%%

{WhiteSpace}+   { /* ignore */ }

<YYINITIAL> {
	"coerce"     { return newToken(Terminals.COERCE,    yytext()); }
	"typeString" { return newToken(Terminals.TYPESTRING,yytext()); }
	{Number}     { return newToken(Terminals.NUMBER,    new Integer(yytext())); }
	{Identifier} { return newToken(Terminals.ID,        new String(yytext())); }
	"("          { return newToken(Terminals.LPAREN,    yytext()); }
	")"          { return newToken(Terminals.RPAREN,    yytext()); }
	"||"         { return newToken(Terminals.OROR,      yytext()); }
	"|"          { return newToken(Terminals.OR,        yytext()); }
	","          { return newToken(Terminals.COMMA,     yytext()); }
	"*"          { return newToken(Terminals.MULT,      yytext()); }
	"?"          { return newToken(Terminals.QUESTION,  yytext()); }
	"->"         { return newToken(Terminals.ARROW,     yytext()); }
	
}

.|\n            { throw new Scanner.Exception("unexpected character '" + yytext() + "'"); }
