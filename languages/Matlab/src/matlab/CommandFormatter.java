package matlab;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import matlab.CommandToken.Arg;
import matlab.CommandToken.EllipsisComment;
import matlab.ExtractionParser.Terminals;
import matlab.ast.ArrayExtract;
import matlab.ast.CellArrayExtract;
import beaver.Symbol;

/**
 * Translates a command style invocation into a function style invocation,
 * if appropriate.
 */
public class CommandFormatter {
    //keeping track of position changes
    private final OffsetTracker offsetTracker;
    //input
    private final List<Symbol> originalSymbols;
    //input after being concatenated and run through CommandScanner
    private final List<CommandToken> rescannedSymbols;
    //output
    private final List<Symbol> formattedSymbols;
    //number of arguments after re-scanning
    private int numArgs;

    private CommandFormatter(List<Symbol> originalSymbols, OffsetTracker offsetTracker) {
        this.offsetTracker = offsetTracker;
        this.originalSymbols = originalSymbols;
        this.rescannedSymbols = new ArrayList<CommandToken>();
        this.formattedSymbols = new ArrayList<Symbol>();
        this.numArgs = 0;
    }

    /**
     * Input: Everything after the IDENTIFIER in a stmt starting with an IDENTIFIER.
     * Output: Parenthesized, quoted, comma-delimited arguments or the input if
     *   no translation is necessary
     */
    public static List<Symbol> format(List<Symbol> originalSymbols, OffsetTracker offsetTracker, List<TranslationProblem> problems) {
        if(originalSymbols == null) {
            return null;
        }
        originalSymbols = new ArrayList<Symbol>(originalSymbols);
        if(isNotCmd(originalSymbols)) {
            return null;
        }
        if(offsetTracker == null) {
            //easier than checking for null everywhere
            offsetTracker = new OffsetTracker(new TextPosition(1, 1));
        }
        CommandFormatter cf = new CommandFormatter(originalSymbols, offsetTracker);
        try {
            cf.rescan();
            cf.format();
        } catch (CommandScanner.Exception e) {
            problems.add(e.getProblem());
        }
        return cf.formattedSymbols;
    }

