package matlab;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import matlab.ExtractionParser.Terminals;
import beaver.Scanner;
import beaver.Symbol;

public class CommandFormatter {
    private final List<Symbol> originalSymbols;
    private final List<Symbol> rescannedSymbols;
    private final List<Symbol> formattedSymbols;
    private int numArgs;

    private CommandFormatter(List<Symbol> originalSymbols) throws CommandException {
        this.originalSymbols = originalSymbols;
        this.rescannedSymbols = new ArrayList<Symbol>();
        this.formattedSymbols = new ArrayList<Symbol>();
        this.numArgs = 0;
    }

    public static List<Symbol> format(List<Symbol> originalSymbols) throws CommandException {
        if(originalSymbols == null) {
            return null;
        }
        originalSymbols = new ArrayList<Symbol>(originalSymbols);
        if(isNotCmd(originalSymbols)) {
            return originalSymbols;
        }
        CommandFormatter cf = new CommandFormatter(originalSymbols);
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
            Symbol curr = null;
            try {
                curr = scanner.nextToken();
            } catch (Scanner.Exception e) {
                throw new CommandException(e.line, e.column, e.getMessage());
            } catch (IOException e) {
                //can't happen - using a StringReader
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            if(curr.getId() == ExtractionParser.Terminals.EOF) {
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
        int startPos = rescannedSymbols.get(0).getStart();
        //NB: column may have the invalid value zero.
        //    this shouldn't matter since it will still preceded the first position
        recordOffsetChange(Symbol.getLine(startPos), Symbol.getColumn(startPos) - 1, 0, 1);
        colOffsetChangeInLine++;
        formattedSymbols.add(new Symbol("(")); //TODO-AC: id?
        int i = 0;
        for(Symbol sym : rescannedSymbols) {
            formattedSymbols.add(sym);
            int symEndPos = sym.getEnd();
            if(!isFiller(sym)) {
                if(i < numArgs - 1) {
                    recordOffsetChange(symEndPos, 0, 1);
                    colOffsetChangeInLine++;
                    formattedSymbols.add(new Symbol(Terminals.COMMA, ","));
                }
                i++;
            } else if(sym.getId() == Terminals.ELLIPSIS_COMMENT) { //only way to increase line num
                recordOffsetChange(symEndPos, 0, -1 * colOffsetChangeInLine);
                colOffsetChangeInLine = 0;
            }
        }
        
        int endPos = rescannedSymbols.get(rescannedSymbols.size() - 1).getEnd();
        recordOffsetChange(endPos, 0, 1);
        colOffsetChangeInLine++;
        formattedSymbols.add(new Symbol(")"));//TODO-AC: id?
    }
    
    private void recordOffsetChange(int pos, int lineOffsetChange, int colOffsetChange) {
        int line = Symbol.getLine(pos);
        int col = Symbol.getColumn(pos);
        recordOffsetChange(line, col, lineOffsetChange, colOffsetChange);
    }
    
    private void recordOffsetChange(int line, int col, int lineOffsetChange, int colOffsetChange) {
        //interpretation: if position in matlab (NB: not natlab) file is greater than (line, col),
        //  then add the accumulated offsets (i.e. sums of changes) to the matlab position to get the
        //  natlab position.
        System.err.println("[" + line + ", " + col + "]  Offset change: " + lineOffsetChange + " " + colOffsetChange);
    }
    
    private static boolean isFiller(Symbol sym) {
        short type = sym.getId();
        return type == Terminals.OTHER_WHITESPACE || type == Terminals.ELLIPSIS_COMMENT;
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
