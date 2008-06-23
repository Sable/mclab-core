package matlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import matlab.ast.Program;
import beaver.Parser;
import beaver.Symbol;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 */
public class TranslatorTestTool {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java matlab.TranslatorTestTool {basename}");
            System.exit(1);
        }
        String basename = args[0];
        try {
            BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));

            ExtractionScanner scanner = new ExtractionScanner(in);
            ExtractionParser parser = new ExtractionParser();
            Program actual = (Program) parser.parse(scanner);
            PrintWriter out = new PrintWriter(new FileWriter(basename + ".out"));
            if(parser.hasError()) {
                for(String error : parser.getErrors()) {
                    System.err.println(error);
                }
            } else {
                try {
                    OffsetTracker offsetTracker = new OffsetTracker();
                    String sourceText = actual.getStructureString();
                    String destText = actual.translate(offsetTracker);
                    PositionMap posMap = offsetTracker.buildPositionMap();

                    out.println(">>>> Source -> Dest");
                    for(TextPosition sourcePos : getAllTextPositions(sourceText)) {
                        TextPosition destPos = posMap.sourceToDest(sourcePos);
                        out.println("(" + sourcePos.getLine() + ", " + sourcePos.getColumn() + ") -> " +
                                "[" + destPos.getLine() + ", " + destPos.getColumn() + "]");
                    }

                    out.println(">>>> Dest -> Source");
                    for(TextPosition destPos : getAllTextPositions(destText)) {
                        TextPosition sourcePos = posMap.destToSource(destPos);
                        out.println("[" + destPos.getLine() + ", " + destPos.getColumn() + "] -> " +
                                "(" + sourcePos.getLine() + ", " + sourcePos.getColumn() + ")");
                    }

                    out.println(">>>> Translated Text");
                    out.print(destText);
                } catch(TranslationException e) {
                    out.println("~" + e.getMessage());
                }
            }
            out.close();
            in.close();
            System.exit(0);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(2);
        } catch (Parser.Exception e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    private static List<TextPosition> getAllTextPositions(String text) {
        List<TextPosition> allPositions = new ArrayList<TextPosition>();
        StringReader reader = new StringReader(text);
        TrivialScanner scanner = new TrivialScanner(reader);
        try {
            while(true) {
                Symbol sym = scanner.nextToken();
                if(sym.getId() < 0) {
                    break;
                }
                int pos = sym.getStart();
                allPositions.add(new TextPosition(Symbol.getLine(pos), Symbol.getColumn(pos)));
            }
            reader.close();
        } catch(IOException e) {
            //can't happen since StringRead
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return allPositions;
    }
}
