package matlab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;

/** 
 * Parent class of the generated MatlabScannerTests class.
 * Provides helper methods to keep MatlabScannerTests short.
 */
class ScannerTestBase extends TestCase {
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("^\\s*([_A-Z]+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*(?:\\s(\\d+))?\\s*(?:\\s=(.*))?$");

    /* Compare the output of the scanner with a list of expected symbols and an optional exception. */
    @SuppressWarnings("unchecked")
    static void checkScan(MatlabLexer lexer, List<Symbol> symbols, TextPosition exceptionPos) throws IOException {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        List<Token> actualTokens = tokens.getTokens(); //prefetch all tokens
        int tokenNum = 0;
        for(Symbol expected : symbols) {
            Token actual = actualTokens.get(tokenNum);
            assertEquals("Token #" + tokenNum, actual, expected);
            tokenNum++;
            if(lexer.hasProblem()) {
                TranslationProblem prob = lexer.getProblems().get(0);
                assertEquals(prob.getMessage(), new TextPosition(prob.getLine(), prob.getColumn()), exceptionPos);
                return;
            }
        }
        if(exceptionPos == null && tokenNum < actualTokens.size()) {
            int actualType = tokens.get(tokenNum).getType();
            int expectedType = MatlabParser.EOF;
            fail("Token #" + tokenNum + ": incorrect token type - " +
                    "expected: " + expectedType + " (EOF) " +
                    "but was: " + actualType + " (" + MatlabParser.tokenNames[actualType] + ")");
        }
        if(lexer.hasProblem()) {
            TranslationProblem prob = lexer.getProblems().get(0);
            assertEquals(prob.getMessage(), new TextPosition(prob.getLine(), prob.getColumn()), exceptionPos);
            return;
        } else if(exceptionPos != null) {
            fail("Expected exception at " + exceptionPos);
        }
    }

    /* Construct a scanner that will read from the specified file. */
    static MatlabLexer getLexer(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        MatlabLexer lexer = new MatlabLexer(new ANTLRReaderStream(in));
        return lexer;
    }

