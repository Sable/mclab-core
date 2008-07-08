//The extractor lexer/parser is used to pull apart a Matlab file so that
//individual sections can be translated to Natlab by different tools and
//re-assembled.

//TODO-AC: factor out common bits (with natlab.flex)

//In general, if a block of text has nice delimiters, it is returned as a single
//token (e.g. any bracketed block).

package matlab;

import static matlab.ExtractionParser.Terminals.*;

import beaver.Symbol;
import beaver.Scanner;

%%

//general header info
%public
%final
%class ExtractionScanner

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
  //Symbol is assumed to start and end on the same line
  //Attaches yytext() as value
  private Symbol symbol(short type) {
    Object value = type == EOF ? null : yytext();
    //NB: JFlex is zero-indexed, but we want one-indexed
    int startLine = yyline + 1;
    int startCol = yycolumn + 1;
    int endLine = startLine;
    int endCol = startCol + yylength() - 1;
    return symbol(type, value, startLine, startCol, endLine, endCol);
  }
  
  //Create a symbol using explicit position information (one-indexed)
  private Symbol symbol(short type, Object value, int startLine, int startCol, int endLine, int endCol) {
    //if we return anything while in state FIELD_NAME, then restore state
    //i.e. only the first token after the dot is parsed specially
    if(yystate() == FIELD_NAME) {
        restoreState();
    }
    //if we saw something that forces the next single-quote to mean MTRANSPOSE, then set transposeNext
    transposeNext = TYPE_PRECEDING_TRANSPOSE.contains(type);
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
  private Symbol symbolFromMarkedPositions(short type, Object value) {
    return symbol(type, value, pos.startLine, pos.startCol, pos.endLine, pos.endCol);
  }
  
  //// Errors //////////////////////////////////////////////////////////////////
  
  //throw an exceptions with position information from JFlex
  private void error(String msg) throws Scanner.Exception {
    error(msg, 0);
  }
  
  //throw an exceptions with position information from JFlex
  //columnOffset is added to the column
  private void error(String msg, int columnOffset) throws Scanner.Exception {
    //correct to one-indexed
    error(msg, yyline + 1, yycolumn + 1 + columnOffset);
  }
  
  //throw an exceptions with explicit position information
  private void error(String msg, int line, int col) throws Scanner.Exception {
    throw new Scanner.Exception(line, col, msg);
  }
  
  //// Comment nesting /////////////////////////////////////////////////////////
  
  //number of '%}'s expected
  private int bracketCommentNestingDepth = 0;
  //bracket comment string consumed so far
  private StringBuffer bracketCommentBuf = null;
  
  //// Bracket nesting /////////////////////////////////////////////////////////
  
  //number of )/]/}'s expected
  private int bracketNestingDepth = 0;
  //bracket string consumed so far
  private StringBuffer bracketBuf = null;
  
  //// State transitions ///////////////////////////////////////////////////////
  
  //stack entry: stack identifier + symbol position
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
  
  void saveStateAndTransition(int newState) {
    stateStack.push(new StateRecord(yystate(), pos));
    pos = new PositionRecord();
    yybegin(newState);
  }
  
  void restoreState() {
    StateRecord rec = stateStack.pop();
    yybegin(rec.stateNum);
    pos = rec.pos;
  }
  
  //// End-bracketing //////////////////////////////////////////////////////////
  
  //Number of end keywords expected before we leave the CLASS state
  //NB: NOT USED TO VERIFY STRUCTURE (that happens in the parser)
  int numEndsExpected = 0;
  
  //Increment the number of 'end's expected if we are in the CLASS state
  void maybeIncrNumEndsExpected() {
    if(yystate() == CLASS) {
        numEndsExpected++;
    }
  }
  
  //// Transpose ///////////////////////////////////////////////////////////////
  
  //if any of these symbols is seen, then an immediately following single-quote
  //will be interpreted as MTRANSPOSE
  //if any other symbol is seen, then a single-quote will be interpreted as the
  //beginning of a string literal
  private static final java.util.Set<Short> TYPE_PRECEDING_TRANSPOSE = new java.util.HashSet<Short>();
  static {
    //NB: cannot contain DOT
    TYPE_PRECEDING_TRANSPOSE.add(IDENTIFIER);
    TYPE_PRECEDING_TRANSPOSE.add(NUMBER);
    TYPE_PRECEDING_TRANSPOSE.add(PARENTHESIZED);
    TYPE_PRECEDING_TRANSPOSE.add(MATRIX);
    TYPE_PRECEDING_TRANSPOSE.add(CELL_ARRAY);
    TYPE_PRECEDING_TRANSPOSE.add(ARRAYTRANSPOSE);
    TYPE_PRECEDING_TRANSPOSE.add(MTRANSPOSE);
  }
  
  private boolean transposeNext = false;
  
  //// String literals /////////////////////////////////////////////////////////
  
  //for accumulating the contents of a string literal
  private StringBuffer strBuf = new StringBuffer();
  
  //// Other blocks ////////////////////////////////////////////////////////////
  
  private StringBuffer propertiesBuf = new StringBuffer();
  private StringBuffer eventsBuf = new StringBuffer();
  
  //// Delayed return //////////////////////////////////////////////////////////
  
  private java.util.Queue<beaver.Symbol> symQueue = new java.util.LinkedList<beaver.Symbol>();
  
  private void append(beaver.Symbol sym) {
    symQueue.add(sym);
  }
%}

