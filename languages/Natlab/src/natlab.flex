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

  //wrap a type (e.g. IDENTIFIER) in a symbol object with appropriate position info
  private Symbol symbol(short type) {
    //if we return anything while in FIELD_NAME, then switch back to initial
    //i.e. only the first token after the dot is parsed specially
    if(yystate() == FIELD_NAME) {
        restoreState();
    }
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength());
  }
  
  //wrap a type (e.g. IDENTIFIER) and value (e.g. "x") in a symbol object with appropriate position info
  private Symbol symbol(short type, Object value) {
    //if we return anything while in FIELD_NAME, then switch back to initial
    //i.e. only the first token after the dot is parsed specially
    if(yystate() == FIELD_NAME) {
        restoreState();
    }
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength(), value);
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
  
  //// Strings /////////////////////////////////////////////////////////////////
  
  //throws an exception if the string contains any invalid escape sequences
  private void validateEscapeSequences(String str) throws Scanner.Exception {
    boolean expectEscape = false;
    int offset = 0;
    for(char ch : str.toCharArray()) {
        if(expectEscape) {
            if("bfnrt\\\"".indexOf(ch) < 0) {
                //NB: offset - 1 so that the preceding \ is flagged as the error
                error("Invalid escape sequence '\\" + ch + "'", offset - 1);
            }
            expectEscape = false;
        } else if(ch == '\\') {
            expectEscape = true;
        }
        offset++;
    }
    if(expectEscape) {
        //TODO-AC: better column number
        error("Incomplete escape sequence '\\'", offset);
    }
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
  
  private final java.util.Queue<Symbol> commentQueue = new java.util.LinkedList<Symbol>();
  
  public Symbol peekComment() {
      return commentQueue.peek();
  }
  
  public Symbol pollComment() {
      return commentQueue.poll();
  }
  
  public java.util.List<Symbol> pollAllComments() {
      java.util.List<Symbol> allComments = new java.util.ArrayList<Symbol>();
      allComments.addAll(commentQueue);
      commentQueue.clear();
      return allComments;
  }
  
  public boolean hasComment() {
      return !commentQueue.isEmpty();
  }
  
  //// State transitions ///////////////////////////////////////////////////////
  
  //most of our states are used for bracketing
  //this gives us a way to nest bracketing states
  private java.util.Stack<Integer> stateStack = new java.util.Stack<Integer>();
  
  private void saveStateAndTransition(int newState) {
      stateStack.push(yystate());
      yybegin(newState);
  }
  
  private void restoreState() {
      yybegin(stateStack.pop());
  }
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

OpenClassDef = classdef
//NB: not a valid identifier
CloseClassDef = end-classdef

ShellCommand=[!].*

String=[']([^'\r\n] | [']['])*[']

//parsing the bit after a DOT
%state FIELD_NAME
//within a bracket comment (i.e. %{)
%xstate COMMENT_NESTING
%xstate HELP_COMMENT_NESTING
%state CLASS
%xstate COMMA_TERMINATOR
%xstate SEMICOLON_TERMINATOR

%%

{EscapedLineTerminator} { commentQueue.add(symbol(ELLIPSIS_COMMENT, yytext().substring(yytext().indexOf("..."), yylength() - 1))); }

{LineTerminator} { return symbol(LINE_TERMINATOR); }
{OtherWhiteSpace} { /* ignore */ }

{IntNumber} { return symbol(INT_NUMBER, parseDecInt(yytext(), false)); }
{FPNumber} { return symbol(FP_NUMBER, parseFP(yytext(), false)); }
{HexNumber} { return symbol(INT_NUMBER, parseHexInt(yytext(), false)); }
{ImaginaryIntNumber} { return symbol(IM_INT_NUMBER, parseDecInt(yytext(), true)); }
{ImaginaryFPNumber} { return symbol(IM_FP_NUMBER, parseFP(yytext(), true)); }
{ImaginaryHexNumber} { return symbol(IM_INT_NUMBER, parseHexInt(yytext(), true)); }

{String} { validateEscapeSequences(yytext()); return symbol(STRING, yytext().substring(1, yylength() - 1)); }

{HelpComment} { return symbol(HELP_COMMENT, yytext()); }
{Comment} { commentQueue.add(symbol(COMMENT, yytext())); }

{OpenBracketHelpComment} { 
    saveStateAndTransition(HELP_COMMENT_NESTING);
    bracketCommentNestingDepth++;
    bracketCommentBuf = new StringBuffer(yytext());
}

{OpenBracketComment} { 
    saveStateAndTransition(COMMENT_NESTING);
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
            restoreState();
            commentQueue.add(symbol(BRACKET_COMMENT, bracketCommentBuf.toString()));
        }
    }
}

