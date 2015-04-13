package matlab;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import matlab.CommandToken.Arg;
import matlab.CommandToken.EllipsisComment;
import matlab.CommandToken.InlineWhitespace;

/**
 * Translates a command style invocation into a function style invocation,
 * if appropriate.
 */
public class CommandFormatter {
    //keeping track of position changes
    private final OffsetTracker offsetTracker;
    //input
    private final String originalStr;
    //first line number of originalStr
    private final int baseLine;
    //first col number of originalStr
    private final int baseCol;
    //input after being concatenated and run through CommandScanner
    private final List<CommandToken> rescannedSymbols;
    //output
    private final StringBuffer formattedStrBuf;
    //number of arguments after re-scanning
    private int numArgs;

    private CommandFormatter(String originalStr, int baseLine, int baseCol, OffsetTracker offsetTracker) {
        this.offsetTracker = offsetTracker;
        this.originalStr = originalStr;
        this.baseLine = baseLine;
        this.baseCol = baseCol;
        this.rescannedSymbols = new ArrayList<CommandToken>();
        this.formattedStrBuf = new StringBuffer();
        this.numArgs = 0;
    }

    /**
     * Input: Everything after the IDENTIFIER in a stmt starting with an IDENTIFIER.
     * Output: Parenthesized, quoted, comma-delimited arguments or the input if
     *   no translation is necessary
     */
    public static String format(String originalStr, int baseLine, int baseCol, OffsetTracker offsetTracker, List<TranslationProblem> problems) {
        if(originalStr == null) {
            return null;
        }
        if(offsetTracker == null) {
            //easier than checking for null everywhere
            offsetTracker = new OffsetTracker(new TextPosition(1, 1));
        }
        CommandFormatter cf = new CommandFormatter(originalStr, baseLine, baseCol, offsetTracker);
        try {
            cf.rescan();
            cf.format();
        } catch (CommandScanner.Exception e) {
            problems.add(e.getProblem());
        }
        return cf.formattedStrBuf.toString();
    }

    /*
     * Concatenates the input symbosl together and runs them through CommandScanner.
     */
    private void rescan() throws CommandScanner.Exception {
        CommandScanner scanner = new CommandScanner(new StringReader(originalStr));
        scanner.setBasePosition(baseLine, baseCol);

        while(true) {
            CommandToken curr = null;
            try {
                curr = scanner.nextToken();
            } catch (IOException e) {
                //can't happen - using a StringReader
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            if(curr == null) { //EOF
                break;
            }
            if(curr instanceof Arg) {
                numArgs++;
            }
            rescannedSymbols.add(curr);
        }
    }

    /*
     * Adds appropriate punctuation around the rescanned arguments and makes
     * the necessary changes to the OffsetTracker.
     */
    private void format() {
        formattedStrBuf.append("(");
        recordOffsetChangeIfWhitespace(0);
        offsetTracker.advanceInLine(1);

        int argsSeen = 0; //used to figure out when we're at the last arg
        for(int i = 0; i < rescannedSymbols.size(); i++) {
            CommandToken tok = rescannedSymbols.get(i);
            if(tok instanceof Arg) {
                formatArg(argsSeen, i, (Arg) tok);
                if(argsSeen < numArgs - 1) {
                    formattedStrBuf.append(",");
                    offsetTracker.recordOffsetChange(0, -1);
                    offsetTracker.advanceInLine(1);
                }
                argsSeen++;
            } else if(tok instanceof EllipsisComment) {
                formattedStrBuf.append(tok.getText());
                offsetTracker.recordOffsetChange(0, argsSeen == 0 ? -1 : findPrecedingWhitespaceLength(i));
                offsetTracker.advanceToNewLine(1, 1);
            }
        }

        formattedStrBuf.append(")");
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);

        recordOffsetChangeIfWhitespace(rescannedSymbols.size() - 1);
    }
    
    private void recordOffsetChangeIfWhitespace(int tokNum) {
        CommandToken tok = rescannedSymbols.get(tokNum);
        if(tok instanceof InlineWhitespace) {
            offsetTracker.recordOffsetChange(0, ((InlineWhitespace) tok).getLength());
        }
    }

    /*
     * Outwardly, just quotes the argument.  Inwardly, tracks position changes
     * that the translation entails.
     * TODO-AC: JFlex seems to give every character length 1 (even tab).
     *   If this changes, we won't be able to use indexOf and length to find positions.
     * NB: assumes that inserted characters are not in consecutive positions
     */
    private void formatArg(int argsSeen, int tokNum, Arg tok) {
        formattedStrBuf.append("'");
        offsetTracker.recordOffsetChange(0, argsSeen == 0 ? -1 : findPrecedingWhitespaceLength(tokNum));
        offsetTracker.advanceInLine(1);

        String argText = tok.getArgText();
        String text = tok.getText();
        int textIndex = -1;
        char[] chars = argText.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            int newTextIndex = text.indexOf(ch, textIndex + 1);
            if(textIndex < 0) { //i.e. first iteration
                textIndex = 0;
            }
            offsetTracker.recordOffsetChange(0, newTextIndex - textIndex - 1);
            offsetTracker.advanceInLine(1);
            textIndex = newTextIndex;
            formattedStrBuf.append(ch);
            if(ch == '\'') {
                //will cause this to point at the doubled-up quote, even if there is a corresponding quote in the text
                formattedStrBuf.append("'");
                offsetTracker.recordOffsetChange(0, -1);
                offsetTracker.advanceInLine(1);
            }
        }

        formattedStrBuf.append("'");
        offsetTracker.recordOffsetChange(0, (text.length() - 1) - textIndex - 1);
        offsetTracker.advanceInLine(1);
    }

    /* 
     * The amount of whitespace between the specificed token and the previous
     * non-whitespace token.
     */
    private int findPrecedingWhitespaceLength(int rescannedSymbolNum) {
        if(rescannedSymbolNum > 0) {
            CommandToken precedingTok = rescannedSymbols.get(rescannedSymbolNum - 1);
            if(precedingTok instanceof InlineWhitespace) {
                return ((InlineWhitespace) precedingTok).getLength();
            }
        }
        return 0;
    }
}
