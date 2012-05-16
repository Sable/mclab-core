package natlab.tame.builtin.isComplexInfoProp;

import beaver.Symbol;
import beaver.Scanner;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropParser.Terminals;


%%

%class isComplexInfoPropScanner
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

%%

{WhiteSpace}+   { /* ignore */ }

<YYINITIAL> {
	{Number}     { return newToken(Terminals.NUMBER,    new Integer(yytext())); }
	{Identifier} { return newToken(Terminals.ID,        new String(yytext())); }
	"||"         { return newToken(Terminals.OROR,      yytext()); }
	"->"         { return newToken(Terminals.ARROW,     yytext()); }
	","          { return newToken(Terminals.COMMA,     yytext()); }
	"?"          { return newToken(Terminals.QUESTION,  yytext()); }	
	"*"          { return newToken(Terminals.MULT,      yytext()); }
	"+"          { return newToken(Terminals.PLUS,      yytext()); }
	"|"          { return newToken(Terminals.LOR,        yytext()); }
	"&"			 { return newToken(Terminals.LAND,        yytext()); }
	"{"          { return newToken(Terminals.LCURLY,    yytext()); }
	"}"          { return newToken(Terminals.RCURLY,    yytext()); }
	"<"          { return newToken(Terminals.LT,        yytext()); }
	">"          { return newToken(Terminals.GT,        yytext()); }
	"<\="         { return newToken(Terminals.LTE,      yytext()); }
	">="         { return newToken(Terminals.GTE,      yytext()); }
	"=="         { return newToken(Terminals.EQEQ,     yytext()); }
	"~="         { return newToken(Terminals.NEQ,     yytext()); }  
	"A"         { return newToken(Terminals.ANY,     yytext()); }  
	"X"         { return newToken(Terminals.COMPLEX,     yytext()); }  
	"R"         { return newToken(Terminals.REAL,     yytext()); }  
	":"         { return newToken(Terminals.COLON,     yytext()); }  
}

.|\n            { throw new Scanner.Exception("unexpected character '" + yytext() + "'"); }
