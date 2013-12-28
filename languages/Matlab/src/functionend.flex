/*
Copyright 2011 Andrew Casey, Soroush Radpour and McGill University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*/
//TODO-AC: if we want to do this without storing the whole file in memory (twice),
//  we'll have to do two passes.

package matlab;

%%

%public
%final
%class FunctionEndScanner

%unicode
%function translate
%type Result

%line
%column

//NB: start in START rather than YYINITIAL
%init{
    yybegin(START);
%init}

%{
  //// Return type /////////////////////////////////////////////////////////////
  
  public static abstract class Result {
  }
  
  public static class ProblemResult extends Result {
      private final java.util.List<TranslationProblem> problems;
      
      private ProblemResult(java.util.List<TranslationProblem> problems) {
          this.problems = problems;
      }
      
      public java.util.List<TranslationProblem> getProblems() {
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
  
  private void saveStateAndTransition(int newState) {
    stateStack.push(yystate());
    yybegin(newState);
  }
  
  private void restoreState() {
    yybegin(stateStack.pop());
  }
  
  private void clearStateStack() {
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
  
  private static enum BlockType { CLASS, FUNCTION, OTHER }
  
  private final java.util.Stack<BlockType> blockStack = new java.util.Stack<BlockType>();
  
  //// Text ////////////////////////////////////////////////////////////////////
  
  private OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
  private StringBuffer originalBuf = new StringBuffer();
  private StringBuffer translatedBuf = new StringBuffer();
  
  //true for identifier, number, bracketed, transpose
  private boolean transposeNext = false;
  
  private void append() {
    appendHelper(false, false, yytext());
    offsetTracker.advanceByTextSize(yytext());
  }
  
  private void appendTransposeNext() {
    appendHelper(true, false, yytext());
    offsetTracker.advanceByTextSize(yytext());
  }
  
  private void appendHelper(boolean transposeNext, boolean isInsertion, String text) {
    //if we return anything while in state FIELD_NAME, then restore state
    //i.e. only the first token after the dot is parsed specially
    if(yystate() == FIELD_NAME) {
        restoreState();
    }
    //if we saw something that forces the next single-quote to mean MTRANSPOSE, then set transposeNext
    this.transposeNext = transposeNext;
    
    if(!isInsertion) {
        originalBuf.append(text);
    }
    translatedBuf.append(text);
  }
  
  private void insertEndBeforeFunction() {
    int bufLength = originalBuf.length();
    char prevChar = originalBuf.charAt(bufLength - 1);
    if(prevChar == '\n' || prevChar == '\r') {
        int prevLineLength = 1;
        if(bufLength > 1 && originalBuf.charAt(bufLength - 2) != '\n') { //i.e. allow '\r'
            prevLineLength++;
        }
        for(int i = bufLength - 3; i >= 0; i--) {
            if(originalBuf.charAt(i) == '\n' || originalBuf.charAt(i) == '\r') {
                break;
            }
            prevLineLength++;
        }
        
        //e
        offsetTracker.recordOffsetChange(-1, prevLineLength - 1);
        offsetTracker.advanceInLine(1);
        
        //n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //d
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //\n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceToNewLine(1, 1);
        
        //following text
        offsetTracker.recordOffsetChange(0, 0);
    } else {
        int prevLineLength = 1;
        for(int i = bufLength - 1; i >= 0; i--) {
            if(originalBuf.charAt(i) == '\n' || originalBuf.charAt(i) == '\r') {
                break;
            }
            prevLineLength++;
        }
        
        //e
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //d
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //\n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceToNewLine(1, 1);
        
        //following text
        offsetTracker.recordOffsetChange(-1, prevLineLength - 1);
    }
    appendHelper(false, true, "end\n");
  }
  
  private void insertFinalEnd() {
    int bufLength = originalBuf.length();
    char prevChar = originalBuf.charAt(bufLength - 1);
    if(prevChar == '\n' || prevChar == '\r') {
        int prevLineLength = 1;
        if(bufLength > 1 && originalBuf.charAt(bufLength - 2) != '\n') { //i.e. allow '\r'
            prevLineLength++;
        }
        for(int i = bufLength - 3; i >= 0; i--) {
            if(originalBuf.charAt(i) == '\n' || originalBuf.charAt(i) == '\r') {
                break;
            }
            prevLineLength++;
        }
        
        //\n
        offsetTracker.recordOffsetChange(-1, prevLineLength - 1);
        offsetTracker.advanceToNewLine(1, 1);
        
        //e
        offsetTracker.recordOffsetChange(-1, prevLineLength - 1);
        offsetTracker.advanceInLine(1);
        
        //n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //d
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
    } else {
        //\n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceToNewLine(1, 1);
        
        //e
        offsetTracker.recordOffsetChange(-1, yycolumn - 1); //yycolumn == prevLineLength - 1
        offsetTracker.advanceInLine(1);
        
        //n
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        //d
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
    }
    appendHelper(false, true, "\nend"); //NB: newline BEFORE 'end', not after
  }
  
  //// Optional end ////////////////////////////////////////////////////////////
  
  private FunctionEndTranslationProblem makeProblem() {
    return new FunctionEndTranslationProblem(yyline + 1, yycolumn + 1, "Function lacks an explicit end." + 
        "  If any function has an explicit end, then all must.");
  }
  
  private final java.util.List<TranslationProblem> unendedFunctions = new java.util.ArrayList<TranslationProblem>();
  
  private int numFunctions = 0;
  
  private void startFunction() {
    //assertion
    if(!yytext().endsWith("function")) {
        throw new RuntimeException("Programmer error: expected yytext() to end with 'function' - " + yytext());
    }
    
    //stmt separator and filler
    int suffixLength = "function".length();
    String prefix = yytext().substring(0, yylength() - suffixLength);
    appendHelper(false, false, prefix);
    offsetTracker.advanceByTextSize(prefix);
    
    //insert end if there is a preceding function
    if(numFunctions > 0) { //check before incrementing
        insertEndBeforeFunction();
    }
    
    //function keyword
    appendHelper(false, false, "function");
    offsetTracker.advanceInLine(suffixLength);
    
    numFunctions++;
    unendedFunctions.add(makeProblem());
  }
  
  private void endFunction() {
    unendedFunctions.remove(unendedFunctions.size() - 1);
  }
  
  private Result result = null;
%}

LineTerminator = \r|\n|\r\n

//NB: acceptable to conflict with ... - matlab just treats .... as a comment containing .
Ellipsis = \.\.\.
EscapedLineTerminator = {Ellipsis}.*{LineTerminator}

Letter = [a-zA-Z]
Digit = [0-9]
HexDigit = {Digit} | [a-fA-F]
Identifier = ([_$] | {Letter}) ([_$] | {Letter} | {Digit})*
SciExp = [EeDd][+-]?{Digit}+
Imaginary = [iIjJ]
IntNumber = {Digit}+
FPNumber = (({Digit}+\.?{Digit}*) | (\.?{Digit}+)){SciExp}?
HexNumber = 0[xX]{HexDigit}+
Number = ({IntNumber} | {FPNumber} | {HexNumber}) {Imaginary}?

Comment=% | %[^{\r\n].*
OpenBracketComment = %\{
CloseBracketComment = %\}

ShellCommand=[!].*

StmtTerminator = [\r\n,;]
Filler = [\t\f ] | {EscapedLineTerminator} 

KeywordPrefix= {StmtTerminator} {Filler}*

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
//initial state - used to catch initial keywords
%xstate START

%%

//AC: similar to extractor.flex but only keep the things that can hide keywords
//  (e.g. comments) and the things needed to distinguish strings from transposes

//TODO-AC: anything that doesn't call append might have to explicitly set transposeNext (probably to false)

//this is a special state for catching keywords requiring end bracketing that appear at the beginning of the file
//usually we require that they be preceded by a stmt separator, but these are preceded by the beginning of the file instead
//leave this state as soon as any non-filler is encountered
<START> {
    {Filler} { append(); }
    
    (classdef|aspect) /{Filler} {
        append();
        blockStack.push(BlockType.CLASS); 
        saveStateAndTransition(INSIDE_CLASS);
    }
    
    function /{Filler} { startFunction(); blockStack.push(BlockType.FUNCTION); yybegin(YYINITIAL); }
    
    ( for
    | if
    | parfor
    | switch
    | try
    | while )  /{Filler}  { append(); blockStack.push(BlockType.OTHER); yybegin(YYINITIAL); }
    
    . | \n { yypushback(1); yybegin(YYINITIAL); }
    
    <<EOF>> { yybegin(YYINITIAL); }
}

//single-line comments
{Comment} { append(); }

//... comment
{EscapedLineTerminator} { append(); }

//bang (!) syntax
{ShellCommand} { append(); }

{Number} { appendTransposeNext(); }

//MTRANSPOSE or STRING (start)
"'" {
    //NB: cannot be a string if we're expecting a transpose - even if string is a longer match
    if(transposeNext) {
        appendTransposeNext();
    } else {
        append();
        saveStateAndTransition(INSIDE_STRING);
    }
}

//remainder of string literal (i.e. after initial single quote)
<INSIDE_STRING> {
    "''" { append(); }
    "'" {
        append();
        restoreState();
    }
    {LineTerminator} { append(); restoreState(); } //NB: let another pass handle this: unterminated string literal
    . { append(); }
    <<EOF>> { clearStateStack(); } //NB: let another pass handle this: unterminated string literal
}

//"'" { appendTransposeNext(); } //mixed with string rule above
".'" { appendTransposeNext(); }

//start multiline comment
{OpenBracketComment} {
    append();
    commentNestingDepth++;
    saveStateAndTransition(INSIDE_BRACKET_COMMENT);
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
        appendTransposeNext();
        bracketNestingDepth--;
        if(bracketNestingDepth == 0) {
            restoreState();
        }
    }
    <<EOF>> { clearStateStack(); } //NB: let another pass handle this: unterminated brackets
}

<YYINITIAL> {
    {KeywordPrefix} (classdef|aspect)  /{Filler} {
        append();
        blockStack.push(BlockType.CLASS); 
        saveStateAndTransition(INSIDE_CLASS);
    }
    
    {KeywordPrefix} end /!({Digit}|{Letter}) {
        append();
        if(!blockStack.isEmpty()) {
            if(blockStack.peek() == BlockType.FUNCTION) {
                endFunction();
            }
            blockStack.pop();
        }
    }
}

<INSIDE_CLASS> {
    {KeywordPrefix} (classdef|aspect) /{Filler} { append(); blockStack.push(BlockType.OTHER); } //don't push CLASS or we won't know when to leave this state
    
    {KeywordPrefix} end /!({Digit}|{Letter}) {
        append();
        if(!blockStack.isEmpty()) {
            if(blockStack.peek() == BlockType.FUNCTION) {
                endFunction();
            } else if(blockStack.peek() == BlockType.CLASS) {
                restoreState();
            }
            blockStack.pop();
        }
    }
    
    {KeywordPrefix} ( methods | properties | events ) { append(); blockStack.push(BlockType.OTHER); }
}

//i.e. not in FIELD_NAME
<YYINITIAL, INSIDE_CLASS> {
    //from matlab "iskeyword" function
    
    {KeywordPrefix} function /{Filler}  { startFunction(); blockStack.push(BlockType.FUNCTION); }
    
    {KeywordPrefix} ( for
                    | if
                    | parfor
                    | switch
                    | try
                    | while )  /{Filler}  { append(); blockStack.push(BlockType.OTHER); }
    
    //distinguish these from identifiers because their action is different
    {KeywordPrefix} ( break
                    | catch
                    | continue
                    | else
                    | elseif
                    | global
                    | otherwise
                    | persistent
                    | return ) { append(); }
    
    //NB: lower precedence than keywords
    {Identifier} { appendTransposeNext(); }
    
    //NB: lower precedence than ellipsis
    \. {
        //NB: have to change the state AFTER calling append
        append();
        saveStateAndTransition(FIELD_NAME);
    }
}

//ignore keywords - we just saw a dot
<FIELD_NAME> {
    {Identifier} { appendTransposeNext(); }
    
    //to avoid transitioning to the same state
    \. { append(); }
}

/* error fallback */
.|\n { append(); }

<<EOF>> {
    if(result == null) {
        if(numFunctions > 0) {
            insertFinalEnd();
        }
        
        if(numFunctions == 0 || unendedFunctions.isEmpty()) { //all functions have an 'end'
            result = new NoChangeResult();
        } else if(unendedFunctions.size() == numFunctions) { //no function has an 'end'
            result = new TranslationResult(offsetTracker.buildPositionMap(), translatedBuf.toString());
        } else {
            result = new ProblemResult(unendedFunctions);
        }
    }
    return result;
}
