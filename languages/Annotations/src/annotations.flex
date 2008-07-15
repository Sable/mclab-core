package annotations;

import static annotations.AnnotationParser.Terminals.*;

import beaver.Symbol;
import beaver.Scanner;

%%

//general header info
%public
%final
%class AnnotationScanner

//required for beaver compatibility
%extends Scanner
%unicode
%function nextToken
%type Symbol
%yylexthrow Scanner.Exception

//for debugging - track line and column
%line
%column

%{
  //// Returning symbols ///////////////////////////////////////////////////////

  //Create a symbol using the current line and column number, as computed by JFlex
  //No attached value
  //Symbol is assumed to start and end on the same line
  //e.g. symbol(SEMICOLON)
  private Symbol symbol(short type) {
    return symbol(type, null);
  }
  
  //Create a symbol using the current line and column number, as computed by JFlex
  //Attached value gives content information
  //Symbol is assumed to start and end on the same line
  //e.g. symbol(IDENTIFIER, "x")
  private Symbol symbol(short type, Object value) {
    //NB: JFlex is zero-indexed, but we want one-indexed
    int startLine = yyline + 1;
    int startCol = yycolumn + 1;
    int endLine = startLine;
    int endCol = startCol + yylength() - 1;
    return symbol(type, value, startLine, startCol, endLine, endCol);
  }
  
  //Create a symbol using explicit position information (one-indexed)
  private Symbol symbol(short type, Object value, int startLine, int startCol, int endLine, int endCol) {
    int startPos = Symbol.makePosition(startLine, startCol);
    int endPos = Symbol.makePosition(endLine, endCol);
    return new Symbol(type, startPos, endPos, value);
  }
  
  //// Errors //////////////////////////////////////////////////////////////////
  
  //throw an exceptions with position information from JFlex
  private void error(String msg) throws Scanner.Exception {
    //correct to one-indexed
    throw new Scanner.Exception(yyline + 1, yycolumn + 1, msg);
  }
%}

Whitespace = [ \t\f\r\n]+

Letter = [a-zA-Z]
Digit = [0-9]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*

Number = 0 | [1-9] {Digit}*

Comment = [/][/] .*

%%

{Whitespace} { /* skip */ }
{Comment} {/* skip */}

"." { return symbol(DOT); }

";" { return symbol(STMT_TERMINATOR); }
"," { return symbol(ELEMENT_SEPARATOR); }

"#" { return symbol(SIZE_OF); }
"=" { return symbol(EQ); }
":" { return symbol(HAS_TYPE); }

\( { return symbol(LPAREN); }
\) { return symbol(RPAREN); }
\{ { return symbol(LCURLY); }
\} { return symbol(RCURLY); }
\[ { return symbol(LSQUARE); }
\] { return symbol(RSQUARE); }

"*" { return symbol(VARARG_MODIFIER); }
"@" { return symbol(HANDLE_MODIFIER); }
"$" { return symbol(TYPE_VAR_MODIFIER); }

"->" { return symbol(ARROW); }
"?" { return symbol(UNKNOWN_TYPE); }

"|" { return symbol(UNION); }
"&" { return symbol(INTERSECT); }

{Number} { return symbol(NUMBER, Integer.parseInt(yytext())); } //NumberFormatException not possible

{Identifier} { return symbol(IDENTIFIER, yytext()); }

/* error fallback */
.|\n { error("Illegal character '" + yytext() + "'"); }

<<EOF>> { return symbol(EOF); }