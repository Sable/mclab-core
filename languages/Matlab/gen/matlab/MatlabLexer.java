// $ANTLR 3.0.1 /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g 2015-03-31 13:46:25

package matlab;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class MatlabLexer extends Lexer {
    public static final int ETIMES=12;
    public static final int ELSEIF=40;
    public static final int LETTER=67;
    public static final int ELDIV=16;
    public static final int LT=21;
    public static final int MINUS=10;
    public static final int RSQUARE=64;
    public static final int MLDIV=15;
    public static final int SEMICOLON=56;
    public static final int ANNOTATION=65;
    public static final int BREAK=34;
    public static final int ELSE=39;
    public static final int TRY=51;
    public static final int IF=45;
    public static final int SCI_EXP=70;
    public static final int MTRANSPOSE=60;
    public static final int Tokens=80;
    public static final int MDIV=13;
    public static final int NUMBER=31;
    public static final int ARRAYTRANSPOSE=61;
    public static final int ELLIPSIS_COMMENT=76;
    public static final int LPAREN=5;
    public static final int FOR=42;
    public static final int BRACKET_COMMENT=59;
    public static final int DOT=62;
    public static final int RPAREN=6;
    public static final int EDIV=14;
    public static final int EQ=23;
    public static final int FUNCTION=43;
    public static final int FP_NUMBER=71;
    public static final int CASE=35;
    public static final int INT_NUMBER=69;
    public static final int NOT=53;
    public static final int AT=29;
    public static final int PARFOR=47;
    public static final int NE=24;
    public static final int AND=25;
    public static final int OTHERWISE=46;
    public static final int ANNOTATION_FILLER=73;
    public static final int BRACKET_COMMENT_FILLER=74;
    public static final int END=41;
    public static final int SWITCH=50;
    public static final int SHORTOR=28;
    public static final int PLUS=9;
    public static final int NOT_LINE_TERMINATOR=75;
    public static final int OTHER_WHITESPACE=77;
    public static final int MISC=79;
    public static final int SHELL_COMMAND=66;
    public static final int ASSIGN=4;
    public static final int STRING_CHAR=72;
    public static final int COMMENT=58;
    public static final int RETURN=49;
    public static final int DOUBLE_DOT=78;
    public static final int CATCH=36;
    public static final int GLOBAL=44;
    public static final int IDENTIFIER=7;
    public static final int EOF=-1;
    public static final int MTIMES=11;
    public static final int GE=20;
    public static final int RCURLY=63;
    public static final int SHORTAND=27;
    public static final int COMMA=55;
    public static final int PERSISTENT=48;
    public static final int OR=26;
    public static final int CLASSDEF=37;
    public static final int LINE_TERMINATOR=57;
    public static final int LCURLY=33;
    public static final int COLON=30;
    public static final int FILLER=8;
    public static final int GT=22;
    public static final int DIGIT=68;
    public static final int MPOW=17;
    public static final int CONTINUE=38;
    public static final int LSQUARE=32;
    public static final int EPOW=18;
    public static final int LE=19;
    public static final int STRING=54;
    public static final int WHILE=52;

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
    private final List<matlab.TranslationProblem> problems = new ArrayList<matlab.TranslationProblem>();

    public boolean hasProblem() {
        return !problems.isEmpty();
    }

    public List<matlab.TranslationProblem> getProblems() {
        return java.util.Collections.unmodifiableList(problems);
    }

    //AC: this is a hackish way to prevent messages from being printed to stderr
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        problems.add(makeProblem(tokenNames, e));
    }

    private matlab.TranslationProblem makeProblem(String[] tokenNames, RecognitionException e) {
        //change column to 1-based
        return new matlab.TranslationProblem(e.line, e.charPositionInLine + 1, "LEXER: " + getErrorMessage(e, tokenNames));
    }

    private boolean couldBeFieldName = false;

    public MatlabLexer() {;} 
    public MatlabLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[79+1];
     }
    public String getGrammarFileName() { return "/home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g"; }

    // $ANTLR start BREAK
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:932:6: ({...}? => 'break' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:932:8: {...}? => 'break'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "BREAK", " !couldBeFieldName ");
            }
            match("break"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BREAK

    // $ANTLR start CASE
    public final void mCASE() throws RecognitionException {
        try {
            int _type = CASE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:933:5: ({...}? => 'case' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:933:7: {...}? => 'case'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "CASE", " !couldBeFieldName ");
            }
            match("case"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CASE

    // $ANTLR start CATCH
    public final void mCATCH() throws RecognitionException {
        try {
            int _type = CATCH;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:934:6: ({...}? => 'catch' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:934:8: {...}? => 'catch'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "CATCH", " !couldBeFieldName ");
            }
            match("catch"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CATCH

    // $ANTLR start CLASSDEF
    public final void mCLASSDEF() throws RecognitionException {
        try {
            int _type = CLASSDEF;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:935:9: ({...}? => 'classdef' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:935:11: {...}? => 'classdef'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "CLASSDEF", " !couldBeFieldName ");
            }
            match("classdef"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CLASSDEF

    // $ANTLR start CONTINUE
    public final void mCONTINUE() throws RecognitionException {
        try {
            int _type = CONTINUE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:936:9: ({...}? => 'continue' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:936:11: {...}? => 'continue'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "CONTINUE", " !couldBeFieldName ");
            }
            match("continue"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONTINUE

    // $ANTLR start ELSE
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:937:5: ({...}? => 'else' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:937:7: {...}? => 'else'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "ELSE", " !couldBeFieldName ");
            }
            match("else"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELSE

    // $ANTLR start ELSEIF
    public final void mELSEIF() throws RecognitionException {
        try {
            int _type = ELSEIF;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:938:7: ({...}? => 'elseif' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:938:9: {...}? => 'elseif'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "ELSEIF", " !couldBeFieldName ");
            }
            match("elseif"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELSEIF

    // $ANTLR start END
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:939:4: ({...}? => 'end' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:939:6: {...}? => 'end'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "END", " !couldBeFieldName ");
            }
            match("end"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end END

    // $ANTLR start FOR
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:940:4: ({...}? => 'for' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:940:6: {...}? => 'for'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "FOR", " !couldBeFieldName ");
            }
            match("for"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FOR

    // $ANTLR start FUNCTION
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:941:9: ({...}? => 'function' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:941:11: {...}? => 'function'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "FUNCTION", " !couldBeFieldName ");
            }
            match("function"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start GLOBAL
    public final void mGLOBAL() throws RecognitionException {
        try {
            int _type = GLOBAL;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:942:7: ({...}? => 'global' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:942:9: {...}? => 'global'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "GLOBAL", " !couldBeFieldName ");
            }
            match("global"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GLOBAL

    // $ANTLR start IF
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:943:3: ({...}? => 'if' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:943:5: {...}? => 'if'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "IF", " !couldBeFieldName ");
            }
            match("if"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IF

    // $ANTLR start OTHERWISE
    public final void mOTHERWISE() throws RecognitionException {
        try {
            int _type = OTHERWISE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:944:10: ({...}? => 'otherwise' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:944:12: {...}? => 'otherwise'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "OTHERWISE", " !couldBeFieldName ");
            }
            match("otherwise"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OTHERWISE

    // $ANTLR start PARFOR
    public final void mPARFOR() throws RecognitionException {
        try {
            int _type = PARFOR;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:945:7: ({...}? => 'parfor' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:945:9: {...}? => 'parfor'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "PARFOR", " !couldBeFieldName ");
            }
            match("parfor"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PARFOR

    // $ANTLR start PERSISTENT
    public final void mPERSISTENT() throws RecognitionException {
        try {
            int _type = PERSISTENT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:946:11: ({...}? => 'persistent' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:946:13: {...}? => 'persistent'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "PERSISTENT", " !couldBeFieldName ");
            }
            match("persistent"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PERSISTENT

    // $ANTLR start RETURN
    public final void mRETURN() throws RecognitionException {
        try {
            int _type = RETURN;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:947:7: ({...}? => 'return' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:947:9: {...}? => 'return'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "RETURN", " !couldBeFieldName ");
            }
            match("return"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RETURN

    // $ANTLR start SWITCH
    public final void mSWITCH() throws RecognitionException {
        try {
            int _type = SWITCH;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:948:7: ({...}? => 'switch' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:948:9: {...}? => 'switch'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "SWITCH", " !couldBeFieldName ");
            }
            match("switch"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SWITCH

    // $ANTLR start TRY
    public final void mTRY() throws RecognitionException {
        try {
            int _type = TRY;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:949:4: ({...}? => 'try' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:949:6: {...}? => 'try'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "TRY", " !couldBeFieldName ");
            }
            match("try"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRY

    // $ANTLR start WHILE
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:950:6: ({...}? => 'while' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:950:8: {...}? => 'while'
            {
            if ( !( !couldBeFieldName ) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "WHILE", " !couldBeFieldName ");
            }
            match("while"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHILE

    // $ANTLR start LETTER
    public final void mLETTER() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:952:17: ( 'a' .. 'z' | 'A' .. 'Z' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end LETTER

    // $ANTLR start DIGIT
    public final void mDIGIT() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:953:16: ( '0' .. '9' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:953:18: '0' .. '9'
            {
            matchRange('0','9'); if (failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end DIGIT

    // $ANTLR start IDENTIFIER
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:954:12: ( ( '_' | '$' | LETTER ) ( '_' | '$' | LETTER | DIGIT )* )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:954:14: ( '_' | '$' | LETTER ) ( '_' | '$' | LETTER | DIGIT )*
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:954:35: ( '_' | '$' | LETTER | DIGIT )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='$'||(LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();
            	    failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IDENTIFIER

    // $ANTLR start INT_NUMBER
    public final void mINT_NUMBER() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:955:21: ( ( DIGIT )+ )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:955:23: ( DIGIT )+
            {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:955:23: ( DIGIT )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:955:23: DIGIT
            	    {
            	    mDIGIT(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end INT_NUMBER

    // $ANTLR start SCI_EXP
    public final void mSCI_EXP() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:956:18: ( ( 'd' | 'D' | 'e' | 'E' ) ( '+' | '-' )? ( DIGIT )+ )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:956:20: ( 'd' | 'D' | 'e' | 'E' ) ( '+' | '-' )? ( DIGIT )+
            {
            if ( (input.LA(1)>='D' && input.LA(1)<='E')||(input.LA(1)>='d' && input.LA(1)<='e') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:956:44: ( '+' | '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='+'||LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:956:57: ( DIGIT )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:956:57: DIGIT
            	    {
            	    mDIGIT(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end SCI_EXP

    // $ANTLR start FP_NUMBER
    public final void mFP_NUMBER() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:20: ( ( ( DIGIT )+ '.' ( DIGIT )* ) | ( '.' ( DIGIT )+ ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                alt8=1;
            }
            else if ( (LA8_0=='.') ) {
                alt8=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("957:10: fragment FP_NUMBER : ( ( ( DIGIT )+ '.' ( DIGIT )* ) | ( '.' ( DIGIT )+ ) );", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:22: ( ( DIGIT )+ '.' ( DIGIT )* )
                    {
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:22: ( ( DIGIT )+ '.' ( DIGIT )* )
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:23: ( DIGIT )+ '.' ( DIGIT )*
                    {
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:23: ( DIGIT )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:23: DIGIT
                    	    {
                    	    mDIGIT(); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    match('.'); if (failed) return ;
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:34: ( DIGIT )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:34: DIGIT
                    	    {
                    	    mDIGIT(); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:44: ( '.' ( DIGIT )+ )
                    {
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:44: ( '.' ( DIGIT )+ )
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:45: '.' ( DIGIT )+
                    {
                    match('.'); if (failed) return ;
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:49: ( DIGIT )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:957:49: DIGIT
                    	    {
                    	    mDIGIT(); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end FP_NUMBER

    // $ANTLR start NUMBER
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:8: ( ( INT_NUMBER | FP_NUMBER ) ( SCI_EXP )? ( 'i' | 'I' | 'j' | 'J' )? )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:10: ( INT_NUMBER | FP_NUMBER ) ( SCI_EXP )? ( 'i' | 'I' | 'j' | 'J' )?
            {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:10: ( INT_NUMBER | FP_NUMBER )
            int alt9=2;
            alt9 = dfa9.predict(input);
            switch (alt9) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:11: INT_NUMBER
                    {
                    mINT_NUMBER(); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:24: FP_NUMBER
                    {
                    mFP_NUMBER(); if (failed) return ;

                    }
                    break;

            }

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:35: ( SCI_EXP )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>='D' && LA10_0<='E')||(LA10_0>='d' && LA10_0<='e')) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:35: SCI_EXP
                    {
                    mSCI_EXP(); if (failed) return ;

                    }
                    break;

            }

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:958:44: ( 'i' | 'I' | 'j' | 'J' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>='I' && LA11_0<='J')||(LA11_0>='i' && LA11_0<='j')) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:
                    {
                    if ( (input.LA(1)>='I' && input.LA(1)<='J')||(input.LA(1)>='i' && input.LA(1)<='j') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NUMBER

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:960:6: ( '+' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:960:8: '+'
            {
            match('+'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:961:7: ( '-' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:961:9: '-'
            {
            match('-'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start MTIMES
    public final void mMTIMES() throws RecognitionException {
        try {
            int _type = MTIMES;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:962:8: ( '*' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:962:10: '*'
            {
            match('*'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MTIMES

    // $ANTLR start ETIMES
    public final void mETIMES() throws RecognitionException {
        try {
            int _type = ETIMES;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:963:8: ( '.*' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:963:10: '.*'
            {
            match(".*"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ETIMES

    // $ANTLR start MDIV
    public final void mMDIV() throws RecognitionException {
        try {
            int _type = MDIV;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:964:6: ( '/' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:964:8: '/'
            {
            match('/'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MDIV

    // $ANTLR start EDIV
    public final void mEDIV() throws RecognitionException {
        try {
            int _type = EDIV;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:965:6: ( './' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:965:8: './'
            {
            match("./"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EDIV

    // $ANTLR start MLDIV
    public final void mMLDIV() throws RecognitionException {
        try {
            int _type = MLDIV;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:966:7: ( '\\\\' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:966:9: '\\\\'
            {
            match('\\'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MLDIV

    // $ANTLR start ELDIV
    public final void mELDIV() throws RecognitionException {
        try {
            int _type = ELDIV;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:967:7: ( '.\\\\' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:967:9: '.\\\\'
            {
            match(".\\"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELDIV

    // $ANTLR start MPOW
    public final void mMPOW() throws RecognitionException {
        try {
            int _type = MPOW;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:968:6: ( '^' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:968:8: '^'
            {
            match('^'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MPOW

    // $ANTLR start EPOW
    public final void mEPOW() throws RecognitionException {
        try {
            int _type = EPOW;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:969:6: ( '.^' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:969:8: '.^'
            {
            match(".^"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EPOW

    // $ANTLR start MTRANSPOSE
    public final void mMTRANSPOSE() throws RecognitionException {
        try {
            int _type = MTRANSPOSE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:970:12: ({...}? => '\\'' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:970:14: {...}? => '\\''
            {
            if ( !(isPreTransposeChar(input.LA(-1))) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "MTRANSPOSE", "isPreTransposeChar(input.LA(-1))");
            }
            match('\''); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MTRANSPOSE

    // $ANTLR start ARRAYTRANSPOSE
    public final void mARRAYTRANSPOSE() throws RecognitionException {
        try {
            int _type = ARRAYTRANSPOSE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:971:16: ( '.\\'' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:971:18: '.\\''
            {
            match(".\'"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ARRAYTRANSPOSE

    // $ANTLR start LE
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:972:4: ( '<=' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:972:6: '<='
            {
            match("<="); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LE

    // $ANTLR start GE
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:973:4: ( '>=' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:973:6: '>='
            {
            match(">="); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GE

    // $ANTLR start LT
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:974:4: ( '<' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:974:6: '<'
            {
            match('<'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LT

    // $ANTLR start GT
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:975:4: ( '>' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:975:6: '>'
            {
            match('>'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GT

    // $ANTLR start EQ
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:976:4: ( '==' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:976:6: '=='
            {
            match("=="); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQ

    // $ANTLR start NE
    public final void mNE() throws RecognitionException {
        try {
            int _type = NE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:977:4: ( '~=' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:977:6: '~='
            {
            match("~="); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NE

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:978:5: ( '&' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:978:7: '&'
            {
            match('&'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:979:4: ( '|' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:979:6: '|'
            {
            match('|'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:980:5: ( '~' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:980:7: '~'
            {
            match('~'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start SHORTAND
    public final void mSHORTAND() throws RecognitionException {
        try {
            int _type = SHORTAND;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:981:10: ( '&&' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:981:12: '&&'
            {
            match("&&"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SHORTAND

    // $ANTLR start SHORTOR
    public final void mSHORTOR() throws RecognitionException {
        try {
            int _type = SHORTOR;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:982:9: ( '||' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:982:11: '||'
            {
            match("||"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SHORTOR

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:983:5: ( '.' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:983:7: '.'
            {
            match('.'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = true; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:984:7: ( ',' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:984:9: ','
            {
            match(','); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start SEMICOLON
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:985:11: ( ';' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:985:13: ';'
            {
            match(';'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SEMICOLON

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:986:7: ( ':' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:986:9: ':'
            {
            match(':'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start AT
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:987:4: ( '@' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:987:6: '@'
            {
            match('@'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AT

    // $ANTLR start LPAREN
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:988:8: ( '(' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:988:10: '('
            {
            match('('); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LPAREN

    // $ANTLR start RPAREN
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:989:8: ( ')' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:989:10: ')'
            {
            match(')'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RPAREN

    // $ANTLR start LCURLY
    public final void mLCURLY() throws RecognitionException {
        try {
            int _type = LCURLY;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:990:8: ( '{' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:990:10: '{'
            {
            match('{'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LCURLY

    // $ANTLR start RCURLY
    public final void mRCURLY() throws RecognitionException {
        try {
            int _type = RCURLY;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:991:8: ( '}' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:991:10: '}'
            {
            match('}'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RCURLY

    // $ANTLR start LSQUARE
    public final void mLSQUARE() throws RecognitionException {
        try {
            int _type = LSQUARE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:992:9: ( '[' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:992:11: '['
            {
            match('['); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LSQUARE

    // $ANTLR start RSQUARE
    public final void mRSQUARE() throws RecognitionException {
        try {
            int _type = RSQUARE;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:993:9: ( ']' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:993:11: ']'
            {
            match(']'); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RSQUARE

    // $ANTLR start ASSIGN
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:995:8: ( '=' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:995:10: '='
            {
            match('='); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASSIGN

    // $ANTLR start STRING_CHAR
    public final void mSTRING_CHAR() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:998:22: (~ ( '\\'' | '\\r' | '\\n' ) | '\\'\\'' )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( ((LA12_0>='\u0000' && LA12_0<='\t')||(LA12_0>='\u000B' && LA12_0<='\f')||(LA12_0>='\u000E' && LA12_0<='&')||(LA12_0>='(' && LA12_0<='\uFFFE')) ) {
                alt12=1;
            }
            else if ( (LA12_0=='\'') ) {
                alt12=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("998:10: fragment STRING_CHAR : (~ ( '\\'' | '\\r' | '\\n' ) | '\\'\\'' );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:998:24: ~ ( '\\'' | '\\r' | '\\n' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFE') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:998:48: '\\'\\''
                    {
                    match("\'\'"); if (failed) return ;


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end STRING_CHAR

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:8: ({...}? => '\\'' ( STRING_CHAR )* ( '\\'' | ( LINE_TERMINATOR )=>) )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:10: {...}? => '\\'' ( STRING_CHAR )* ( '\\'' | ( LINE_TERMINATOR )=>)
            {
            if ( !(!isPreTransposeChar(input.LA(-1))) ) {
                if (backtracking>0) {failed=true; return ;}
                throw new FailedPredicateException(input, "STRING", "!isPreTransposeChar(input.LA(-1))");
            }
            match('\''); if (failed) return ;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:53: ( STRING_CHAR )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0=='\'') ) {
                    int LA13_1 = input.LA(2);

                    if ( (LA13_1=='\'') ) {
                        alt13=1;
                    }


                }
                else if ( ((LA13_0>='\u0000' && LA13_0<='\t')||(LA13_0>='\u000B' && LA13_0<='\f')||(LA13_0>='\u000E' && LA13_0<='&')||(LA13_0>='(' && LA13_0<='\uFFFE')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:53: STRING_CHAR
            	    {
            	    mSTRING_CHAR(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:66: ( '\\'' | ( LINE_TERMINATOR )=>)
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='\'') ) {
                alt14=1;
            }
            else {
                alt14=2;}
            switch (alt14) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:67: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:74: ( LINE_TERMINATOR )=>
                    {
                    if ( backtracking==0 ) {
                       _type = MISC; 
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start ANNOTATION_FILLER
    public final void mANNOTATION_FILLER() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1001:28: (~ '*' | '*' ~ ')' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>='\u0000' && LA15_0<=')')||(LA15_0>='+' && LA15_0<='\uFFFE')) ) {
                alt15=1;
            }
            else if ( (LA15_0=='*') ) {
                alt15=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1001:10: fragment ANNOTATION_FILLER : (~ '*' | '*' ~ ')' );", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1001:30: ~ '*'
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<=')')||(input.LA(1)>='+' && input.LA(1)<='\uFFFE') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1001:37: '*' ~ ')'
                    {
                    match('*'); if (failed) return ;
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='(')||(input.LA(1)>='*' && input.LA(1)<='\uFFFE') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end ANNOTATION_FILLER

    // $ANTLR start ANNOTATION
    public final void mANNOTATION() throws RecognitionException {
        try {
            int _type = ANNOTATION;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1002:12: ( '(*' ( ANNOTATION_FILLER )* '*)' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1002:14: '(*' ( ANNOTATION_FILLER )* '*)'
            {
            match("(*"); if (failed) return ;

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1002:19: ( ANNOTATION_FILLER )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='*') ) {
                    int LA16_1 = input.LA(2);

                    if ( ((LA16_1>='\u0000' && LA16_1<='(')||(LA16_1>='*' && LA16_1<='\uFFFE')) ) {
                        alt16=1;
                    }


                }
                else if ( ((LA16_0>='\u0000' && LA16_0<=')')||(LA16_0>='+' && LA16_0<='\uFFFE')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1002:19: ANNOTATION_FILLER
            	    {
            	    mANNOTATION_FILLER(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            match("*)"); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ANNOTATION

    // $ANTLR start BRACKET_COMMENT_FILLER
    public final void mBRACKET_COMMENT_FILLER() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1004:33: (~ '%' | '%' ~ ( '{' | '}' ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>='\u0000' && LA17_0<='$')||(LA17_0>='&' && LA17_0<='\uFFFE')) ) {
                alt17=1;
            }
            else if ( (LA17_0=='%') ) {
                alt17=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1004:10: fragment BRACKET_COMMENT_FILLER : (~ '%' | '%' ~ ( '{' | '}' ) );", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1004:35: ~ '%'
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='$')||(input.LA(1)>='&' && input.LA(1)<='\uFFFE') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1004:42: '%' ~ ( '{' | '}' )
                    {
                    match('%'); if (failed) return ;
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='z')||input.LA(1)=='|'||(input.LA(1)>='~' && input.LA(1)<='\uFFFE') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end BRACKET_COMMENT_FILLER

    // $ANTLR start BRACKET_COMMENT
    public final void mBRACKET_COMMENT() throws RecognitionException {
        try {
            int _type = BRACKET_COMMENT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:17: ( '%{' ( BRACKET_COMMENT_FILLER )* ( BRACKET_COMMENT ( BRACKET_COMMENT_FILLER )* )* '%}' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:19: '%{' ( BRACKET_COMMENT_FILLER )* ( BRACKET_COMMENT ( BRACKET_COMMENT_FILLER )* )* '%}'
            {
            match("%{"); if (failed) return ;

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:24: ( BRACKET_COMMENT_FILLER )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='%') ) {
                    int LA18_1 = input.LA(2);

                    if ( ((LA18_1>='\u0000' && LA18_1<='z')||LA18_1=='|'||(LA18_1>='~' && LA18_1<='\uFFFE')) ) {
                        alt18=1;
                    }


                }
                else if ( ((LA18_0>='\u0000' && LA18_0<='$')||(LA18_0>='&' && LA18_0<='\uFFFE')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:24: BRACKET_COMMENT_FILLER
            	    {
            	    mBRACKET_COMMENT_FILLER(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:48: ( BRACKET_COMMENT ( BRACKET_COMMENT_FILLER )* )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0=='%') ) {
                    int LA20_1 = input.LA(2);

                    if ( (LA20_1=='{') ) {
                        alt20=1;
                    }


                }


                switch (alt20) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:49: BRACKET_COMMENT ( BRACKET_COMMENT_FILLER )*
            	    {
            	    mBRACKET_COMMENT(); if (failed) return ;
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:65: ( BRACKET_COMMENT_FILLER )*
            	    loop19:
            	    do {
            	        int alt19=2;
            	        int LA19_0 = input.LA(1);

            	        if ( (LA19_0=='%') ) {
            	            int LA19_1 = input.LA(2);

            	            if ( ((LA19_1>='\u0000' && LA19_1<='z')||LA19_1=='|'||(LA19_1>='~' && LA19_1<='\uFFFE')) ) {
            	                alt19=1;
            	            }


            	        }
            	        else if ( ((LA19_0>='\u0000' && LA19_0<='$')||(LA19_0>='&' && LA19_0<='\uFFFE')) ) {
            	            alt19=1;
            	        }


            	        switch (alt19) {
            	    	case 1 :
            	    	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1005:65: BRACKET_COMMENT_FILLER
            	    	    {
            	    	    mBRACKET_COMMENT_FILLER(); if (failed) return ;

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop19;
            	        }
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            match("%}"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BRACKET_COMMENT

    // $ANTLR start NOT_LINE_TERMINATOR
    public final void mNOT_LINE_TERMINATOR() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1007:30: (~ ( '\\r' | '\\n' ) )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1007:32: ~ ( '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFE') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end NOT_LINE_TERMINATOR

    // $ANTLR start COMMENT
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1008:9: ( '%' | '%' ~ ( '{' | '\\r' | '\\n' ) ( NOT_LINE_TERMINATOR )* )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='%') ) {
                int LA22_1 = input.LA(2);

                if ( ((LA22_1>='\u0000' && LA22_1<='\t')||(LA22_1>='\u000B' && LA22_1<='\f')||(LA22_1>='\u000E' && LA22_1<='z')||(LA22_1>='|' && LA22_1<='\uFFFE')) ) {
                    alt22=2;
                }
                else {
                    alt22=1;}
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1008:1: COMMENT : ( '%' | '%' ~ ( '{' | '\\r' | '\\n' ) ( NOT_LINE_TERMINATOR )* );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1008:11: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1008:17: '%' ~ ( '{' | '\\r' | '\\n' ) ( NOT_LINE_TERMINATOR )*
                    {
                    match('%'); if (failed) return ;
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='z')||(input.LA(1)>='|' && input.LA(1)<='\uFFFE') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1008:42: ( NOT_LINE_TERMINATOR )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='\u0000' && LA21_0<='\t')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='\uFFFE')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1008:42: NOT_LINE_TERMINATOR
                    	    {
                    	    mNOT_LINE_TERMINATOR(); if (failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMENT

    // $ANTLR start SHELL_COMMAND
    public final void mSHELL_COMMAND() throws RecognitionException {
        try {
            int _type = SHELL_COMMAND;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1010:15: ( '!' ( NOT_LINE_TERMINATOR )* )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1010:17: '!' ( NOT_LINE_TERMINATOR )*
            {
            match('!'); if (failed) return ;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1010:21: ( NOT_LINE_TERMINATOR )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( ((LA23_0>='\u0000' && LA23_0<='\t')||(LA23_0>='\u000B' && LA23_0<='\f')||(LA23_0>='\u000E' && LA23_0<='\uFFFE')) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1010:21: NOT_LINE_TERMINATOR
            	    {
            	    mNOT_LINE_TERMINATOR(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SHELL_COMMAND

    // $ANTLR start LINE_TERMINATOR
    public final void mLINE_TERMINATOR() throws RecognitionException {
        try {
            int _type = LINE_TERMINATOR;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1012:17: ( '\\r' '\\n' | '\\r' | '\\n' )
            int alt24=3;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='\r') ) {
                int LA24_1 = input.LA(2);

                if ( (LA24_1=='\n') ) {
                    alt24=1;
                }
                else {
                    alt24=2;}
            }
            else if ( (LA24_0=='\n') ) {
                alt24=3;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1012:1: LINE_TERMINATOR : ( '\\r' '\\n' | '\\r' | '\\n' );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1012:19: '\\r' '\\n'
                    {
                    match('\r'); if (failed) return ;
                    match('\n'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1012:31: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1012:38: '\\n'
                    {
                    match('\n'); if (failed) return ;
                    if ( backtracking==0 ) {
                       couldBeFieldName = false; 
                    }

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_TERMINATOR

    // $ANTLR start ELLIPSIS_COMMENT
    public final void mELLIPSIS_COMMENT() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1014:27: ( '...' ( NOT_LINE_TERMINATOR )* LINE_TERMINATOR )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1014:29: '...' ( NOT_LINE_TERMINATOR )* LINE_TERMINATOR
            {
            match("..."); if (failed) return ;

            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1014:35: ( NOT_LINE_TERMINATOR )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>='\u0000' && LA25_0<='\t')||(LA25_0>='\u000B' && LA25_0<='\f')||(LA25_0>='\u000E' && LA25_0<='\uFFFE')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1014:35: NOT_LINE_TERMINATOR
            	    {
            	    mNOT_LINE_TERMINATOR(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);

            mLINE_TERMINATOR(); if (failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end ELLIPSIS_COMMENT

    // $ANTLR start OTHER_WHITESPACE
    public final void mOTHER_WHITESPACE() throws RecognitionException {
        try {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1015:27: ( ( ' ' | '\\t' | '\\f' )+ )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1015:29: ( ' ' | '\\t' | '\\f' )+
            {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1015:29: ( ' ' | '\\t' | '\\f' )+
            int cnt26=0;
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='\t'||LA26_0=='\f'||LA26_0==' ') ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
            	        input.consume();
            	    failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt26 >= 1 ) break loop26;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(26, input);
                        throw eee;
                }
                cnt26++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end OTHER_WHITESPACE

    // $ANTLR start FILLER
    public final void mFILLER() throws RecognitionException {
        try {
            int _type = FILLER;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:8: ( ( ( ( '...' )=> ELLIPSIS_COMMENT ) | OTHER_WHITESPACE )+ )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:10: ( ( ( '...' )=> ELLIPSIS_COMMENT ) | OTHER_WHITESPACE )+
            {
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:10: ( ( ( '...' )=> ELLIPSIS_COMMENT ) | OTHER_WHITESPACE )+
            int cnt27=0;
            loop27:
            do {
                int alt27=3;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='.') && (synpred2())) {
                    alt27=1;
                }
                else if ( (LA27_0=='\t'||LA27_0=='\f'||LA27_0==' ') ) {
                    alt27=2;
                }


                switch (alt27) {
            	case 1 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:11: ( ( '...' )=> ELLIPSIS_COMMENT )
            	    {
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:11: ( ( '...' )=> ELLIPSIS_COMMENT )
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:12: ( '...' )=> ELLIPSIS_COMMENT
            	    {
            	    mELLIPSIS_COMMENT(); if (failed) return ;

            	    }


            	    }
            	    break;
            	case 2 :
            	    // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:42: OTHER_WHITESPACE
            	    {
            	    mOTHER_WHITESPACE(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FILLER

    // $ANTLR start DOUBLE_DOT
    public final void mDOUBLE_DOT() throws RecognitionException {
        try {
            int _type = DOUBLE_DOT;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1023:12: ( '..' )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1023:14: '..'
            {
            match(".."); if (failed) return ;

            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_DOT

    // $ANTLR start MISC
    public final void mMISC() throws RecognitionException {
        try {
            int _type = MISC;
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1025:6: ( . )
            // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1025:8: .
            {
            matchAny(); if (failed) return ;
            if ( backtracking==0 ) {
               couldBeFieldName = false; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MISC

    public void mTokens() throws RecognitionException {
        // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:8: ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC )
        int alt28=65;
        int LA28_0 = input.LA(1);

        if ( (LA28_0=='b') ) {
            int LA28_1 = input.LA(2);

            if ( (LA28_1=='r') ) {
                int LA28_45 = input.LA(3);

                if ( (LA28_45=='e') ) {
                    int LA28_107 = input.LA(4);

                    if ( (LA28_107=='a') ) {
                        int LA28_127 = input.LA(5);

                        if ( (LA28_127=='k') ) {
                            int LA28_145 = input.LA(6);

                            if ( (LA28_145=='$'||(LA28_145>='0' && LA28_145<='9')||(LA28_145>='A' && LA28_145<='Z')||LA28_145=='_'||(LA28_145>='a' && LA28_145<='z')) ) {
                                alt28=20;
                            }
                            else if ( ( !couldBeFieldName ) ) {
                                alt28=1;
                            }
                            else if ( (true) ) {
                                alt28=20;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 145, input);

                                throw nvae;
                            }
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='c') ) {
            switch ( input.LA(2) ) {
            case 'a':
                {
                switch ( input.LA(3) ) {
                case 's':
                    {
                    int LA28_108 = input.LA(4);

                    if ( (LA28_108=='e') ) {
                        int LA28_128 = input.LA(5);

                        if ( (LA28_128=='$'||(LA28_128>='0' && LA28_128<='9')||(LA28_128>='A' && LA28_128<='Z')||LA28_128=='_'||(LA28_128>='a' && LA28_128<='z')) ) {
                            alt28=20;
                        }
                        else if ( ( !couldBeFieldName ) ) {
                            alt28=2;
                        }
                        else if ( (true) ) {
                            alt28=20;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 128, input);

                            throw nvae;
                        }
                    }
                    else {
                        alt28=20;}
                    }
                    break;
                case 't':
                    {
                    int LA28_109 = input.LA(4);

                    if ( (LA28_109=='c') ) {
                        int LA28_129 = input.LA(5);

                        if ( (LA28_129=='h') ) {
                            int LA28_147 = input.LA(6);

                            if ( (LA28_147=='$'||(LA28_147>='0' && LA28_147<='9')||(LA28_147>='A' && LA28_147<='Z')||LA28_147=='_'||(LA28_147>='a' && LA28_147<='z')) ) {
                                alt28=20;
                            }
                            else if ( ( !couldBeFieldName ) ) {
                                alt28=3;
                            }
                            else if ( (true) ) {
                                alt28=20;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 147, input);

                                throw nvae;
                            }
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                    }
                    break;
                default:
                    alt28=20;}

                }
                break;
            case 'l':
                {
                int LA28_48 = input.LA(3);

                if ( (LA28_48=='a') ) {
                    int LA28_110 = input.LA(4);

                    if ( (LA28_110=='s') ) {
                        int LA28_130 = input.LA(5);

                        if ( (LA28_130=='s') ) {
                            int LA28_148 = input.LA(6);

                            if ( (LA28_148=='d') ) {
                                int LA28_166 = input.LA(7);

                                if ( (LA28_166=='e') ) {
                                    int LA28_180 = input.LA(8);

                                    if ( (LA28_180=='f') ) {
                                        int LA28_191 = input.LA(9);

                                        if ( (LA28_191=='$'||(LA28_191>='0' && LA28_191<='9')||(LA28_191>='A' && LA28_191<='Z')||LA28_191=='_'||(LA28_191>='a' && LA28_191<='z')) ) {
                                            alt28=20;
                                        }
                                        else if ( ( !couldBeFieldName ) ) {
                                            alt28=4;
                                        }
                                        else if ( (true) ) {
                                            alt28=20;
                                        }
                                        else {
                                            if (backtracking>0) {failed=true; return ;}
                                            NoViableAltException nvae =
                                                new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 191, input);

                                            throw nvae;
                                        }
                                    }
                                    else {
                                        alt28=20;}
                                }
                                else {
                                    alt28=20;}
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
                }
                break;
            case 'o':
                {
                int LA28_49 = input.LA(3);

                if ( (LA28_49=='n') ) {
                    int LA28_111 = input.LA(4);

                    if ( (LA28_111=='t') ) {
                        int LA28_131 = input.LA(5);

                        if ( (LA28_131=='i') ) {
                            int LA28_149 = input.LA(6);

                            if ( (LA28_149=='n') ) {
                                int LA28_167 = input.LA(7);

                                if ( (LA28_167=='u') ) {
                                    int LA28_181 = input.LA(8);

                                    if ( (LA28_181=='e') ) {
                                        int LA28_192 = input.LA(9);

                                        if ( (LA28_192=='$'||(LA28_192>='0' && LA28_192<='9')||(LA28_192>='A' && LA28_192<='Z')||LA28_192=='_'||(LA28_192>='a' && LA28_192<='z')) ) {
                                            alt28=20;
                                        }
                                        else if ( ( !couldBeFieldName ) ) {
                                            alt28=5;
                                        }
                                        else if ( (true) ) {
                                            alt28=20;
                                        }
                                        else {
                                            if (backtracking>0) {failed=true; return ;}
                                            NoViableAltException nvae =
                                                new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 192, input);

                                            throw nvae;
                                        }
                                    }
                                    else {
                                        alt28=20;}
                                }
                                else {
                                    alt28=20;}
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
                }
                break;
            default:
                alt28=20;}

        }
        else if ( (LA28_0=='e') ) {
            switch ( input.LA(2) ) {
            case 'l':
                {
                int LA28_50 = input.LA(3);

                if ( (LA28_50=='s') ) {
                    int LA28_112 = input.LA(4);

                    if ( (LA28_112=='e') ) {
                        int LA28_132 = input.LA(5);

                        if ( (LA28_132=='i') ) {
                            int LA28_150 = input.LA(6);

                            if ( (LA28_150=='f') ) {
                                int LA28_168 = input.LA(7);

                                if ( (LA28_168=='$'||(LA28_168>='0' && LA28_168<='9')||(LA28_168>='A' && LA28_168<='Z')||LA28_168=='_'||(LA28_168>='a' && LA28_168<='z')) ) {
                                    alt28=20;
                                }
                                else if ( ( !couldBeFieldName ) ) {
                                    alt28=7;
                                }
                                else if ( (true) ) {
                                    alt28=20;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 168, input);

                                    throw nvae;
                                }
                            }
                            else {
                                alt28=20;}
                        }
                        else if ( (LA28_132=='$'||(LA28_132>='0' && LA28_132<='9')||(LA28_132>='A' && LA28_132<='Z')||LA28_132=='_'||(LA28_132>='a' && LA28_132<='h')||(LA28_132>='j' && LA28_132<='z')) ) {
                            alt28=20;
                        }
                        else if ( ( !couldBeFieldName ) ) {
                            alt28=6;
                        }
                        else if ( (true) ) {
                            alt28=20;
                        }
                        else {
                            if (backtracking>0) {failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 132, input);

                            throw nvae;
                        }
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
                }
                break;
            case 'n':
                {
                int LA28_51 = input.LA(3);

                if ( (LA28_51=='d') ) {
                    int LA28_113 = input.LA(4);

                    if ( (LA28_113=='$'||(LA28_113>='0' && LA28_113<='9')||(LA28_113>='A' && LA28_113<='Z')||LA28_113=='_'||(LA28_113>='a' && LA28_113<='z')) ) {
                        alt28=20;
                    }
                    else if ( ( !couldBeFieldName ) ) {
                        alt28=8;
                    }
                    else if ( (true) ) {
                        alt28=20;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 113, input);

                        throw nvae;
                    }
                }
                else {
                    alt28=20;}
                }
                break;
            default:
                alt28=20;}

        }
        else if ( (LA28_0=='f') ) {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA28_52 = input.LA(3);

                if ( (LA28_52=='r') ) {
                    int LA28_114 = input.LA(4);

                    if ( (LA28_114=='$'||(LA28_114>='0' && LA28_114<='9')||(LA28_114>='A' && LA28_114<='Z')||LA28_114=='_'||(LA28_114>='a' && LA28_114<='z')) ) {
                        alt28=20;
                    }
                    else if ( ( !couldBeFieldName ) ) {
                        alt28=9;
                    }
                    else if ( (true) ) {
                        alt28=20;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 114, input);

                        throw nvae;
                    }
                }
                else {
                    alt28=20;}
                }
                break;
            case 'u':
                {
                int LA28_53 = input.LA(3);

                if ( (LA28_53=='n') ) {
                    int LA28_115 = input.LA(4);

                    if ( (LA28_115=='c') ) {
                        int LA28_135 = input.LA(5);

                        if ( (LA28_135=='t') ) {
                            int LA28_154 = input.LA(6);

                            if ( (LA28_154=='i') ) {
                                int LA28_170 = input.LA(7);

                                if ( (LA28_170=='o') ) {
                                    int LA28_183 = input.LA(8);

                                    if ( (LA28_183=='n') ) {
                                        int LA28_194 = input.LA(9);

                                        if ( (LA28_194=='$'||(LA28_194>='0' && LA28_194<='9')||(LA28_194>='A' && LA28_194<='Z')||LA28_194=='_'||(LA28_194>='a' && LA28_194<='z')) ) {
                                            alt28=20;
                                        }
                                        else if ( ( !couldBeFieldName ) ) {
                                            alt28=10;
                                        }
                                        else if ( (true) ) {
                                            alt28=20;
                                        }
                                        else {
                                            if (backtracking>0) {failed=true; return ;}
                                            NoViableAltException nvae =
                                                new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 194, input);

                                            throw nvae;
                                        }
                                    }
                                    else {
                                        alt28=20;}
                                }
                                else {
                                    alt28=20;}
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
                }
                break;
            default:
                alt28=20;}

        }
        else if ( (LA28_0=='g') ) {
            int LA28_5 = input.LA(2);

            if ( (LA28_5=='l') ) {
                int LA28_54 = input.LA(3);

                if ( (LA28_54=='o') ) {
                    int LA28_116 = input.LA(4);

                    if ( (LA28_116=='b') ) {
                        int LA28_136 = input.LA(5);

                        if ( (LA28_136=='a') ) {
                            int LA28_155 = input.LA(6);

                            if ( (LA28_155=='l') ) {
                                int LA28_171 = input.LA(7);

                                if ( (LA28_171=='$'||(LA28_171>='0' && LA28_171<='9')||(LA28_171>='A' && LA28_171<='Z')||LA28_171=='_'||(LA28_171>='a' && LA28_171<='z')) ) {
                                    alt28=20;
                                }
                                else if ( ( !couldBeFieldName ) ) {
                                    alt28=11;
                                }
                                else if ( (true) ) {
                                    alt28=20;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 171, input);

                                    throw nvae;
                                }
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='i') ) {
            int LA28_6 = input.LA(2);

            if ( (LA28_6=='f') ) {
                int LA28_55 = input.LA(3);

                if ( (LA28_55=='$'||(LA28_55>='0' && LA28_55<='9')||(LA28_55>='A' && LA28_55<='Z')||LA28_55=='_'||(LA28_55>='a' && LA28_55<='z')) ) {
                    alt28=20;
                }
                else if ( ( !couldBeFieldName ) ) {
                    alt28=12;
                }
                else if ( (true) ) {
                    alt28=20;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 55, input);

                    throw nvae;
                }
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='o') ) {
            int LA28_7 = input.LA(2);

            if ( (LA28_7=='t') ) {
                int LA28_56 = input.LA(3);

                if ( (LA28_56=='h') ) {
                    int LA28_118 = input.LA(4);

                    if ( (LA28_118=='e') ) {
                        int LA28_138 = input.LA(5);

                        if ( (LA28_138=='r') ) {
                            int LA28_156 = input.LA(6);

                            if ( (LA28_156=='w') ) {
                                int LA28_172 = input.LA(7);

                                if ( (LA28_172=='i') ) {
                                    int LA28_185 = input.LA(8);

                                    if ( (LA28_185=='s') ) {
                                        int LA28_196 = input.LA(9);

                                        if ( (LA28_196=='e') ) {
                                            int LA28_204 = input.LA(10);

                                            if ( (LA28_204=='$'||(LA28_204>='0' && LA28_204<='9')||(LA28_204>='A' && LA28_204<='Z')||LA28_204=='_'||(LA28_204>='a' && LA28_204<='z')) ) {
                                                alt28=20;
                                            }
                                            else if ( ( !couldBeFieldName ) ) {
                                                alt28=13;
                                            }
                                            else if ( (true) ) {
                                                alt28=20;
                                            }
                                            else {
                                                if (backtracking>0) {failed=true; return ;}
                                                NoViableAltException nvae =
                                                    new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 204, input);

                                                throw nvae;
                                            }
                                        }
                                        else {
                                            alt28=20;}
                                    }
                                    else {
                                        alt28=20;}
                                }
                                else {
                                    alt28=20;}
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='p') ) {
            switch ( input.LA(2) ) {
            case 'a':
                {
                int LA28_57 = input.LA(3);

                if ( (LA28_57=='r') ) {
                    int LA28_119 = input.LA(4);

                    if ( (LA28_119=='f') ) {
                        int LA28_139 = input.LA(5);

                        if ( (LA28_139=='o') ) {
                            int LA28_157 = input.LA(6);

                            if ( (LA28_157=='r') ) {
                                int LA28_173 = input.LA(7);

                                if ( (LA28_173=='$'||(LA28_173>='0' && LA28_173<='9')||(LA28_173>='A' && LA28_173<='Z')||LA28_173=='_'||(LA28_173>='a' && LA28_173<='z')) ) {
                                    alt28=20;
                                }
                                else if ( ( !couldBeFieldName ) ) {
                                    alt28=14;
                                }
                                else if ( (true) ) {
                                    alt28=20;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 173, input);

                                    throw nvae;
                                }
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
                }
                break;
            case 'e':
                {
                int LA28_58 = input.LA(3);

                if ( (LA28_58=='r') ) {
                    int LA28_120 = input.LA(4);

                    if ( (LA28_120=='s') ) {
                        int LA28_140 = input.LA(5);

                        if ( (LA28_140=='i') ) {
                            int LA28_158 = input.LA(6);

                            if ( (LA28_158=='s') ) {
                                int LA28_174 = input.LA(7);

                                if ( (LA28_174=='t') ) {
                                    int LA28_187 = input.LA(8);

                                    if ( (LA28_187=='e') ) {
                                        int LA28_198 = input.LA(9);

                                        if ( (LA28_198=='n') ) {
                                            int LA28_205 = input.LA(10);

                                            if ( (LA28_205=='t') ) {
                                                int LA28_210 = input.LA(11);

                                                if ( (LA28_210=='$'||(LA28_210>='0' && LA28_210<='9')||(LA28_210>='A' && LA28_210<='Z')||LA28_210=='_'||(LA28_210>='a' && LA28_210<='z')) ) {
                                                    alt28=20;
                                                }
                                                else if ( ( !couldBeFieldName ) ) {
                                                    alt28=15;
                                                }
                                                else if ( (true) ) {
                                                    alt28=20;
                                                }
                                                else {
                                                    if (backtracking>0) {failed=true; return ;}
                                                    NoViableAltException nvae =
                                                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 210, input);

                                                    throw nvae;
                                                }
                                            }
                                            else {
                                                alt28=20;}
                                        }
                                        else {
                                            alt28=20;}
                                    }
                                    else {
                                        alt28=20;}
                                }
                                else {
                                    alt28=20;}
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
                }
                break;
            default:
                alt28=20;}

        }
        else if ( (LA28_0=='r') ) {
            int LA28_9 = input.LA(2);

            if ( (LA28_9=='e') ) {
                int LA28_59 = input.LA(3);

                if ( (LA28_59=='t') ) {
                    int LA28_121 = input.LA(4);

                    if ( (LA28_121=='u') ) {
                        int LA28_141 = input.LA(5);

                        if ( (LA28_141=='r') ) {
                            int LA28_159 = input.LA(6);

                            if ( (LA28_159=='n') ) {
                                int LA28_175 = input.LA(7);

                                if ( (LA28_175=='$'||(LA28_175>='0' && LA28_175<='9')||(LA28_175>='A' && LA28_175<='Z')||LA28_175=='_'||(LA28_175>='a' && LA28_175<='z')) ) {
                                    alt28=20;
                                }
                                else if ( ( !couldBeFieldName ) ) {
                                    alt28=16;
                                }
                                else if ( (true) ) {
                                    alt28=20;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 175, input);

                                    throw nvae;
                                }
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='s') ) {
            int LA28_10 = input.LA(2);

            if ( (LA28_10=='w') ) {
                int LA28_60 = input.LA(3);

                if ( (LA28_60=='i') ) {
                    int LA28_122 = input.LA(4);

                    if ( (LA28_122=='t') ) {
                        int LA28_142 = input.LA(5);

                        if ( (LA28_142=='c') ) {
                            int LA28_160 = input.LA(6);

                            if ( (LA28_160=='h') ) {
                                int LA28_176 = input.LA(7);

                                if ( (LA28_176=='$'||(LA28_176>='0' && LA28_176<='9')||(LA28_176>='A' && LA28_176<='Z')||LA28_176=='_'||(LA28_176>='a' && LA28_176<='z')) ) {
                                    alt28=20;
                                }
                                else if ( ( !couldBeFieldName ) ) {
                                    alt28=17;
                                }
                                else if ( (true) ) {
                                    alt28=20;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 176, input);

                                    throw nvae;
                                }
                            }
                            else {
                                alt28=20;}
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='t') ) {
            int LA28_11 = input.LA(2);

            if ( (LA28_11=='r') ) {
                int LA28_61 = input.LA(3);

                if ( (LA28_61=='y') ) {
                    int LA28_123 = input.LA(4);

                    if ( (LA28_123=='$'||(LA28_123>='0' && LA28_123<='9')||(LA28_123>='A' && LA28_123<='Z')||LA28_123=='_'||(LA28_123>='a' && LA28_123<='z')) ) {
                        alt28=20;
                    }
                    else if ( ( !couldBeFieldName ) ) {
                        alt28=18;
                    }
                    else if ( (true) ) {
                        alt28=20;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 123, input);

                        throw nvae;
                    }
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='w') ) {
            int LA28_12 = input.LA(2);

            if ( (LA28_12=='h') ) {
                int LA28_62 = input.LA(3);

                if ( (LA28_62=='i') ) {
                    int LA28_124 = input.LA(4);

                    if ( (LA28_124=='l') ) {
                        int LA28_144 = input.LA(5);

                        if ( (LA28_144=='e') ) {
                            int LA28_162 = input.LA(6);

                            if ( (LA28_162=='$'||(LA28_162>='0' && LA28_162<='9')||(LA28_162>='A' && LA28_162<='Z')||LA28_162=='_'||(LA28_162>='a' && LA28_162<='z')) ) {
                                alt28=20;
                            }
                            else if ( ( !couldBeFieldName ) ) {
                                alt28=19;
                            }
                            else if ( (true) ) {
                                alt28=20;
                            }
                            else {
                                if (backtracking>0) {failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 162, input);

                                throw nvae;
                            }
                        }
                        else {
                            alt28=20;}
                    }
                    else {
                        alt28=20;}
                }
                else {
                    alt28=20;}
            }
            else {
                alt28=20;}
        }
        else if ( (LA28_0=='$'||(LA28_0>='A' && LA28_0<='Z')||LA28_0=='_'||LA28_0=='a'||LA28_0=='d'||LA28_0=='h'||(LA28_0>='j' && LA28_0<='n')||LA28_0=='q'||(LA28_0>='u' && LA28_0<='v')||(LA28_0>='x' && LA28_0<='z')) ) {
            alt28=20;
        }
        else if ( ((LA28_0>='0' && LA28_0<='9')) ) {
            alt28=21;
        }
        else if ( (LA28_0=='.') ) {
            switch ( input.LA(2) ) {
            case '.':
                {
                int LA28_64 = input.LA(3);

                if ( (LA28_64=='.') ) {
                    alt28=63;
                }
                else {
                    alt28=64;}
                }
                break;
            case '*':
                {
                alt28=25;
                }
                break;
            case '/':
                {
                alt28=27;
                }
                break;
            case '\\':
                {
                alt28=29;
                }
                break;
            case '^':
                {
                alt28=31;
                }
                break;
            case '\'':
                {
                alt28=33;
                }
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                {
                alt28=21;
                }
                break;
            default:
                alt28=45;}

        }
        else if ( (LA28_0=='+') ) {
            alt28=22;
        }
        else if ( (LA28_0=='-') ) {
            alt28=23;
        }
        else if ( (LA28_0=='*') ) {
            alt28=24;
        }
        else if ( (LA28_0=='/') ) {
            alt28=26;
        }
        else if ( (LA28_0=='\\') ) {
            alt28=28;
        }
        else if ( (LA28_0=='^') ) {
            alt28=30;
        }
        else if ( (LA28_0=='\'') ) {
            int LA28_22 = input.LA(2);

            if ( ((LA28_22>='\u0000' && LA28_22<='\t')||(LA28_22>='\u000B' && LA28_22<='\f')||(LA28_22>='\u000E' && LA28_22<='\uFFFE')) && (!isPreTransposeChar(input.LA(-1)))) {
                alt28=57;
            }
            else if ( (isPreTransposeChar(input.LA(-1))) ) {
                alt28=32;
            }
            else if ( (!isPreTransposeChar(input.LA(-1))) ) {
                alt28=57;
            }
            else if ( (true) ) {
                alt28=65;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 22, input);

                throw nvae;
            }
        }
        else if ( (LA28_0=='<') ) {
            int LA28_23 = input.LA(2);

            if ( (LA28_23=='=') ) {
                alt28=34;
            }
            else {
                alt28=36;}
        }
        else if ( (LA28_0=='>') ) {
            int LA28_24 = input.LA(2);

            if ( (LA28_24=='=') ) {
                alt28=35;
            }
            else {
                alt28=37;}
        }
        else if ( (LA28_0=='=') ) {
            int LA28_25 = input.LA(2);

            if ( (LA28_25=='=') ) {
                alt28=38;
            }
            else {
                alt28=56;}
        }
        else if ( (LA28_0=='~') ) {
            int LA28_26 = input.LA(2);

            if ( (LA28_26=='=') ) {
                alt28=39;
            }
            else {
                alt28=42;}
        }
        else if ( (LA28_0=='&') ) {
            int LA28_27 = input.LA(2);

            if ( (LA28_27=='&') ) {
                alt28=43;
            }
            else {
                alt28=40;}
        }
        else if ( (LA28_0=='|') ) {
            int LA28_28 = input.LA(2);

            if ( (LA28_28=='|') ) {
                alt28=44;
            }
            else {
                alt28=41;}
        }
        else if ( (LA28_0==',') ) {
            alt28=46;
        }
        else if ( (LA28_0==';') ) {
            alt28=47;
        }
        else if ( (LA28_0==':') ) {
            alt28=48;
        }
        else if ( (LA28_0=='@') ) {
            alt28=49;
        }
        else if ( (LA28_0=='(') ) {
            int LA28_33 = input.LA(2);

            if ( (LA28_33=='*') ) {
                alt28=58;
            }
            else {
                alt28=50;}
        }
        else if ( (LA28_0==')') ) {
            alt28=51;
        }
        else if ( (LA28_0=='{') ) {
            alt28=52;
        }
        else if ( (LA28_0=='}') ) {
            alt28=53;
        }
        else if ( (LA28_0=='[') ) {
            alt28=54;
        }
        else if ( (LA28_0==']') ) {
            alt28=55;
        }
        else if ( (LA28_0=='%') ) {
            int LA28_39 = input.LA(2);

            if ( (LA28_39=='{') ) {
                alt28=59;
            }
            else {
                alt28=60;}
        }
        else if ( (LA28_0=='!') ) {
            alt28=61;
        }
        else if ( (LA28_0=='\r') ) {
            alt28=62;
        }
        else if ( (LA28_0=='\n') ) {
            alt28=62;
        }
        else if ( (LA28_0=='\t'||LA28_0=='\f'||LA28_0==' ') ) {
            alt28=63;
        }
        else if ( ((LA28_0>='\u0000' && LA28_0<='\b')||LA28_0=='\u000B'||(LA28_0>='\u000E' && LA28_0<='\u001F')||(LA28_0>='\"' && LA28_0<='#')||LA28_0=='?'||LA28_0=='`'||(LA28_0>='\u007F' && LA28_0<='\uFFFE')) ) {
            alt28=65;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( BREAK | CASE | CATCH | CLASSDEF | CONTINUE | ELSE | ELSEIF | END | FOR | FUNCTION | GLOBAL | IF | OTHERWISE | PARFOR | PERSISTENT | RETURN | SWITCH | TRY | WHILE | IDENTIFIER | NUMBER | PLUS | MINUS | MTIMES | ETIMES | MDIV | EDIV | MLDIV | ELDIV | MPOW | EPOW | MTRANSPOSE | ARRAYTRANSPOSE | LE | GE | LT | GT | EQ | NE | AND | OR | NOT | SHORTAND | SHORTOR | DOT | COMMA | SEMICOLON | COLON | AT | LPAREN | RPAREN | LCURLY | RCURLY | LSQUARE | RSQUARE | ASSIGN | STRING | ANNOTATION | BRACKET_COMMENT | COMMENT | SHELL_COMMAND | LINE_TERMINATOR | FILLER | DOUBLE_DOT | MISC );", 28, 0, input);

            throw nvae;
        }
        switch (alt28) {
            case 1 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:10: BREAK
                {
                mBREAK(); if (failed) return ;

                }
                break;
            case 2 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:16: CASE
                {
                mCASE(); if (failed) return ;

                }
                break;
            case 3 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:21: CATCH
                {
                mCATCH(); if (failed) return ;

                }
                break;
            case 4 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:27: CLASSDEF
                {
                mCLASSDEF(); if (failed) return ;

                }
                break;
            case 5 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:36: CONTINUE
                {
                mCONTINUE(); if (failed) return ;

                }
                break;
            case 6 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:45: ELSE
                {
                mELSE(); if (failed) return ;

                }
                break;
            case 7 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:50: ELSEIF
                {
                mELSEIF(); if (failed) return ;

                }
                break;
            case 8 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:57: END
                {
                mEND(); if (failed) return ;

                }
                break;
            case 9 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:61: FOR
                {
                mFOR(); if (failed) return ;

                }
                break;
            case 10 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:65: FUNCTION
                {
                mFUNCTION(); if (failed) return ;

                }
                break;
            case 11 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:74: GLOBAL
                {
                mGLOBAL(); if (failed) return ;

                }
                break;
            case 12 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:81: IF
                {
                mIF(); if (failed) return ;

                }
                break;
            case 13 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:84: OTHERWISE
                {
                mOTHERWISE(); if (failed) return ;

                }
                break;
            case 14 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:94: PARFOR
                {
                mPARFOR(); if (failed) return ;

                }
                break;
            case 15 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:101: PERSISTENT
                {
                mPERSISTENT(); if (failed) return ;

                }
                break;
            case 16 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:112: RETURN
                {
                mRETURN(); if (failed) return ;

                }
                break;
            case 17 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:119: SWITCH
                {
                mSWITCH(); if (failed) return ;

                }
                break;
            case 18 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:126: TRY
                {
                mTRY(); if (failed) return ;

                }
                break;
            case 19 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:130: WHILE
                {
                mWHILE(); if (failed) return ;

                }
                break;
            case 20 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:136: IDENTIFIER
                {
                mIDENTIFIER(); if (failed) return ;

                }
                break;
            case 21 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:147: NUMBER
                {
                mNUMBER(); if (failed) return ;

                }
                break;
            case 22 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:154: PLUS
                {
                mPLUS(); if (failed) return ;

                }
                break;
            case 23 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:159: MINUS
                {
                mMINUS(); if (failed) return ;

                }
                break;
            case 24 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:165: MTIMES
                {
                mMTIMES(); if (failed) return ;

                }
                break;
            case 25 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:172: ETIMES
                {
                mETIMES(); if (failed) return ;

                }
                break;
            case 26 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:179: MDIV
                {
                mMDIV(); if (failed) return ;

                }
                break;
            case 27 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:184: EDIV
                {
                mEDIV(); if (failed) return ;

                }
                break;
            case 28 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:189: MLDIV
                {
                mMLDIV(); if (failed) return ;

                }
                break;
            case 29 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:195: ELDIV
                {
                mELDIV(); if (failed) return ;

                }
                break;
            case 30 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:201: MPOW
                {
                mMPOW(); if (failed) return ;

                }
                break;
            case 31 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:206: EPOW
                {
                mEPOW(); if (failed) return ;

                }
                break;
            case 32 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:211: MTRANSPOSE
                {
                mMTRANSPOSE(); if (failed) return ;

                }
                break;
            case 33 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:222: ARRAYTRANSPOSE
                {
                mARRAYTRANSPOSE(); if (failed) return ;

                }
                break;
            case 34 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:237: LE
                {
                mLE(); if (failed) return ;

                }
                break;
            case 35 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:240: GE
                {
                mGE(); if (failed) return ;

                }
                break;
            case 36 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:243: LT
                {
                mLT(); if (failed) return ;

                }
                break;
            case 37 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:246: GT
                {
                mGT(); if (failed) return ;

                }
                break;
            case 38 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:249: EQ
                {
                mEQ(); if (failed) return ;

                }
                break;
            case 39 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:252: NE
                {
                mNE(); if (failed) return ;

                }
                break;
            case 40 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:255: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 41 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:259: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 42 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:262: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 43 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:266: SHORTAND
                {
                mSHORTAND(); if (failed) return ;

                }
                break;
            case 44 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:275: SHORTOR
                {
                mSHORTOR(); if (failed) return ;

                }
                break;
            case 45 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:283: DOT
                {
                mDOT(); if (failed) return ;

                }
                break;
            case 46 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:287: COMMA
                {
                mCOMMA(); if (failed) return ;

                }
                break;
            case 47 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:293: SEMICOLON
                {
                mSEMICOLON(); if (failed) return ;

                }
                break;
            case 48 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:303: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 49 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:309: AT
                {
                mAT(); if (failed) return ;

                }
                break;
            case 50 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:312: LPAREN
                {
                mLPAREN(); if (failed) return ;

                }
                break;
            case 51 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:319: RPAREN
                {
                mRPAREN(); if (failed) return ;

                }
                break;
            case 52 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:326: LCURLY
                {
                mLCURLY(); if (failed) return ;

                }
                break;
            case 53 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:333: RCURLY
                {
                mRCURLY(); if (failed) return ;

                }
                break;
            case 54 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:340: LSQUARE
                {
                mLSQUARE(); if (failed) return ;

                }
                break;
            case 55 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:348: RSQUARE
                {
                mRSQUARE(); if (failed) return ;

                }
                break;
            case 56 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:356: ASSIGN
                {
                mASSIGN(); if (failed) return ;

                }
                break;
            case 57 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:363: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 58 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:370: ANNOTATION
                {
                mANNOTATION(); if (failed) return ;

                }
                break;
            case 59 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:381: BRACKET_COMMENT
                {
                mBRACKET_COMMENT(); if (failed) return ;

                }
                break;
            case 60 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:397: COMMENT
                {
                mCOMMENT(); if (failed) return ;

                }
                break;
            case 61 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:405: SHELL_COMMAND
                {
                mSHELL_COMMAND(); if (failed) return ;

                }
                break;
            case 62 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:419: LINE_TERMINATOR
                {
                mLINE_TERMINATOR(); if (failed) return ;

                }
                break;
            case 63 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:435: FILLER
                {
                mFILLER(); if (failed) return ;

                }
                break;
            case 64 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:442: DOUBLE_DOT
                {
                mDOUBLE_DOT(); if (failed) return ;

                }
                break;
            case 65 :
                // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1:453: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:74: ( LINE_TERMINATOR )
        // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:999:75: LINE_TERMINATOR
        {
        mLINE_TERMINATOR(); if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:12: ( '...' )
        // /home/sameer/interview/test/mclab_core/languages/Matlab/src/Matlab.g:1016:13: '...'
        {
        match("..."); if (failed) return ;


        }
    }
    // $ANTLR end synpred2

    public final boolean synpred2() {
        backtracking++;
        int start = input.mark();
        try {
            synpred2_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA9_eotS =
        "\1\uffff\1\3\2\uffff";
    static final String DFA9_eofS =
        "\4\uffff";
    static final String DFA9_minS =
        "\2\56\2\uffff";
    static final String DFA9_maxS =
        "\2\71\2\uffff";
    static final String DFA9_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA9_specialS =
        "\4\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\2\1\uffff\12\1",
            "",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "958:10: ( INT_NUMBER | FP_NUMBER )";
        }
    }
 

}