    /*
     * Concatenates the input symbosl together and runs them through CommandScanner.
     */
    private void rescan() throws CommandScanner.Exception {
        StringBuffer textBuf = new StringBuffer();
        for(Symbol sym : originalSymbols) {
            if(sym instanceof ArrayExtract) {
                textBuf.append(((ArrayExtract) sym).getText()); //NB: do NOT translate
            } else {
                textBuf.append(sym.value);
            }
        }

        CommandScanner scanner = new CommandScanner(new StringReader(textBuf.toString()));
        int basePos = originalSymbols.get(0).getStart();
        int baseLine = Symbol.getLine(basePos);
        int baseCol = Symbol.getColumn(basePos);
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
            if(!isFiller(curr)) {
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
        formattedSymbols.add(new Symbol("(")); //TODO-AC: id?
        offsetTracker.recordOffsetChange(0, findPrecedingWhitespaceLength(0));
        offsetTracker.advanceInLine(1);

        int argsSeen = 0; //used to figure out when we're at the last arg
        for(int i = 0; i < rescannedSymbols.size(); i++) {
            CommandToken tok = rescannedSymbols.get(i);
            if(tok instanceof Arg) {
                formatArg(argsSeen, i, (Arg) tok);

                if(argsSeen < numArgs - 1) {
                    formattedSymbols.add(new Symbol(Terminals.COMMA, ","));
                    offsetTracker.recordOffsetChange(0, -1);
                    offsetTracker.advanceInLine(1);
                }
                argsSeen++;
            } else if(tok instanceof EllipsisComment) {
                int startPos = Symbol.makePosition(tok.getLine(), tok.getStartCol());
                int endPos = Symbol.makePosition(tok.getLine(), tok.getEndCol());
                formattedSymbols.add(new Symbol(Terminals.ELLIPSIS_COMMENT, startPos, endPos, tok.getText()));
                offsetTracker.recordOffsetChange(0, argsSeen == 0 ? -1 : findPrecedingWhitespaceLength(i));
                offsetTracker.advanceToNewLine(1, 1);
            }
        }

        formattedSymbols.add(new Symbol(")")); //TODO-AC: id?
        offsetTracker.recordOffsetChange(0, -1);
        offsetTracker.advanceInLine(1);
        
        CommandToken lastRescannedTok = rescannedSymbols.get(rescannedSymbols.size() - 1);
        Symbol lastOriginalSym = originalSymbols.get(originalSymbols.size() - 1);
        offsetTracker.recordOffsetChange(0, Symbol.getColumn(lastOriginalSym.getEnd()) - lastRescannedTok.getEndCol());
    }

    /*
     * Outwardly, just quotes the argument.  Inwardly, tracks position changes
     * that the translation entails.
     * TODO-AC: JFlex seems to give every character length 1 (even tab).
     *   If this changes, we won't be able to use indexOf and length to find positions.
     */
    private void formatArg(int argsSeen, int tokNum, Arg tok) {
        formattedSymbols.add(new Symbol("'")); //TODO-AC: id?
        offsetTracker.recordOffsetChange(0, argsSeen == 0 ? -1 : findPrecedingWhitespaceLength(tokNum));
        offsetTracker.advanceInLine(1);

        String argText = tok.getArgText();
        String text = tok.getText();
        int textIndex = -1;
        for(char ch : argText.toCharArray()) {
            int newTextIndex = text.indexOf(ch, textIndex + 1);
            if(textIndex < 0) {
                textIndex = 0;
            }
            offsetTracker.recordOffsetChange(0, newTextIndex - textIndex - 1);
            offsetTracker.advanceInLine(1);
            textIndex = newTextIndex;
        }

        int argStartPos = Symbol.makePosition(tok.getLine(), tok.getStartCol());
        int argEndPos = Symbol.makePosition(tok.getLine(), tok.getEndCol());
        formattedSymbols.add(new Symbol(Terminals.STRING, argStartPos, argEndPos, argText));

        formattedSymbols.add(new Symbol("'")); //TODO-AC: id?
        offsetTracker.recordOffsetChange(0, (text.length() - 1) - textIndex - 1);
        offsetTracker.advanceInLine(1);
    }

    /* 
     * The amount of whitespace between the specificed token and the previous
     * non-whitespace token.
     */
    private int findPrecedingWhitespaceLength(int rescannedSymbolNum) {
        if(rescannedSymbolNum == 0) {
            int firstNonFillerPos = 0;
            for(Symbol sym : originalSymbols) {
                if(!isFiller(sym)) {
                    //must happen at least once - won't reach this point if there are no args
                    break;
                }
                firstNonFillerPos++;
            }
            int length = 0;
            for(int i = firstNonFillerPos - 1; i >= 0; i--) {
                Symbol sym = originalSymbols.get(i);
                if(sym.getId() != Terminals.OTHER_WHITESPACE) {
                    break;
                }
                int startPos = sym.getStart();
                int endPos = sym.getEnd();
                length += Symbol.getColumn(endPos) - Symbol.getColumn(startPos) + 1;
            }
            return length;
        } else {
            CommandToken tok = rescannedSymbols.get(rescannedSymbolNum);
            CommandToken precedingTok = rescannedSymbols.get(rescannedSymbolNum - 1);
            if(precedingTok instanceof EllipsisComment) {
                //=> first arg on this line - just use pos
                return tok.getStartCol() - 1;
            } else {
                //use distance from previous token
                return tok.getStartCol() - precedingTok.getEndCol() - 1;
            }
        }
    }

    private static boolean isFiller(CommandToken tok) {
        return tok instanceof EllipsisComment;
    }

    private static boolean isFiller(Symbol sym) {
        short type = sym.getId();
        return (type == Terminals.OTHER_WHITESPACE) || (type == Terminals.ELLIPSIS_COMMENT);
    }

    /*
     * Checks to determine whether or not the symbols are really arguments in
     * a command style call
     */
    private static boolean isNotCmd(List<Symbol> originalSymbols) {
        //no args => not a command
        if(originalSymbols == null || originalSymbols.isEmpty()) {
            return true;
        }

        Symbol firstSymbol = originalSymbols.get(0);
        if(firstSymbol instanceof CellArrayExtract) {
            return true;
        }
        switch(firstSymbol.getId()) {
        //transpose => no args => not a command
        case Terminals.MTRANSPOSE:
        case Terminals.ARRAYTRANSPOSE:
        //NB: no filler before cell_array => access / filler => command
        case Terminals.CELL_ARRAY:
            return true;
        }

        Symbol firstNonWhitespace = null;
        for(Symbol sym : originalSymbols) {
            if(!isFiller(sym)) {
                if(firstNonWhitespace == null) {
                    firstNonWhitespace = sym;
                    switch(sym.getId()) {
                    //paren or assign => definitely not a command
                    case Terminals.PARENTHESIZED:
                    case Terminals.ASSIGN:
                        return true;
                    //operator => command iff nothing important follows
                    case Terminals.LT:
                    case Terminals.GT:
                    case Terminals.LE:
                    case Terminals.GE:
                    case Terminals.EQ:
                    case Terminals.NE:
                    case Terminals.AND:
                    case Terminals.OR:
                    case Terminals.SHORTAND:
                    case Terminals.SHORTOR:
                    case Terminals.COLON:
                    case Terminals.MTIMES:
                    case Terminals.ETIMES:
                    case Terminals.MDIV:
                    case Terminals.EDIV:
                    case Terminals.MLDIV:
                    case Terminals.ELDIV:
                    case Terminals.PLUS:
                    case Terminals.MINUS:
                    case Terminals.MPOW:
                    case Terminals.EPOW:
                    case Terminals.DOT:
                        break;
                    //otherwise => definitely a command
                    default:
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
