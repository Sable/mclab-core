package natlab;

import static natlab.NatlabParser.Terminals.*;

import beaver.Symbol;
import beaver.Scanner;

%%

%public
%final
%class NatlabScanner
%extends Scanner
%unicode
%function nextToken
%type Symbol
%yylexthrow Scanner.Exception
%line
%column

%{
  //// Returning symbols ///////////////////////////////////////////////////////

  //wrap a type (e.g. COLON) in a symbol object with appropriate position info
  private Symbol symbol(short type) {
    return symbol(type, null);
  }
  
  //wrap a type (e.g. IDENTIFIER) and value (e.g. "x") in a symbol object with appropriate position info
  private Symbol symbol(short type, Object value) {
    int startLine = yyline + 1;
    int startCol = yycolumn + 1;
    int endLine = startLine;
    int endCol = startCol + yylength() - 1;
    return symbol(type, value, startLine, startCol, endLine, endCol);
  }
  
  //wrap a type (e.g. IDENTIFIER) and value (e.g. "x") in a symbol object with explicit position info
  private Symbol symbol(short type, Object value, int startLine, int startCol, int endLine, int endCol) {
    //if we return anything while in FIELD_NAME, then switch back to initial
    //i.e. only the first token after the dot is parsed specially
    if(yystate() == FIELD_NAME) {
        restoreState();
    }
    transposeNext = TYPE_PRECEDING_TRANSPOSE.contains(type);
    int startPos = Symbol.makePosition(startLine, startCol);
    int endPos = Symbol.makePosition(endLine, endCol);
    return new Symbol(type, startPos, endPos, value);
  }
  
  //// Position ////////////////////////////////////////////////////////////////
  
  private static class PositionRecord {
      int startLine = -1;
      int startCol = -1;
      int endLine = -1;
      int endCol = -1;
  }
  
  private PositionRecord pos = new PositionRecord();
  
  private void markStartPosition() {
    pos.startLine = yyline + 1;
    pos.startCol = yycolumn + 1;
  }
  
  private void markEndPosition() {
    pos.endLine = yyline + 1;
    pos.endCol = (yycolumn + 1) + yylength() - 1;
  }
  
  private Symbol symbolFromMarkedPositions(short type) {
    return symbolFromMarkedPositions(type, null);
  }
  
  private Symbol symbolFromMarkedPositions(short type, Object value) {
    return symbol(type, value, pos.startLine, pos.startCol, pos.endLine, pos.endCol);
  }
  
  private Symbol symbolFromMarkedStart(short type, int length) {
    return symbolFromMarkedStart(type, null, length);
  }
  
  private Symbol symbolFromMarkedStart(short type, Object value, int length) {
    return symbol(type, value, pos.startLine, pos.startCol, pos.startLine, pos.startCol + length - 1);
  }
  
  //// Errors //////////////////////////////////////////////////////////////////
  
  //throw an exceptions with appropriate position information
  private void error(String msg) throws Scanner.Exception {
    throw new Scanner.Exception(yyline + 1, yycolumn + 1, msg);
  }
  
  //throw an exceptions with appropriate position information
  //columnOffset is added to the column
  private void error(String msg, int columnOffset) throws Scanner.Exception {
    throw new Scanner.Exception(yyline + 1, yycolumn + 1 + columnOffset, msg);
  }
  
  //// Numbers /////////////////////////////////////////////////////////////////
  
  private DecIntNumericLiteralValue parseDecInt(String text, boolean imaginary) throws Scanner.Exception {
      try { 
          return new DecIntNumericLiteralValue(yytext(), imaginary);
      } catch(NumberFormatException e) {
          error("Invalid number: " + yytext() + " (" + e.getMessage() + ")");
          return null; //unreachable - error throws an exception
      }
  }
  
  private HexNumericLiteralValue parseHexInt(String text, boolean imaginary) throws Scanner.Exception {
      try { 
          return new HexNumericLiteralValue(yytext(), imaginary);
      } catch(NumberFormatException e) {
          error("Invalid number: " + yytext() + " (" + e.getMessage() + ")");
          return null; //unreachable - error throws an exception
      }
  }
  
  private FPNumericLiteralValue parseFP(String text, boolean imaginary) throws Scanner.Exception {
      try { 
          return new FPNumericLiteralValue(yytext(), imaginary);
      } catch(NumberFormatException e) {
          error("Invalid number: " + yytext() + " (" + e.getMessage() + ")");
          return null; //unreachable - error throws an exception
      }
  }
  
  //// Comment nesting /////////////////////////////////////////////////////////
  
  //number of '%}'s expected
  private int bracketCommentNestingDepth = 0;
  //bracket comment string consumed so far
  private StringBuffer bracketCommentBuf = null;
  
  //// Comment queue ///////////////////////////////////////////////////////////
  
  private CommentBuffer commentBuffer = null;
  
  public void setCommentBuffer(CommentBuffer commentBuffer) {
      this.commentBuffer = commentBuffer;
  }
  
  public CommentBuffer getCommentBuffer() {
      return commentBuffer;
  }
  
  //// State transitions ///////////////////////////////////////////////////////
  
  private static class StateRecord {
    int stateNum;
    PositionRecord pos;
    
    StateRecord(int stateNum, PositionRecord pos) {
        this.stateNum = stateNum;
        this.pos = pos;
    }
  }
  
  //most of our states are used for bracketing
  //this gives us a way to nest bracketing states
  private java.util.Stack<StateRecord> stateStack = new java.util.Stack<StateRecord>();
  
  private void saveStateAndTransition(int newState) {
    stateStack.push(new StateRecord(yystate(), pos));
    pos = new PositionRecord();
    yybegin(newState);
  }
  
  private void restoreState() {
    StateRecord rec = stateStack.pop();
    yybegin(rec.stateNum);
    pos = rec.pos;
  }
  
  //// End-bracketing //////////////////////////////////////////////////////////
  
  //Number of end keywords expected before we leave the CLASS state
  //NB: NOT USED TO VERIFY STRUCTURE (that happens in the parser)
  int numEndsExpected = 0;
  
  //Increment the number of 'end's expected if we are int the CLASS state
  void maybeIncrNumEndsExpected() {
    if(yystate() == CLASS) {
        numEndsExpected++;
    }
  }
  
  //// Transpose ///////////////////////////////////////////////////////////////
  
  private static final java.util.Set<Short> TYPE_PRECEDING_TRANSPOSE = new java.util.HashSet<Short>();
  static {
    //NB: cannot contain DOT
    TYPE_PRECEDING_TRANSPOSE.add(IDENTIFIER);
    TYPE_PRECEDING_TRANSPOSE.add(INT_NUMBER);
    TYPE_PRECEDING_TRANSPOSE.add(FP_NUMBER);
    TYPE_PRECEDING_TRANSPOSE.add(RPAREN);
    TYPE_PRECEDING_TRANSPOSE.add(RSQUARE);
    TYPE_PRECEDING_TRANSPOSE.add(RCURLY);
    TYPE_PRECEDING_TRANSPOSE.add(ARRAYTRANSPOSE);
    TYPE_PRECEDING_TRANSPOSE.add(MTRANSPOSE);
  }
  
  private boolean transposeNext = false;
  
  //// String literals /////////////////////////////////////////////////////////
  
  private StringBuffer strBuf = new StringBuffer();
%}

