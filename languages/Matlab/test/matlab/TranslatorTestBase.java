package matlab;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import matlab.ast.Program;

/** 
 * Parent class of the generated ExtractionTranslatorTests class.
 * Provides helper methods to keep ExtractionTranslatorTests short.
 */
class TranslatorTestBase extends TestCase {
    private static final String SEPARATOR = ">>>>";
    private static final Pattern DEST_TO_SOURCE_PATTERN = Pattern.compile("^\\s*\\[([-]?\\d+)\\s*[,]\\s*([-]?\\d+)\\s*\\]\\s*[-][>]\\s*\\(([-]?\\d+)\\s*[,]\\s*([-]?\\d+)\\s*\\)\\s*$");

    /* Construct a scanner that will read from the specified file. */
    static ExtractionScanner getScanner(String filename) throws FileNotFoundException {
        return new ExtractionScanner(new BufferedReader(new FileReader(filename)));
    }

    /*
     * Returns a structure containing the contents of the .out file
     * Format:
     *   >>>> blah
     *   [dest line, dest col] -> (source line, source col)
     *   >>>> blah 
     *   {translated text - see translate.jrag}
     */
    static Structure parseStructure(String filename) throws IOException {
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

        return new Structure(destToSourceMap, translateBuf.toString());
    }

    /* Check deep equality of an AST and the contents of the .out file. */
    public static void assertEquiv(Program actual, Structure expected) {
        OffsetTracker offsetTracker = new OffsetTracker();
        try {
            BufferedReader expectedReader = new BufferedReader(new StringReader(expected.getTranslatedText()));
            BufferedReader actualReader = new BufferedReader(new StringReader(actual.translate(offsetTracker)));
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
        } catch(TranslationException e) {
            assertEquals("Translation exception ", expected.getTranslatedText().substring(1), e.getMessage());
        }

        PositionMap posMap = offsetTracker.buildPositionMap();
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
    static class Structure {
        private final Map<TextPosition, TextPosition> destToSourceMap;
        private final String translatedText;

        private Structure(Map<TextPosition, TextPosition> destToSourceMap, String translatedText) {
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
}
