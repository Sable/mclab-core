//TODO-AC: factor out common bits

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
  private Symbol symbolFromMarkedPositions(short type) {
    return symbolFromMarkedPositions(type, null);
  }
  
  //like symbol(type, value), but uses the position stored in pos rather than
  //the position computed by JFlex
  private Symbol symbolFromMarkedPositions(short type, Object value) {
    return symbol(type, value, pos.startLine, pos.startCol, pos.endLine, pos.endCol);
  }
  
  //like symbol(type), but uses the start position stored in pos rather than
  //the start position computed by JFlex and an explicit length param rather
  //than yylength
  private Symbol symbolFromMarkedStart(short type, int length) {
    return symbolFromMarkedStart(type, null, length);
  }
  
  //like symbol(type, value), but uses the start position stored in pos rather than
  //the start position computed by JFlex and an explicit length param rather
  //than yylength
  private Symbol symbolFromMarkedStart(short type, Object value, int length) {
    return symbol(type, value, pos.startLine, pos.startCol, pos.startLine, pos.startCol + length - 1);
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
    
  private Symbol endCmdArg() throws Scanner.Exception {
    if(cmdQuoteCount % 2 == 1) {
        error("Unterminated command-style call argument: '" + cmdArgBuf + "'");
    }
    yypushback(yylength());
    markEndPosition();
    Symbol sym = (cmdArgBuf.length() == 0) ? null : cmdArgSymbol();
    restoreState();
    return sym;
  }
  
  //// Other blocks ////////////////////////////////////////////////////////////
  
  private StringBuffer propertiesBuf = new StringBuffer();
  private StringBuffer eventsBuf = new StringBuffer();
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
//within a bracket help comment (i.e. %%{)
%xstate HELP_COMMENT_NESTING
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
//inside a command-style call
%xstate COMMAND

%%

//TODO-AC: anything that doesn't call symbol might have to explicitly set transposeNext (probably to false)

//... comment
{EscapedLineTerminator} { return symbol(ELLIPSIS_COMMENT, yytext()); }

//whitespace
{LineTerminator} { return symbol(LINE_TERMINATOR, yytext()); }
{OtherWhiteSpace} { return symbol(OTHER_WHITESPACE, yytext()); }

//numeric literals
{Number} { return symbol(NUMBER, yytext()); }

//MTRANSPOSE or STRING (start)
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

//remainder of string literal (i.e. after initial single quote)
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
        error("Unterminated string literal: '" + strBuf + "'");
    }
}

//single-line comments
{HelpComment} { return symbol(HELP_COMMENT, yytext()); }
{Comment} { return symbol(COMMENT, yytext()); }

//start multiline help comment
{OpenBracketHelpComment} {
    transposeNext = false; 
    saveStateAndTransition(HELP_COMMENT_NESTING);
    markStartPosition();
    bracketCommentNestingDepth++;
    bracketCommentBuf = new StringBuffer(yytext());
}

//start multiline comment
{OpenBracketComment} {
    transposeNext = false; 
    saveStateAndTransition(COMMENT_NESTING);
    markStartPosition();
    bracketCommentNestingDepth++;
    bracketCommentBuf = new StringBuffer(yytext());
}

//continue multiline (help) comment
<COMMENT_NESTING, HELP_COMMENT_NESTING> {
    [^%]+ { bracketCommentBuf.append(yytext()); }
    % { bracketCommentBuf.append(yytext()); }
    {OpenBracketComment} { bracketCommentNestingDepth++; bracketCommentBuf.append(yytext()); }
    <<EOF>> {
        //don't finish scanning if there's an unclosed comment
        if(bracketCommentNestingDepth != 0) {
            error(bracketCommentNestingDepth + " levels of comments not closed");
        }
        return symbol(EOF);
    }
}

//terminate multiline comment
<COMMENT_NESTING> {
    {CloseBracketComment} { 
        bracketCommentNestingDepth--;
        bracketCommentBuf.append(yytext());
        if(bracketCommentNestingDepth == 0) {
            markEndPosition();
            Symbol sym = symbolFromMarkedPositions(BRACKET_COMMENT, bracketCommentBuf.toString());
            restoreState();
            return sym;
        }
    }
}

//terminate multiline help comment
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

//bang (!) syntax
{ShellCommand} { return symbol(SHELL_COMMAND, yytext().substring(1)); }

//start parenthesized section
\( {
    transposeNext = false; 
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
            return sym;
        }
    }
}

//start cell array
\{ {
    transposeNext = false; 
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
            return sym;
        }
    }
}