LineTerminator = \r|\n|\r\n
OtherWhiteSpace = [ \t\f]

Ellipsis = \.\.\.
//NB: require preceding whitespace to prevent '....' case.
EscapedLineTerminator = {OtherWhiteSpace}+{Ellipsis}.*{LineTerminator}

Letter = [a-zA-Z]
Digit = [0-9]
HexDigit = {Digit} | [a-fA-F]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*
SciExp = [Ee][+-]?{Digit}+
Imaginary = [iIjJ]
IntNumber = {Digit}+
FPNumber = (({Digit}+\.?{Digit}*) | (\.?{Digit}+)){SciExp}?
HexNumber = 0[xX]{HexDigit}+
ImaginaryIntNumber = {Digit}+{Imaginary}
ImaginaryFPNumber = (({Digit}+\.?{Digit}*) | (\.?{Digit}+)){SciExp}?{Imaginary}
ImaginaryHexNumber = 0[xX]{HexDigit}+{Imaginary}

HelpComment=%% | %%[^{\r\n].*
OpenBracketHelpComment = %%\{
Comment=% | %[^%{\r\n].*
OpenBracketComment = %\{
CloseBracketComment = %\}

ShellCommand=[!].*

ValidEscape=\\[bfnrt\\\"]

//parsing the bit after a DOT
%state FIELD_NAME
//within a bracket comment (i.e. %{)
%xstate COMMENT_NESTING
%xstate HELP_COMMENT_NESTING
%state CLASS
%xstate COMMA_TERMINATOR
%xstate SEMICOLON_TERMINATOR
%xstate INSIDE_STRING

%%

//TODO-AC: anything that doesn't call symbol might have to explicitly set transposeNext (probably to false)

{EscapedLineTerminator} { transposeNext = false; commentBuffer.pushComment(symbol(ELLIPSIS_COMMENT, yytext().substring(yytext().indexOf("..."), yylength() - 1))); }

{LineTerminator} { return symbol(LINE_TERMINATOR); }
{OtherWhiteSpace} { transposeNext = false; /* ignore */ }

{IntNumber} { return symbol(INT_NUMBER, parseDecInt(yytext(), false)); }
{FPNumber} { return symbol(FP_NUMBER, parseFP(yytext(), false)); }
{HexNumber} { return symbol(INT_NUMBER, parseHexInt(yytext(), false)); }
{ImaginaryIntNumber} { return symbol(IM_INT_NUMBER, parseDecInt(yytext(), true)); }
{ImaginaryFPNumber} { return symbol(IM_FP_NUMBER, parseFP(yytext(), true)); }
{ImaginaryHexNumber} { return symbol(IM_INT_NUMBER, parseHexInt(yytext(), true)); }

"'" {
    //NB: cannot be a string if we're expecting a transpose - even if string is a longer match
    if(transposeNext) {
        return symbol(MTRANSPOSE);
    } else {
        saveStateAndTransition(INSIDE_STRING);
        strBuf = new StringBuffer();
        markStartPosition();
    }
}

<INSIDE_STRING> {
    "''" { strBuf.append(yytext()); }
    "'" {
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(STRING, strBuf.toString());
        restoreState();
        return sym;
    }
    {ValidEscape} { strBuf.append(yytext()); }
    \\ { error("Invalid escape sequence"); }
    . { strBuf.append(yytext()); }
    <<EOF>> { 
        error("Unterminated string literal: '" + strBuf);
    }
}

{HelpComment} { return symbol(HELP_COMMENT, yytext()); }
{Comment} { transposeNext = false; commentBuffer.pushComment(symbol(COMMENT, yytext())); }

{OpenBracketHelpComment} {
    transposeNext = false; 
    saveStateAndTransition(HELP_COMMENT_NESTING);
    markStartPosition();
    bracketCommentNestingDepth++;
    bracketCommentBuf = new StringBuffer(yytext());
}

{OpenBracketComment} {
    transposeNext = false; 
    saveStateAndTransition(COMMENT_NESTING);
    markStartPosition();
    bracketCommentNestingDepth++;
    bracketCommentBuf = new StringBuffer(yytext());
}

<COMMENT_NESTING, HELP_COMMENT_NESTING> {
    [^%]+ { bracketCommentBuf.append(yytext()); }
    % { bracketCommentBuf.append(yytext()); }
    {OpenBracketComment} { bracketCommentNestingDepth++; bracketCommentBuf.append(yytext()); }
}

<COMMENT_NESTING> {
    {CloseBracketComment} { 
        bracketCommentNestingDepth--;
        bracketCommentBuf.append(yytext());
        if(bracketCommentNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(BRACKET_COMMENT, bracketCommentBuf.toString());
            restoreState();
            commentBuffer.pushComment(sym);
        }
    }
}

<HELP_COMMENT_NESTING> {
    {CloseBracketComment} { 
        bracketCommentNestingDepth--;
        bracketCommentBuf.append(yytext());
        if(bracketCommentNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(BRACKET_HELP_COMMENT, bracketCommentBuf.toString());
            restoreState();
            return sym;
        }
    }
}

{ShellCommand} { return symbol(SHELL_COMMAND, yytext()); }

\( { return symbol(LPAREN); }
\) { return symbol(RPAREN); }
\[ { return symbol(LSQUARE); }
\] { return symbol(RSQUARE); }
\{ { return symbol(LCURLY); }
\} { return symbol(RCURLY); }

, { transposeNext = false; saveStateAndTransition(COMMA_TERMINATOR); markStartPosition(); }
; { transposeNext = false; saveStateAndTransition(SEMICOLON_TERMINATOR); markStartPosition(); }

<COMMA_TERMINATOR, SEMICOLON_TERMINATOR> {
    {OtherWhiteSpace} { /* ignore */ }

    {Comment} { commentBuffer.pushComment(symbol(COMMENT, yytext())); }

    {OpenBracketComment} {
        saveStateAndTransition(COMMENT_NESTING);
        markStartPosition();
        bracketCommentNestingDepth++;
        bracketCommentBuf = new StringBuffer(yytext());
    }
}

<COMMA_TERMINATOR> {
    {LineTerminator} { 
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(COMMA_LINE_TERMINATOR);
        restoreState();
        return sym;
    }
    <<EOF>> { 
        Symbol sym = symbolFromMarkedStart(COMMA, 1);
        restoreState();
        return sym;
    }
    . { 
        yypushback(1);
        Symbol sym = symbolFromMarkedStart(COMMA, 1);
        restoreState();
        return sym;
    }
}

<SEMICOLON_TERMINATOR> {
    {LineTerminator} { 
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(SEMICOLON_LINE_TERMINATOR);
        restoreState();
        return sym;
    }
    <<EOF>> { 
        Symbol sym = symbolFromMarkedStart(SEMICOLON, 1);
        restoreState();
        return sym;
    }
    . { 
        yypushback(1);
        Symbol sym = symbolFromMarkedStart(SEMICOLON, 1);
        restoreState();
        return sym;
    }
}

: { return symbol(COLON); }
@ { return symbol(AT); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/ref/arithmeticoperators.html
"+" { return symbol(PLUS); }
"-" { return symbol(MINUS); }
"*" { return symbol(MTIMES); }
".*" { return symbol(ETIMES); }
"/" { return symbol(MDIV); }
"./" { return symbol(EDIV); }
"\\" { return symbol(MLDIV); }
".\\" { return symbol(ELDIV); }
"^" { return symbol(MPOW); }
".^" { return symbol(EPOW); }
//"'" { return symbol(MTRANSPOSE); } //mixed with string rule above
".'" { return symbol(ARRAYTRANSPOSE); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/ref/relationaloperators.html
"<=" { return symbol(LE); }
">=" { return symbol(GE); }
"<" { return symbol(LT); }
">" { return symbol(GT); }
"==" { return symbol(EQ); }
"~=" { return symbol(NE); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
"&" { return symbol(AND); }
"|" { return symbol(OR); }
"~" { return symbol(NOT); }
"&&" { return symbol(SHORTAND); }
"||" { return symbol(SHORTOR); }

"=" { return symbol(ASSIGN); }

<YYINITIAL> {
    classdef {
        numEndsExpected++; 
        saveStateAndTransition(CLASS);
        return symbol(CLASSDEF);
    }
    
    case { return symbol(CASE); }
    for { return symbol(FOR); }
    function { return symbol(FUNCTION); }
    if { return symbol(IF); }
    parfor { return symbol(PARFOR); }
    switch { return symbol(SWITCH); }
    try { return symbol(TRY); }
    while { return symbol(WHILE); }
    
    end { return symbol(END); }
}

<CLASS> {
    classdef { numEndsExpected++; return symbol(CLASSDEF); }
    
    case { numEndsExpected++; return symbol(CASE); }
    for { numEndsExpected++; return symbol(FOR); }
    function { numEndsExpected++; return symbol(FUNCTION); }
    if { numEndsExpected++; return symbol(IF); }
    parfor { numEndsExpected++; return symbol(PARFOR); }
    switch { numEndsExpected++; return symbol(SWITCH); }
    try { numEndsExpected++; return symbol(TRY); }
    while { numEndsExpected++; return symbol(WHILE); }
    
    properties { return symbol(PROPERTIES); }
    methods { return symbol(METHODS); }
    events { return symbol(EVENTS); }
    
    end {
        numEndsExpected--;
        if(numEndsExpected == 0) {
            restoreState();
        }
        return symbol(END); //NB: just return normal END token
    }
}

<YYINITIAL, CLASS> {
    //from matlab "iskeyword" function
    break { return symbol(BREAK); }
    catch { return symbol(CATCH); }
    continue { return symbol(CONTINUE); }
    else { return symbol(ELSE); }
    elseif { return symbol(ELSEIF); }
    end { return symbol(END); }
    global { return symbol(GLOBAL); }
    otherwise { return symbol(OTHERWISE); }
    persistent { return symbol(PERSISTENT); }
    return { return symbol(RETURN); }
    
    //NB: lower precedence than keywords
    {Identifier} { return symbol(IDENTIFIER, yytext()); }
    
    //NB: lower precedence than ellipsis
    \. {
            //NB: have to change the state AFTER calling symbol
            Symbol result = symbol(DOT);
            saveStateAndTransition(FIELD_NAME);
            return result;
       }
}

<FIELD_NAME> {
    {Identifier} { return symbol(IDENTIFIER, yytext()); }
    
    //NB: lower precedence than ellipsis
    \. { return symbol(DOT); }
}

/* error fallback */
.|\n { error("Illegal character '" + yytext() + "'"); }

<<EOF>> {
            //don't finish scanning if there's an unclosed comment
            if(bracketCommentNestingDepth != 0) {
                error(bracketCommentNestingDepth + " levels of comments not closed");
            }
            //don't need to check that we're in the initial state, because the
            //  xstates don't accept EOF and the non-xstates are acceptable when ending
            //don't need to check numEndsExpected since that's only used for changing
            //  states - the parser actually checks the bracketing
            return symbol(EOF);
        }