<HELP_COMMENT_NESTING> {
    {CloseBracketComment} { 
        bracketCommentNestingDepth--;
        bracketCommentBuf.append(yytext());
        if(bracketCommentNestingDepth == 0) {
            restoreState();
            return symbol(BRACKET_HELP_COMMENT, bracketCommentBuf.toString());
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

, { saveStateAndTransition(COMMA_TERMINATOR); }
; { saveStateAndTransition(SEMICOLON_TERMINATOR); }

<COMMA_TERMINATOR, SEMICOLON_TERMINATOR> {
    {OtherWhiteSpace} { /* ignore */ }

    {Comment} { commentQueue.add(symbol(COMMENT, yytext())); }

    {OpenBracketComment} { 
        saveStateAndTransition(COMMENT_NESTING);
        bracketCommentNestingDepth++;
        bracketCommentBuf = new StringBuffer(yytext());
    }
}

<COMMA_TERMINATOR> {
    {LineTerminator} { restoreState(); return symbol(COMMA_LINE_TERMINATOR); }
    .                { yypushback(1); restoreState(); return symbol(COMMA); }
}

<SEMICOLON_TERMINATOR> {
    {LineTerminator} { restoreState(); return symbol(SEMICOLON_LINE_TERMINATOR); }
    .                { yypushback(1); restoreState(); return symbol(SEMICOLON); }
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
"'" { return symbol(MTRANSPOSE); }
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
    {OpenClassDef} {
        saveStateAndTransition(CLASS);
        return symbol(CLASSDEF);
    }
}

<CLASS> {
    {CloseClassDef} {
        restoreState();
        return symbol(END); //NB: just return normal END token
    }
    
    properties { return symbol(PROPERTIES); }
    methods { return symbol(METHODS); }
    events { return symbol(EVENTS); }
}

<YYINITIAL, CLASS> {
    //from matlab "iskeyword" function
    break { return symbol(BREAK); }
    case { return symbol(CASE); }
    catch { return symbol(CATCH); }
    continue { return symbol(CONTINUE); }
    else { return symbol(ELSE); }
    elseif { return symbol(ELSEIF); }
    end { return symbol(END); }
    for { return symbol(FOR); }
    function { return symbol(FUNCTION); }
    global { return symbol(GLOBAL); }
    if { return symbol(IF); }
    otherwise { return symbol(OTHERWISE); }
    parfor { return symbol(PARFOR); }
    persistent { return symbol(PERSISTENT); }
    return { return symbol(RETURN); }
    switch { return symbol(SWITCH); }
    try { return symbol(TRY); }
    while { return symbol(WHILE); }
    
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

{CloseClassDef} { error(yytext() + " must be paired with a classdef keyword."); }

/* error fallback */
.|\n { error("Illegal character '" + yytext() + "'"); }

<<EOF>> {
            //don't finish scanning if there's an unclosed comment
            if(bracketCommentNestingDepth != 0) {
                error(bracketCommentNestingDepth + " levels of comments not closed");
            }
            return symbol(EOF);
        }