package matlab;

%%

//general header info
%public
%final
%class TrivialScanner

%unicode
%function nextPos
%type TextPosition

//track line and column
%line
%column

%%

. | \n { return new TextPosition(yyline + 1, yycolumn + 1); }
<<EOF>> { return null; }