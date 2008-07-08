package natlab;

%%

%public
%final
%class FunctionEndScanner

%unicode
%function translate
%type Result

%line
%column

%{
  //// Return type /////////////////////////////////////////////////////////////
  
  public static abstract class Result {
  }
  
  public static class ProblemResult extends Result {
      private final List<TranslationProblem> problems;
      
      private ProblemResult(List<TranslationProblem> problems) {
          this.problems = problems;
      }
      
      public List<TranslationProblem> getProblems() {
          return problems;
      }
      
      public boolean hasProblem() {
          return !problems.isEmpty();
      }
  }
  
  public static class NoChangeResult extends Result {
      //TODO-AC: original text and trivial map?
  }
  
  public static class TranslationResult extends Result {
      private final PositionMap posMap;
      private final String text;
      
      private TranslationResult(PositionMap posMap, String text) {
          this.posMap = posMap;
          this.text = text;
      }
      
      public PositionMap getPositionMap() {
          return posMap;
      }
      
      public String getText() {
          return text;
      }
  }
  
  //// State transitions ///////////////////////////////////////////////////////
  
  //most of our states are used for bracketing
  //this gives us a way to nest bracketing states
  private java.util.Stack<Integer> stateStack = new java.util.Stack<Integer>();
  
  void saveStateAndTransition(int newState) {
    stateStack.push(yystate());
    yybegin(newState);
  }
  
  void restoreState() {
    yybegin(stateStack.pop());
  }
  
  void clearStateStack() {
    yybegin(YYINITIAL);
    stateStack.clear();
  }
  
  //// Bracket nesting /////////////////////////////////////////////////////////
  
  //number of ')', '}', or ']' expected
  private int bracketNestingDepth = 0;
  
  //// Comment nesting /////////////////////////////////////////////////////////
  
  //number of '%}'s expected
  private int commentNestingDepth = 0;
  
  //// End-bracketing //////////////////////////////////////////////////////////
  
  //number of 'end's expected
  private int endNestingDepth = 0;
  
  //// Text ////////////////////////////////////////////////////////////////////
  
  private StringBuffer buf = new StringBuffer();
  
  //true for identifier, number, bracketed, transpose
  private boolean transposeNext = false;
  
  private void append() {
    append(false);
  }
  
  private void append(boolean transposeNext) {
    //if we return anything while in state FIELD_NAME, then restore state
    //i.e. only the first token after the dot is parsed specially
    if(yystate() == FIELD_NAME) {
        restoreState();
    }
    //if we saw something that forces the next single-quote to mean MTRANSPOSE, then set transposeNext
    this.transposeNext = transposeNext;
    
    buf.append(yytext());
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

//parsing the bit after a DOT
%state FIELD_NAME
//within a classdef
%state INSIDE_CLASS
//within a string literal
%xstate INSIDE_STRING
//within (), {}, or []
%xstate INSIDE_BRACKETS
//within a bracket comment (i.e. %{)
%xstate INSIDE_BRACKET_COMMENT

%%

//AC: similar to extractor.flex but only keep the things that can hide keywords
//  (e.g. comments) and the things needed to distinguish strings from transposes

//TODO-AC: anything that doesn't call append might have to explicitly set transposeNext (probably to false)

//single-line comments
{Comment} { append(); }

//... comment
{EscapedLineTerminator} { append(); }

//bang (!) syntax
{ShellCommand} { append(); }

{Number} { append(true); }

//MTRANSPOSE or STRING (start)
"'" {
    //NB: cannot be a string if we're expecting a transpose - even if string is a longer match
    if(transposeNext) {
        append(true);
    } else {
        saveStateAndTransition(INSIDE_STRING);
        append();
    }
}

//remainder of string literal (i.e. after initial single quote)
<INSIDE_STRING> {
    "''" { append(); }
    "'" {
        append();
        restoreState();
    }
    . { append(); }
    <<EOF>> { clearStateStack(); } //NB: let another pass handle this: unterminated string literal
}

//"'" { append(true); } //mixed with string rule above
".'" { append(true); }

//start multiline comment
{OpenBracketComment} {
    append();
    saveStateAndTransition(INSIDE_BRACKET_COMMENT);
    commentNestingDepth++;
}

//continue multiline comment
<INSIDE_BRACKET_COMMENT> {
    [^%]+ { append(); }
    % { append(); }
    {OpenBracketComment} { append(); commentNestingDepth++; }
    {CloseBracketComment} { 
        append();
        commentNestingDepth--;
        if(commentNestingDepth == 0) {
            restoreState();
        }
    }
    <<EOF>> { clearStateStack(); } //NB: let another pass handle this: unterminated bracket comment
}

//start parenthesized section
\( | \{ | \[ {
    append();
    bracketNestingDepth++;
    saveStateAndTransition(INSIDE_BRACKETS);
}

//continue parenthesized section
<INSIDE_BRACKETS> {
    [^\(\)\{\}\[\]]+ { append(); }
    \( | \{ | \[ { append(); bracketNestingDepth++; }
    \) | \} | \] {
        append(true);
        bracketNestingDepth--;
        if(bracketNestingDepth == 0) {
            restoreState();
        }
    }
    <<EOF>> { clearStateStack(); } //NB: let another pass handle this: unterminated brackets
}

<YYINITIAL> {
    classdef {
        append();
        endNestingDepth++; 
        saveStateAndTransition(INSIDE_CLASS);
    }
    
    end { endNestingDepth--; append(); }
}

<INSIDE_CLASS> {
    classdef { endNestingDepth++; append(); }
    
    end {
        endNestingDepth--;
        if(endNestingDepth == 0) { //safe test since classdef must be top-level
            restoreState();
        }
        append();
    }
    
    methods { endNestingDepth++; append(); }
    properties { endNestingDepth++; append(); }
    events { endNestingDepth++; append(); }
}

//i.e. not in FIELD_NAME
<YYINITIAL, INSIDE_CLASS> {
    //from matlab "iskeyword" function
    
    case { endNestingDepth++; append(); }
    for { endNestingDepth++; append(); }
    function { endNestingDepth++; append(); }
    if { endNestingDepth++; append(); }
    parfor { endNestingDepth++; append(); }
    switch { endNestingDepth++; append(); }
    try { endNestingDepth++; append(); }
    while { endNestingDepth++; append(); }
    
    break { append(); }
    catch { append(); }
    continue { append(); }
    else { append(); }
    elseif { append(); }
    end { append(); }
    global { append(); }
    otherwise { append(); }
    persistent { append(); }
    return { append(); }
    
    //NB: lower precedence than keywords
    {Identifier} { append(true); }
    
    //NB: lower precedence than ellipsis
    \. {
            //NB: have to change the state AFTER calling append
            append();
            saveStateAndTransition(FIELD_NAME);
    }
}

//ignore keywords - we just saw a dot
<FIELD_NAME> {
    {Identifier} { append(true); }
    
    //to avoid transitioning to the same state
    \. { append(); }
}

/* error fallback */
.|\n { append(); }

<<EOF>> {
    //TODO-AC
}