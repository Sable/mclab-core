package matlab;

import static matlab.ExtractionParser.Terminals.*;

import beaver.Symbol;
import beaver.Scanner;

%%

//general header info
%public
%final
%class CommandScanner

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
  
  //// Position ////////////////////////////////////////////////////////////////
  
  //records the position of a symbol
  private static class PositionRecord {
      int startLine = -1;
      int startCol = -1;
      int endLine = -1;
      int endCol = -1;
  }
  
  //the position of the current symbol
  private PositionRecord pos = new PositionRecord();
  
  //populate the start line and column fields of the Position record with
  //values from JFlex
  private void markStartPosition() {
    //correct to one-indexed
    pos.startLine = yyline + 1;
    pos.startCol = yycolumn + 1;
  }
  
  //populate the start line and column fields of the Position record with
  //values from JFlex
  private void markEndPosition() {
    //correct to one-indexed
    pos.endLine = yyline + 1;
    pos.endCol = (yycolumn + 1) + yylength() - 1;
  }
  
  //like symbol(type), but uses the position stored in pos rather than
  //the position computed by JFlex
  private Symbol symbolFromMarkedPositions(short type) {
    return symbolFromMarkedPositions(type, null);
  }
  
  //like symbol(type, value), but uses the position stored in pos rather than
  //the position computed by JFlex
  private Symbol symbolFromMarkedPositions(short type, Object value) {
    return symbol(type, value, pos.startLine, pos.startCol, pos.endLine, pos.endCol);
  }
  
  //// Errors //////////////////////////////////////////////////////////////////
  
  //throw an exceptions with position information from JFlex
  private void error(String msg) throws Scanner.Exception {
    //correct to one-indexed
    throw new Scanner.Exception(yyline + 1, yycolumn + 1, msg);
  }
  
  //// Command-style call arguments ////////////////////////////////////////////
    
  private StringBuffer cmdArgBuf = new StringBuffer();
  private int cmdQuoteCount = 0;
  private boolean cmdArgPrevCharWasQuote = false;
  
  private Symbol cmdArgSymbol() {
    String cmdArg = cmdArgBuf.toString();
    cmdArgBuf = new StringBuffer();
    cmdQuoteCount = 0;
    cmdArgPrevCharWasQuote = false;
    
    return symbolFromMarkedPositions(STRING, cmdArg);
  }
  
  private Symbol handleNonArg(Symbol nonArg) throws Scanner.Exception {
    if(cmdQuoteCount % 2 == 1) {
        error("Unterminated command-style call argument: '" + cmdArgBuf + "'");
        return null; //unreachable, but Java can't tell that error throws an exception
    } else {
        if(cmdArgBuf.length() > 0) {
            yypushback(yylength()); //will rematch after we return the arg
            return cmdArgSymbol();
        }
        return nonArg;
    }
  }
%}

LineTerminator = \r|\n|\r\n
OtherWhiteSpace = [ \t\f]

//NB: acceptable to conflict with ... - matlab just treats .... as a comment containing .
Ellipsis = \.\.\.
EscapedLineTerminator = {Ellipsis}.*{LineTerminator}

%%

{EscapedLineTerminator} { handleNonArg(symbol(ELLIPSIS_COMMENT, yytext())); }

{OtherWhiteSpace}+ {
    if(cmdQuoteCount % 2 == 1) {
        cmdArgBuf.append(yytext());
        cmdArgPrevCharWasQuote = false;
        markEndPosition(); //NB: this will likely be overwritten before the string is returned
    } else if(cmdArgBuf.length() > 0) {
        return cmdArgSymbol();
    }
}

. {
    if(cmdArgBuf.length() == 0) {
        markStartPosition();
    }
    boolean isQuote = yytext().equals("'");
    if(isQuote) {
        cmdQuoteCount++;
        if(cmdArgPrevCharWasQuote && (cmdQuoteCount % 2 == 1)) {
            cmdArgBuf.append("''");
            markEndPosition(); //NB: this will likely be overwritten before the string is returned
        }
    } else {
        cmdArgBuf.append(yytext());
        markEndPosition(); //NB: this will likely be overwritten before the string is returned
    }
    cmdArgPrevCharWasQuote = isQuote;
}

<<EOF>> { handleNonArg(symbol(EOF)); }