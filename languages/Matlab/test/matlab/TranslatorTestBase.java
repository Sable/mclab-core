package matlab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;

import org.antlr.runtime.ANTLRReaderStream;

/** 
 * Parent class of the generated MatlabTranslatorTests class.
 * Provides helper methods to keep MatlabTranslatorTests short.
 */
class TranslatorTestBase extends TestCase {
    private static final String SEPARATOR = ">>>>";
    private static final Pattern DEST_TO_SOURCE_PATTERN = Pattern.compile("^\\s*\\[([-]?\\d+)\\s*[,]\\s*([-]?\\d+)\\s*\\]\\s*[-][>]\\s*\\(([-]?\\d+)\\s*[,]\\s*([-]?\\d+)\\s*\\)\\s*$");

    /*
     * Returns an ActualTranslation containing the contents of the .in file
     * Format: Matlab
     */
    static ActualTranslation parseActualTranslation(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        FunctionEndScanner prescanner = new FunctionEndScanner(in);
        FunctionEndScanner.Result result = prescanner.translate();
        PositionMap prePosMap = null;
        in.close();

        if(result instanceof NoChangeResult) {
            in = new BufferedReader(new FileReader(filename)); //just re-open original file
        } else if(result instanceof ProblemResult) {
            for(TranslationProblem prob : ((ProblemResult) result).getProblems()) {
                System.err.println("~" + prob);
            }
            fail("Encountered problems while filling in optional function ends.");
        } else if(result instanceof TranslationResult) {
            TranslationResult transResult = (TranslationResult) result;
            in = new BufferedReader(new StringReader(transResult.getText()));
            prePosMap = transResult.getPositionMap();
        }

        OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
        List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
        String destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems);

        PositionMap posMap = offsetTracker.buildPositionMap();
        if(prePosMap != null) {
            posMap = new CompositePositionMap(posMap, prePosMap);
        }

        if(problems.isEmpty()) {
            return new ActualTranslation(posMap, destText);
        } else {
            StringBuffer failureStringBuffer = new StringBuffer();
            for(TranslationProblem prob : problems) {
                failureStringBuffer.append("~" + prob + "\n"); //TODO-AC: cross-platform?
            }
            return new ActualTranslation(posMap, failureStringBuffer.toString());
        }
    }

    /*
     * Returns an ExpectedTranslation containing the contents of the .out file
     * Format:
     *   >>>> blah
     *   [dest line, dest col] -> (source line, source col)
     *   >>>> blah 
     *   {translated text - see translate.jrag}
     */
    static ExpectedTranslation parseExpectedTranslation(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        while(in.ready() && !in.readLine().startsWith(SEPARATOR)) {}

        Map<TextPosition, TextPosition> destToSourceMap = new HashMap<TextPosition, TextPosition>();
        while(in.ready()) {
            String line = in.readLine();
            if(line.startsWith(SEPARATOR)) {
                break;
            }
            Matcher matcher = DEST_TO_SOURCE_PATTERN.matcher(line);
            if(!matcher.matches()) {
                fail("Invalid dest-to-source line: " + line);
            }
            int sourceLine = Integer.parseInt(matcher.group(1));
            int sourceCol = Integer.parseInt(matcher.group(2));
            int destLine = Integer.parseInt(matcher.group(3));
            int destCol = Integer.parseInt(matcher.group(4));
            destToSourceMap.put(new TextPosition(sourceLine, sourceCol), new TextPosition(destLine, destCol));
        }

        StringBuffer translateBuf = new StringBuffer();
        while(in.ready()) {
            translateBuf.append(in.readLine());
            translateBuf.append('\n');
        }

        in.close();

        return new ExpectedTranslation(destToSourceMap, translateBuf.toString());
    }

    /* Check deep equality of an AST and the contents of the .out file. */
    public void assertEquiv(ActualTranslation actual, ExpectedTranslation expected) {
        try {
            BufferedReader expectedReader = new BufferedReader(new StringReader(expected.getTranslatedText()));
            BufferedReader actualReader = new BufferedReader(new StringReader(actual.getTranslatedText()));
            while(true) {
                String expectedLine = expectedReader.readLine();
                String actualLine = actualReader.readLine();

                if(!equals(expectedLine, actualLine)) {
                    StringBuffer buf = new StringBuffer();
                    if(expectedLine == null) {
                        buf.append("Actual AST is larger than expected AST:\n");
                        buf.append(actualLine);
                        buf.append('\n');
                        appendRemainingToBuffer(actualReader, buf);
                        fail(buf.toString());
                    } else if(actualLine == null) {
                        buf.append("Expected AST is larger than actual AST:\n");
                        buf.append(expectedLine);
                        buf.append('\n');
                        appendRemainingToBuffer(expectedReader, buf);
                        fail(buf.toString());
                    } else {
                        buf.append("ASTs do not match:\n");
                        buf.append("Remaining expected:\n");
                        buf.append(expectedLine);
                        buf.append('\n');
                        appendRemainingToBuffer(expectedReader, buf);
                        buf.append('\n');
                        buf.append("Remaining actual:\n");
                        buf.append(actualLine);
                        buf.append('\n');
                        appendRemainingToBuffer(actualReader, buf);
                        fail(buf.toString());
                    }
                } else if(actualLine == null) {
                    break;
                }
            }
        } catch(IOException e) {
            //this can't happen since we're reading strings
            e.printStackTrace();
        }

        PositionMap posMap = actual.getDestToSourceMap();
        for(Map.Entry<TextPosition, TextPosition> entry : expected.getDestToSourceMap().entrySet()) {
            TextPosition destPos = entry.getKey();
            TextPosition expectedSourcePos = entry.getValue();
            TextPosition actualSourcePos = posMap.getPreTranslationPosition(destPos);
            if(expectedSourcePos.compareTo(actualSourcePos) != 0) {
                fail("Destination position [" + destPos.getLine() + ", " + destPos.getColumn() + "] mapped to " +
                        "(" + actualSourcePos.getLine() + ", " + actualSourcePos.getColumn() + ") instead of " +
                        "(" + expectedSourcePos.getLine() + ", " + expectedSourcePos.getColumn() + ")");
            }
        }
    }

    private static void appendRemainingToBuffer(BufferedReader reader, StringBuffer buf) throws IOException {
        while(true) {
            String line = reader.readLine();
            if(line == null) {
                break;
            }
            buf.append(line);
            buf.append('\n');
        }
    }

    private static boolean equals(String str1, String str2) {
        if(str1 == null) {
            return str2 == null;
        } else if(str2 == null) {
            return str1 == null;
        } else {
            return str1.equals(str2);
        }
    }

    /* struct for storing .out file contents. */
    static class ExpectedTranslation {
        private final Map<TextPosition, TextPosition> destToSourceMap;
        private final String translatedText;

        private ExpectedTranslation(Map<TextPosition, TextPosition> destToSourceMap, String translatedText) {
            this.destToSourceMap = destToSourceMap;
            this.translatedText = translatedText;
        }

        public Map<TextPosition, TextPosition> getDestToSourceMap() {
            return destToSourceMap;
        }

        public String getTranslatedText() {
            return translatedText;
        }
    }

    static class ActualTranslation {
        private final PositionMap destToSourceMap;
        private final String translatedText;

        private ActualTranslation(PositionMap destToSourceMap, String translatedText) {
            this.destToSourceMap = destToSourceMap;
            this.translatedText = translatedText;
        }

        public PositionMap getDestToSourceMap() {
            return destToSourceMap;
        }

        public String getTranslatedText() {
            return translatedText;
        }
    }
}
