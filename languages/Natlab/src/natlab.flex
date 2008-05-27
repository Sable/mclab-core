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
  private Symbol symbol(short type) {
    if(yystate() == FIELD_NAME) {
        yybegin(YYINITIAL);
    }
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength());
  }
  private Symbol symbol(short type, Object value) {
    if(yystate() == FIELD_NAME) {
        yybegin(YYINITIAL);
    }
    return new Symbol(type, yyline + 1, yycolumn + 1, yylength(), value);
  }
  
  private void error(String msg) throws Scanner.Exception {
    throw new Scanner.Exception(yyline + 1, yycolumn + 1, msg);
  }
  
  private void error(String msg, int columnOffset) throws Scanner.Exception {
    throw new Scanner.Exception(yyline + 1, yycolumn + 1 + columnOffset, msg);
  }
  
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
  
  private int bracketCommentNestingDepth = 0;
  private StringBuffer bracketCommentBuf = null;
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
DecimalNumber = (({Digit}+\.?{Digit}*) | (\.?{Digit}+)){SciExp}?{Imaginary}?
HexNumber = 0[xX]{HexDigit}+{Imaginary}?
Number = {DecimalNumber} | {HexNumber}

CommentSymbol = %
HelpComment={CommentSymbol}{CommentSymbol}.*
Comment={CommentSymbol} | {CommentSymbol}[^{].*
OpenBracketComment = %\{
CloseBracketComment = %\}

ShellCommand=[!].*

String=[']([^'\r\n] | [']['])*[']

%state FIELD_NAME
%xstate BRACKET_COMMENT

%%

{EscapedLineTerminator} { return symbol(COMMENT, yytext().substring(yytext().indexOf("..."), yylength() - 1)); }
{OtherWhiteSpace} { /* ignore */ }

{LineTerminator} { return symbol(LINE_TERMINATOR); }

{Number} { return symbol(NUMBER, yytext()); }

{String} { validateEscapeSequences(yytext()); return symbol(STRING, yytext().substring(1, yylength() - 1)); }

{HelpComment} { return symbol(HELP_COMMENT, yytext()); }
{Comment} { return symbol(COMMENT, yytext()); }

{OpenBracketComment} { yybegin(BRACKET_COMMENT); bracketCommentNestingDepth++; bracketCommentBuf = new StringBuffer(yytext()); }

<BRACKET_COMMENT> {
    [^%]+ { bracketCommentBuf.append(yytext()); }
    {CommentSymbol} { bracketCommentBuf.append(yytext()); }
    {OpenBracketComment} { bracketCommentNestingDepth++; bracketCommentBuf.append(yytext()); }
    {CloseBracketComment} { 
        bracketCommentNestingDepth--;
        bracketCommentBuf.append(yytext());
        if(bracketCommentNestingDepth == 0) {
            yybegin(YYINITIAL);
            return symbol(COMMENT, bracketCommentBuf.toString());
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

, { return symbol(COMMA); }
; { return symbol(SEMICOLON); }

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

<YYINITIAL> {
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
            yybegin(FIELD_NAME);
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
            if(bracketCommentNestingDepth != 0) {
                error(bracketCommentNestingDepth + " levels of comments not closed");
            }
            return symbol(EOF);
        }