package matlab;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import matlab.CommandToken.Arg;
import matlab.CommandToken.EllipsisComment;
import matlab.ExtractionParser.Terminals;
import beaver.Scanner;
import beaver.Symbol;

public class CommandFormatter {
    private final OffsetTracker offsetTracker;
    private final List<Symbol> originalSymbols;
    private final List<CommandToken> rescannedSymbols;
    private final List<Symbol> formattedSymbols;
    private int numArgs;

    private CommandFormatter(List<Symbol> originalSymbols, OffsetTracker offsetTracker) {
        this.offsetTracker = offsetTracker;
        this.originalSymbols = originalSymbols;
        this.rescannedSymbols = new ArrayList<CommandToken>();
        this.formattedSymbols = new ArrayList<Symbol>();
        this.numArgs = 0;
    }

    public static List<Symbol> format(List<Symbol> originalSymbols, OffsetTracker offsetTracker) throws CommandException {
        if(originalSymbols == null) {
            return null;
        }
        originalSymbols = new ArrayList<Symbol>(originalSymbols);
        if(isNotCmd(originalSymbols)) {
            return originalSymbols;
        }
        if(offsetTracker == null) {
            //easier than checking for null everywhere
            offsetTracker = new OffsetTracker();
        }
        CommandFormatter cf = new CommandFormatter(originalSymbols, offsetTracker);
        cf.rescan();
        cf.format();
        return cf.formattedSymbols;
    }

    private void rescan() throws CommandException {
        StringBuffer textBuf = new StringBuffer();
        for(Symbol sym : originalSymbols) {
            textBuf.append(sym.value);
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

    //TODO-AC: track position changes
    private void format() {
        int colOffsetChangeInLine = 0;
        int startLine = rescannedSymbols.get(0).getLine();
        int startCol = rescannedSymbols.get(0).getStartCol();
        //NB: column may have the invalid value zero.
        //    this shouldn't matter since it will still preceded the first position
        offsetTracker.recordOffsetChange(startLine, startCol - 1, 0, 1);
        colOffsetChangeInLine++;
        formattedSymbols.add(new Symbol("(")); //TODO-AC: id?
        int i = 0;
        for(CommandToken tok : rescannedSymbols) {
            Symbol sym = convertToSymbol(tok);
            formattedSymbols.add(sym);
            int symEndPos = sym.getEnd();
            if(!isFiller(sym)) {
                if(i < numArgs - 1) {
                    offsetTracker.recordOffsetChange(symEndPos, 0, 1);
                    colOffsetChangeInLine++;
                    formattedSymbols.add(new Symbol(Terminals.COMMA, ","));
                }
                i++;
            } else if(sym.getId() == Terminals.ELLIPSIS_COMMENT) { //only way to increase line num
                offsetTracker.recordOffsetChange(symEndPos, 0, -1 * colOffsetChangeInLine);
                colOffsetChangeInLine = 0;
            }
        }
        
        int endLine = rescannedSymbols.get(rescannedSymbols.size() - 1).getLine();
        int endCol = rescannedSymbols.get(rescannedSymbols.size() - 1).getEndCol();
        offsetTracker.recordOffsetChange(endLine, endCol, 0, 1);
        colOffsetChangeInLine++;
        formattedSymbols.add(new Symbol(")"));//TODO-AC: id?
    }
    
    private static Symbol convertToSymbol(CommandToken tok) {
        int startLine = tok.getLine();
        int startCol = tok.getStartCol();
        int startPos = Symbol.makePosition(startLine, startCol);
        int endLine = tok.getLine();
        int endCol = tok.getEndCol();
        int endPos = Symbol.makePosition(endLine, endCol);
        if(tok instanceof Arg) {
            return new Symbol(Terminals.STRING, startPos, endPos, "'" + ((Arg) tok).getArgText() + "'");
        } else if(tok instanceof EllipsisComment) {
            return new Symbol(Terminals.ELLIPSIS_COMMENT, startPos, endPos, tok.getText());
        } else {
            throw new IllegalArgumentException("Unexpected token type: " + tok.getClass().getName());
        }
    }
    
    private static boolean isFiller(CommandToken tok) {
        return tok instanceof EllipsisComment;
    }
    
    private static boolean isFiller(Symbol sym) {
        short type = sym.getId();
        return (type == Terminals.OTHER_WHITESPACE) || (type == Terminals.ELLIPSIS_COMMENT);
    }

    private static boolean isNotCmd(List<Symbol> originalSymbols) {
        //no args => not a command
        if(originalSymbols == null || originalSymbols.isEmpty()) {
            return true;
        }

        //transpose => no args => not a command
        switch(originalSymbols.get(0).getId()) {
        case Terminals.MTRANSPOSE:
        case Terminals.ARRAYTRANSPOSE:
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
