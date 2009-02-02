%%
import beaver.Scanner;
%%
%layout helper_errors
%helper

%declare "void error(String) throws Scanner.Exception"
%declare "void error(String, int) throws Scanner.Exception"
%{//// Errors //////////////////////////////////////////////////////////////////
  
  //throw an exceptions with position information from JFlex
  private void error(String msg) throws Scanner.Exception {
    //correct to one-indexed
    throw new Scanner.Exception(yyline + 1, yycolumn + 1, msg);
  }
  
  //throw an exceptions with position information from JFlex
  //columnOffset is added to the column
  private void error(String msg, int columnOffset) throws Scanner.Exception {
  //correct to one-indexed
    throw new Scanner.Exception(yyline + 1, yycolumn + 1 + columnOffset, msg);
  }
%}