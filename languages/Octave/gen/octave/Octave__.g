lexer grammar Octave;
@members {
private static boolean isPreTransposeChar(int ch) {
    switch(ch) {
    case ')':
    case '}':
    case ']':
    case '_': //at the end of an identifier
    case '\'': //at the end of a transpose
               //NB: if this was at the end of a string, it would have been an escape instead of a close quote
        return true;
    default:
        return Character.isLetterOrDigit(ch); //at the end of an identifier
    }
}

//TODO-AC: This content is duplicated from the parser...maybe it can be factored out
private final List<octave.TranslationProblem> problems = new ArrayList<octave.TranslationProblem>();

public boolean hasProblem() {
    return !problems.isEmpty();
}

public List<octave.TranslationProblem> getProblems() {
    return java.util.Collections.unmodifiableList(problems);
}

//AC: this is a hackish way to prevent messages from being printed to stderr
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    problems.add(makeProblem(tokenNames, e));
}

private octave.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
    //change column to 1-based
    return new octave.TranslationProblem(e.line, e.charPositionInLine + 1, "LEXER: " + getErrorMessage(e, tokenNames));
}

private boolean couldBeFieldName = false;
}
@header {
package octave;
}

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 932
BREAK: { !couldBeFieldName }?=> 'break';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 933
CASE: { !couldBeFieldName }?=> 'case';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 934
CATCH: { !couldBeFieldName }?=> 'catch';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 935
CLASSDEF: { !couldBeFieldName }?=> 'classdef';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 936
CONTINUE: { !couldBeFieldName }?=> 'continue';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 937
ELSE: { !couldBeFieldName }?=> 'else';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 938
ELSEIF: { !couldBeFieldName }?=> 'elseif';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 939
END: { !couldBeFieldName }?=> 'end';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 940
FOR: { !couldBeFieldName }?=> 'for';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 941
FUNCTION: { !couldBeFieldName }?=> 'function';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 942
GLOBAL: { !couldBeFieldName }?=> 'global';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 943
IF: { !couldBeFieldName }?=> 'if';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 944
OTHERWISE: { !couldBeFieldName }?=> 'otherwise';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 945
PARFOR: { !couldBeFieldName }?=> 'parfor';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 946
PERSISTENT: { !couldBeFieldName }?=> 'persistent';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 947
RETURN: { !couldBeFieldName }?=> 'return';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 948
SWITCH: { !couldBeFieldName }?=> 'switch';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 949
TRY: { !couldBeFieldName }?=> 'try';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 950
WHILE: { !couldBeFieldName }?=> 'while';

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 952
fragment LETTER : 'a'..'z' | 'A'..'Z';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 953
fragment DIGIT : '0'..'9';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 954
IDENTIFIER : ('_' | '$' | LETTER) ('_' | '$' | LETTER | DIGIT)* { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 955
fragment INT_NUMBER : DIGIT+;
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 956
fragment SCI_EXP : ('d' | 'D' | 'e' | 'E') ('+' | '-')? DIGIT+;
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 957
fragment FP_NUMBER : (DIGIT+ '.' DIGIT*) | ('.' DIGIT+);
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 958
NUMBER : (INT_NUMBER | FP_NUMBER) SCI_EXP? ('i' | 'I' | 'j' | 'J')? { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 960
PLUS : '+' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 961
MINUS : '-' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 962
MTIMES : '*' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 963
ETIMES : '.*' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 964
MDIV : '/' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 965
EDIV : './' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 966
MLDIV : '\\' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 967
ELDIV : '.\\' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 968
MPOW : '^' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 969
EPOW : '.^' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 970
MTRANSPOSE : {isPreTransposeChar(input.LA(-1))}?=> '\'' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 971
ARRAYTRANSPOSE : '.\'' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 972
LE : '<=' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 973
GE : '>=' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 974
LT : '<' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 975
GT : '>' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 976
EQ : '==' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 977
NE : '~=' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 978
AND : '&' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 979
OR : '|' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 980
NOT : '~' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 981
SHORTAND : '&&' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 982
SHORTOR : '||' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 983
DOT : '.' { couldBeFieldName = true; }; //NB: TRUE
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 984
COMMA : ',' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 985
SEMICOLON : ';' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 986
COLON : ':' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 987
AT : '@' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 988
LPAREN : '(' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 989
RPAREN : ')' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 990
LCURLY : '{' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 991
RCURLY : '}' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 992
LSQUARE : '[' { couldBeFieldName = false; };
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 993
RSQUARE : ']' { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 995
ASSIGN : '=' { couldBeFieldName = false; };

//NB: matched AFTER transpose
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 998
fragment STRING_CHAR : ~('\'' | '\r' | '\n') | '\'\'';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 999
STRING : {!isPreTransposeChar(input.LA(-1))}?=>'\'' STRING_CHAR* ('\'' | (LINE_TERMINATOR)=> { $type = MISC; }) { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1001
fragment ANNOTATION_FILLER : ~'*' | '*' ~')';
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1002
ANNOTATION : '(*' ANNOTATION_FILLER* '*)' { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1004
fragment BRACKET_COMMENT_FILLER : ~'%' | '%' ~('{' | '}');
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1005
BRACKET_COMMENT : '%{' BRACKET_COMMENT_FILLER* (BRACKET_COMMENT BRACKET_COMMENT_FILLER*)* '%}';

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1007
fragment NOT_LINE_TERMINATOR : ~('\r' | '\n');
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1008
COMMENT : '%' | '%' ~('{' | '\r' | '\n') NOT_LINE_TERMINATOR*;

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1010
SHELL_COMMAND : '!' NOT_LINE_TERMINATOR* { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1012
LINE_TERMINATOR : '\r' '\n' | '\r' | '\n' { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1014
fragment ELLIPSIS_COMMENT : '...' NOT_LINE_TERMINATOR* LINE_TERMINATOR;
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1015
fragment OTHER_WHITESPACE : (' ' | '\t' | '\f')+;
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1016
FILLER : ((('...')=> ELLIPSIS_COMMENT) | OTHER_WHITESPACE)+; //NB: putting the predicate on the fragment doesn't work

//TODO-AC: this is a huge hack to get around an antlr issue.
//  When the lexer sees lookahead '..', it attempts to match FILLER, regardless of whether or not there's a third '.'
//  If there isn't, it simply fails instead of falling back to match DOT.
//  This prevents that from happening.  It is safe to return a nonsense token because '..' is never a valid sequence in matlab.
//TODO-AC: getting this rule to emit two DOT tokens would be much more satisfactory.
// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1023
DOUBLE_DOT : '..' { couldBeFieldName = false; };

// $ANTLR src "/home/valerie/Documents/mclab/mclab-core/languages/Octave/src/Octave.g" 1025
MISC : . { couldBeFieldName = false; };