LineTerminator = \r|\n|\r\n
OtherWhiteSpace = [ \t\f]

//NB: acceptable to conflict with ... - matlab just treats .... as a comment containing .
Ellipsis = \.\.\.
EscapedLineTerminator = {Ellipsis}.*{LineTerminator}

Letter = [a-zA-Z]
Digit = [0-9]
HexDigit = {Digit} | [a-fA-F]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*
SciExp = [Ee][+-]?{Digit}+
Imaginary = [iIjJ]
IntNumber = {Digit}+
FPNumber = (({Digit}+\.?{Digit}*) | (\.?{Digit}+)){SciExp}?
HexNumber = 0[xX]{HexDigit}+
Number = ({IntNumber} | {FPNumber} | {HexNumber}) {Imaginary}?

Comment=% | %[^{\r\n].*
OpenBracketComment = %\{
CloseBracketComment = %\}

ShellCommand=[!].*

ValidEscape=\\[bfnrt\\\"]

//parsing the bit after a DOT
%state FIELD_NAME
//within a bracket comment (i.e. %{)
%xstate COMMENT_NESTING
//within a classdef
%state CLASS
//within a string literal
%xstate INSIDE_STRING
//within a cell array literal
%xstate INSIDE_CELL_ARRAY
//within a matrix literal
%xstate INSIDE_MATRIX
//within a matrix literal
%xstate INSIDE_PARENS
//within a properties block
%xstate INSIDE_PROPERTIES
//within an events block
%xstate INSIDE_EVENTS

%%

//TODO-AC: anything that doesn't call symbol might have to explicitly set transposeNext (probably to false)

//... comment
{EscapedLineTerminator} { append(symbol(ELLIPSIS_COMMENT)); }

//whitespace
{LineTerminator} { append(symbol(LINE_TERMINATOR)); }
{OtherWhiteSpace} { append(symbol(OTHER_WHITESPACE)); }

//numeric literals
{Number} { append(symbol(NUMBER)); }

//MTRANSPOSE or STRING (start)
"'" {
    //NB: cannot be a string if we're expecting a transpose - even if string is a longer match
    if(transposeNext) {
        append(symbol(MTRANSPOSE));
    } else {
        saveStateAndTransition(INSIDE_STRING);
        strBuf = new StringBuffer();
        strBuf.append(yytext());
        markStartPosition();
    }
}

//remainder of string literal (i.e. after initial single quote)
<INSIDE_STRING> {
    "''" { strBuf.append(yytext()); }
    "'" {
        strBuf.append(yytext());
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(STRING, strBuf.toString());
        restoreState();
        append(sym);
    }
    {ValidEscape} { strBuf.append(yytext()); }
    \\ { error("Invalid escape sequence"); }
    . { strBuf.append(yytext()); }
    <<EOF>> { 
        error("Unterminated string literal: '" + strBuf + "'");
    }
}

//single-line comments
{Comment} { append(symbol(COMMENT)); }

//start multiline comment
{OpenBracketComment} {
    transposeNext = false; 
    saveStateAndTransition(COMMENT_NESTING);
    markStartPosition();
    bracketCommentNestingDepth++;
    bracketCommentBuf = new StringBuffer(yytext());
}

//continue multiline comment
<COMMENT_NESTING> {
    [^%]+ { bracketCommentBuf.append(yytext()); }
    % { bracketCommentBuf.append(yytext()); }
    {OpenBracketComment} { bracketCommentNestingDepth++; bracketCommentBuf.append(yytext()); }
    {CloseBracketComment} { 
        bracketCommentNestingDepth--;
        bracketCommentBuf.append(yytext());
        if(bracketCommentNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(BRACKET_COMMENT, bracketCommentBuf.toString());
            restoreState();
            append(sym);
        }
    }
    <<EOF>> {
        yybegin(YYINITIAL); //so that we'll return EOF as the next token
        if(bracketCommentNestingDepth != 0) {
            error(bracketCommentNestingDepth + " levels of comments not closed");
        }
    }
}

//bang (!) syntax
{ShellCommand} { append(symbol(SHELL_COMMAND)); }

//start parenthesized section
\( {
    transposeNext = false; 
    bracketNestingDepth++;
    saveStateAndTransition(INSIDE_PARENS);
    markStartPosition();
    bracketBuf = new StringBuffer(yytext());
}

//continue parenthesized section
<INSIDE_PARENS> {
    [^\(\)]+ { bracketBuf.append(yytext()); }
    \( { bracketNestingDepth++; bracketBuf.append(yytext()); }
    \) { 
        bracketNestingDepth--;
        bracketBuf.append(yytext());
        if(bracketNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(PARENTHESIZED, bracketBuf.toString());
            restoreState();
            append(sym);
        }
    }
}

//start cell array
\{ {
    transposeNext = false;
    bracketNestingDepth++;
    saveStateAndTransition(INSIDE_CELL_ARRAY);
    markStartPosition();
    bracketBuf = new StringBuffer(yytext());
}

//continue cell array
<INSIDE_CELL_ARRAY> {
    [^\{\}]+ { bracketBuf.append(yytext()); }
    \{ { bracketNestingDepth++; bracketBuf.append(yytext()); }
    \} { 
        bracketNestingDepth--;
        bracketBuf.append(yytext());
        if(bracketNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(CELL_ARRAY, bracketBuf.toString());
            restoreState();
            append(sym);
        }
    }
}

//start matrix
\[ {
    transposeNext = false;
    bracketNestingDepth++;
    saveStateAndTransition(INSIDE_MATRIX);
    markStartPosition();
    bracketBuf = new StringBuffer(yytext());
}

//continue cell array
<INSIDE_MATRIX> {
    [^\[\]]+ { bracketBuf.append(yytext()); }
    \[ { bracketNestingDepth++; bracketBuf.append(yytext()); }
    \] { 
        bracketNestingDepth--;
        bracketBuf.append(yytext());
        if(bracketNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(MATRIX, bracketBuf.toString());
            restoreState();
            append(sym);
        }
    }
}

<INSIDE_PARENS, INSIDE_MATRIX, INSIDE_CELL_ARRAY> {
    <<EOF>> {
        yybegin(YYINITIAL); //so that we'll return EOF as the next token
        if(bracketNestingDepth != 0) {
            error("Unbalanced brackets", pos.startLine, pos.startCol);
        }
    }
}

//stmt terminators
, { append(symbol(COMMA)); }
; { append(symbol(SEMICOLON)); }

//misc punctuation
: { append(symbol(COLON)); }
@ { append(symbol(AT)); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/ref/arithmeticoperators.html
"+" { append(symbol(PLUS)); }
"-" { append(symbol(MINUS)); }
"*" { append(symbol(MTIMES)); }
".*" { append(symbol(ETIMES)); }
"/" { append(symbol(MDIV)); }
"./" { append(symbol(EDIV)); }
"\\" { append(symbol(MLDIV)); }
".\\" { append(symbol(ELDIV)); }
"^" { append(symbol(MPOW)); }
".^" { append(symbol(EPOW)); }
//"'" { append(symbol(MTRANSPOSE)); } //mixed with string rule above
".'" { append(symbol(ARRAYTRANSPOSE)); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/ref/relationaloperators.html
"<=" { append(symbol(LE)); }
">=" { append(symbol(GE)); }
"<" { append(symbol(LT)); }
">" { append(symbol(GT)); }
"==" { append(symbol(EQ)); }
"~=" { append(symbol(NE)); }

//from http://www.mathworks.com/access/helpdesk/help/techdoc/matlab_prog/f0-40063.html
"&" { append(symbol(AND)); }
"|" { append(symbol(OR)); }
"~" { append(symbol(NOT)); }
"&&" { append(symbol(SHORTAND)); }
"||" { append(symbol(SHORTOR)); }

"=" { append(symbol(ASSIGN)); }

<YYINITIAL> {
    classdef {
        numEndsExpected++; 
        saveStateAndTransition(CLASS);
        append(symbol(CLASSDEF));
    }
    
    end { append(symbol(END)); }
}

<CLASS> {
    classdef { numEndsExpected++; append(symbol(CLASSDEF)); }
    
    end {
        numEndsExpected--;
        if(numEndsExpected == 0) {
            restoreState();
        }
        append(symbol(END)); //NB: just return normal END token
    }
    
    methods { numEndsExpected++; append(symbol(METHODS)); }
    properties {
        transposeNext = false;
        numEndsExpected++;
        saveStateAndTransition(INSIDE_PROPERTIES);
        markStartPosition();
        propertiesBuf = new StringBuffer(yytext());
    }
    events {
        transposeNext = false;
        numEndsExpected++;
        saveStateAndTransition(INSIDE_EVENTS);
        markStartPosition();
        eventsBuf = new StringBuffer(yytext());
    }
}

<INSIDE_PROPERTIES> {
    end { 
        numEndsExpected--;
        propertiesBuf.append(yytext());
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(PROPERTIES_BLOCK, propertiesBuf.toString());
        restoreState();
        append(sym);
    }
    .|\n { propertiesBuf.append(yytext()); }
    <<EOF>> {
        yybegin(YYINITIAL); //so that we'll return EOF as the next token
        error("Unterminated properties block", pos.startLine, pos.startCol);
    }
}

<INSIDE_EVENTS> {
    end { 
        numEndsExpected--;
        eventsBuf.append(yytext());
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(EVENTS_BLOCK, eventsBuf.toString());
        restoreState();
        append(sym);
    }
    .|\n { eventsBuf.append(yytext()); }
    <<EOF>> {
        yybegin(YYINITIAL); //so that we'll return EOF as the next token
        error("Unterminated events block", pos.startLine, pos.startCol);
    }
}


<YYINITIAL, CLASS> {
    //from matlab "iskeyword" function
    
    case { maybeIncrNumEndsExpected(); append(symbol(CASE)); }
    for { maybeIncrNumEndsExpected(); append(symbol(FOR)); }
    function { maybeIncrNumEndsExpected(); append(symbol(FUNCTION)); }
    if { maybeIncrNumEndsExpected(); append(symbol(IF)); }
    parfor { maybeIncrNumEndsExpected(); append(symbol(PARFOR)); }
    switch { maybeIncrNumEndsExpected(); append(symbol(SWITCH)); }
    try { maybeIncrNumEndsExpected(); append(symbol(TRY)); }
    while { maybeIncrNumEndsExpected(); append(symbol(WHILE)); }
    
    break { append(symbol(BREAK)); }
    catch { append(symbol(CATCH)); }
    continue { append(symbol(CONTINUE)); }
    else { append(symbol(ELSE)); }
    elseif { append(symbol(ELSEIF)); }
    end { append(symbol(END)); }
    global { append(symbol(GLOBAL)); }
    otherwise { append(symbol(OTHERWISE)); }
    persistent { append(symbol(PERSISTENT)); }
    return { append(symbol(RETURN)); }
    
    //NB: lower precedence than keywords
    {Identifier} { append(symbol(IDENTIFIER)); }
    
    //NB: lower precedence than ellipsis
    \. {
            //NB: have to change the state AFTER calling symbol
            Symbol result = symbol(DOT);
            saveStateAndTransition(FIELD_NAME);
            append(result);
    }
}

//ignore keywords - we just saw a dot
<FIELD_NAME> {
    {Identifier} { append(symbol(IDENTIFIER)); }
    
    //NB: lower precedence than ellipsis
    \. { append(symbol(DOT)); }
}

/* error fallback */
.|\n { append(symbol(MISC)); }

<<EOF>> {
    //don't need to check that we're in the initial state, because the
    //  xstates handle EOF and the non-xstates are acceptable when ending
    //don't need to check numEndsExpected since that's only used for changing
    //  states - the parser actually checks the bracketing
    if(symQueue.isEmpty()) {
        return symbol(EOF);
    } else {
        return symQueue.poll();
    }
}