    /* 
     * Read symbols from the .out file.
     * Returns a position if there is an exception line and null otherwise.
     */
    static TextPosition parseSymbols(String filename, /*OUT*/ List<Symbol> symbols) 
            throws IOException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        while(in.ready()) {
            String line = in.readLine();
            if(isExceptionLine(line)) {
                return getExceptionPosition(line);
            }
            symbols.add(parseSymbol(line));
        }
        in.close();
        return null;
    }

    /* Returns true if the line should be treated as an exception line. */
    private static boolean isExceptionLine(String line) {
        return line.charAt(0) == '~';
    }

    /*
     * Constructs a TextPosition from a line of text.
     * Format:
     *   ~ start_line start_col
     */
    private static TextPosition getExceptionPosition(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line.substring(1));
        assertEquals("Number of tokens in exception line: " + line, 2, tokenizer.countTokens());
        int lineNum = Integer.parseInt(tokenizer.nextToken());
        int colNum = Integer.parseInt(tokenizer.nextToken());
        return new TextPosition(lineNum, colNum);
    }

    /*
     * Constructs a Symbol from a line of text.
     * Format:
     *   TYPE start_line start_col length [=value]
     *   e.g. FOR 2 1 3
     *        IDENTIFIER 3 2 1 =x
     */
    private static Symbol parseSymbol(String line) 
            throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        Matcher matcher = SYMBOL_PATTERN.matcher(line);
        if(!matcher.matches()) {
            fail("Invalid line: " + line);
        }
        String idString = matcher.group(1);
        int id = MatlabParser.class.getField(idString).getInt(null); //null since the field is static
        int startLine = Integer.parseInt(matcher.group(2));
        int startCol = Integer.parseInt(matcher.group(3));
        String value = matcher.group(6);
        if(matcher.group(5) == null) {
            int length = Integer.parseInt(matcher.group(4));
            return new Symbol(id, startLine, startCol, startLine, startCol + length - 1, value);
        } else {
            int endLine = Integer.parseInt(matcher.group(4));
            int endCol = Integer.parseInt(matcher.group(5));
            return new Symbol(id, startLine, startCol, endLine, endCol, value);
        }
    }

    /* Checks deep equality of two Symbols (complexity comes from giving nice error messages). */
    public static void assertEquals(String msg, Token actual, Symbol expected) throws IOException {
        final int expectedId = expected.getType();
        final int actualId = actual.getType();
        if(actualId != expectedId) {
            fail(msg + ": incorrect token type - " +
                    "expected: " + expectedId + " (" + MatlabParser.tokenNames[expectedId] + ") " +
                    "but was: " + actualId + " (" + MatlabParser.tokenNames[actualId] + ")");
        }
        final String expectedValue = expected.getText();
        final String actualValue = ScannerTestTool.stringifyValue(actual.getText());
        if(((actualValue == null || expectedValue == null) && (actualValue != expectedValue)) || 
                (actualValue != null && !actualValue.equals(expectedValue))) {
            fail(msg + " - " + MatlabParser.tokenNames[actualId] + ": incorrect token value - " +
                    "expected: " + expectedValue + " " +
                    "but was: " + actualValue);

        }
        final int expectedStartLine = expected.getStartLine();
        final int expectedStartCol = expected.getStartCol();
        final int actualStartLine = actual.getLine();
        final int actualStartCol = actual.getCharPositionInLine() + 1;
        if(expectedStartLine != actualStartLine || expectedStartCol != actualStartCol) {
            fail(msg + " - " + getTokenString(actual) + ": incorrect start position - " +
                    "expected: line " + expectedStartLine + " col " + expectedStartCol + " " +
                    "but was: line " + actualStartLine + " col " + actualStartCol);
        }
        final int expectedEndLine = expected.getEndLine();
        final int expectedEndCol = expected.getEndCol();
        TextPosition lastPos = ScannerTestTool.getLastPosition(actual.getText());
        final int actualEndLine = actualStartLine + lastPos.getLine() - 1;
        final int actualEndCol = (lastPos.getLine() == 1) ? actualStartCol + lastPos.getColumn() - 1 : lastPos.getColumn();
        if(expectedEndLine != actualEndLine || expectedEndCol != actualEndCol) {
            fail(msg + " - " + getTokenString(actual) + ": incorrect end position - " +
                    "expected: line " + expectedEndLine + " col " + expectedEndCol + " " +
                    "but was: line " + actualEndLine + " col " + actualEndCol);
        }
    }

    /* Checks deep equality of two error TextPositions. */
    public static void assertEquals(String msg, TextPosition actual, TextPosition expected) {
        if(actual == null) {
            if(expected == null) {
                return;
            } else {
                fail("Actual exception was unexpectedly null (expected: [" + actual.getLine() + ", " + actual.getColumn() + "] " + expected + ")");
            }
        } else if(expected == null) {
            fail("Unexpected exception: [" + actual.getLine() + ", " + actual.getColumn() + "] " + actual);
        }
        assertEquals(msg + " - exception line number", actual.getLine(), expected.getLine());
        assertEquals(msg + " - exception column number", actual.getColumn(), expected.getColumn());
    }

    /* Returns a pretty-printed version of a Symbol. */
    protected static String getTokenString(Token tok) {
        return MatlabParser.tokenNames[tok.getType()] + "(" + tok.getText() + ")";
    }

    static class Symbol {
        private final int startLine;
        private final int startCol;
        private final int endLine;
        private final int endCol;
        private final int type;
        private final String text;

        Symbol(int type, int startLine, int startCol, int endLine, int endCol, String text) {
            this.startLine = startLine;
            this.startCol = startCol;
            this.endLine = endLine;
            this.endCol = endCol;
            this.type = type;
            this.text = text;
        }

        public int getStartLine() {
            return startLine;
        }

        public int getStartCol() {
            return startCol;
        }

        public int getEndLine() {
            return endLine;
        }

        public int getEndCol() {
            return endCol;
        }

        public int getType() {
            return type;
        }

        public String getText() {
            return text;
        }

        public String toString() {
            return MatlabParser.tokenNames[type] + "[" + type + "] - (" + startLine + ", " + startCol + ") to (" + endLine + ", " + endCol + ") = " + text;
        }
    }
}
