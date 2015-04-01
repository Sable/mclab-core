%%
import beaver.Symbol;
import beaver.Scanner;
%%
%layout helper_beaver
%helper

//general header info
%option visibility "%public"
%option final "%final"

//required for beaver compatibility
%option extends "%extends Scanner"
%option encoding "%unicode"

//for debugging - track line and column
%option line "%line"
%option col "%column"

%option type "%type Symbol"

%lexthrow "Scanner.Exception"

%%
%%inherit helper_symbols
%%inherit helper_errors