//start matrix
\[ {
    transposeNext = false; 
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
            return sym;
        }
    }
}

<INSIDE_PARENS, INSIDE_MATRIX, INSIDE_CELL_ARRAY> {
    <<EOF>> {
        //don't finish scanning if there's an unclosed bracket
        if(bracketNestingDepth != 0) {
            error("Unbalanced brackets", pos.startLine, pos.startCol);
        }
        return symbol(EOF);
    }
}

//stmt terminators
, { return symbol(COMMA); }
; { return symbol(SEMICOLON); }

//misc punctuation
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
    
    end { return symbol(END); }
}

<CLASS> {
    classdef { numEndsExpected++; return symbol(CLASSDEF); }
    
    end {
        numEndsExpected--;
        if(numEndsExpected == 0) {
            restoreState();
        }
        return symbol(END); //NB: just return normal END token
    }
    
    methods { numEndsExpected++; return symbol(METHODS); }
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
        return sym;
    }
    . { propertiesBuf.append(yytext()); }
}

<INSIDE_EVENTS> {
    end { 
        numEndsExpected--;
        eventsBuf.append(yytext());
        markEndPosition();
        Symbol sym = symbolFromMarkedPositions(EVENTS_BLOCK, eventsBuf.toString());
        restoreState();
        return sym;
    }
    . { eventsBuf.append(yytext()); }
}


<YYINITIAL, CLASS> {
    //from matlab "iskeyword" function
    
    case { maybeIncrNumEndsExpected(); return symbol(CASE); }
    for { maybeIncrNumEndsExpected(); return symbol(FOR); }
    function { maybeIncrNumEndsExpected(); return symbol(FUNCTION); }
    if { maybeIncrNumEndsExpected(); return symbol(IF); }
    parfor { maybeIncrNumEndsExpected(); return symbol(PARFOR); }
    switch { maybeIncrNumEndsExpected(); return symbol(SWITCH); }
    try { maybeIncrNumEndsExpected(); return symbol(TRY); }
    while { maybeIncrNumEndsExpected(); return symbol(WHILE); }
    
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

//ignore keywords - we just saw a dot
<FIELD_NAME> {
    {Identifier} { return symbol(IDENTIFIER, yytext()); }
    
    //NB: lower precedence than ellipsis
    \. { return symbol(DOT); }
}

<COMMAND> {
    //not stringified - return collected string and restore state
    {LineTerminator} | "," | ";" | "%" { Symbol sym = endCmdArg(); if(sym != null) { return sym; } }
    <<EOF>> { Symbol sym = endCmdArg(); if(sym != null) { return sym; } }
    
    {EscapedLineTerminator} {
        if(cmdQuoteCount % 2 == 1) {
            error("Unterminated command-style call argument: '" + cmdArgBuf + "'");
        } else {
            Symbol comment = symbol(ELLIPSIS_COMMENT, yytext());
            if(cmdArgBuf.length() > 0) {
                //TODO-AC: check that this fixes yyline and yycol
                yypushback(yylength()); //push comment back onto stream to be returned next
                return cmdArgSymbol();
            } else {
                return symbol(ELLIPSIS_COMMENT, yytext());
            }
        }
    }
    
    {OtherWhiteSpace}+ {
        if(cmdQuoteCount % 2 == 1) {
            cmdArgBuf.append(yytext());
            cmdArgPrevCharWasQuote = false;
            markEndPosition(); //NB: this will likely be overwritten before the string is returned
        } else if(cmdArgBuf.length() > 0) {
            yypushback(yylength()); //push whitespace back onto stream to be returned next
            return cmdArgSymbol();
        } else {
            return symbol(OTHER_WHITESPACE, yytext());
        }
    }
    
    //an initial "=" or "(" is not stringified
    "=" | "(" {
        if(cmdArgBuf.length() == 0) {
            Symbol sym = endCmdArg();
            if(sym != null) {
                return sym;
            }
        } else {
            cmdArgBuf.append(yytext());
            cmdArgPrevCharWasQuote = false;
            markEndPosition(); //NB: this will likely be overwritten before the string is returned
        }
    }
    
    //an initial "==" IS stringified
    "==" | . {
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
}

/* error fallback */
.|\n { error("Illegal character '" + yytext() + "'"); }

<<EOF>> {
    //don't need to check that we're in the initial state, because the
    //  xstates handle EOF and the non-xstates are acceptable when ending
    //don't need to check numEndsExpected since that's only used for changing
    //  states - the parser actually checks the bracketing
    return symbol(EOF);
}