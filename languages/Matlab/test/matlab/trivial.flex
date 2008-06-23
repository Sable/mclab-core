package matlab;

import beaver.Symbol;

%%

//general header info
%public
%final
%class TrivialScanner

%unicode
%function nextToken
%type Symbol

//track line and column
%line
%column

%%

. | \n { return new Symbol((short) 1, yyline + 1, yycolumn + 1, 1, yytext()); }
<<EOF>> { return new Symbol((short) -1); }