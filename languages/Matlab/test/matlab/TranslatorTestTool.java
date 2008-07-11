package matlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;
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
            PrintWriter out = new PrintWriter(new FileWriter(basename + ".out"));
            
            BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));
            PositionMap prePosMap = null;
            
            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();
            
            if(result instanceof NoChangeResult) {
                in = new BufferedReader(new FileReader(basename + ".in")); //just re-open original file
            } else if(result instanceof ProblemResult) {
                for(TranslationProblem prob : ((ProblemResult) result).getProblems()) {
                    out.println("~" + prob);
                }
                out.close();
                System.exit(0); //terminate early since extraction parser can't work without balanced 'end's
            } else if(result instanceof TranslationResult) {
                TranslationResult transResult = (TranslationResult) result;
                in = new BufferedReader(new StringReader(transResult.getText()));
                prePosMap = transResult.getPositionMap();
                System.err.println(transResult.getText());
            }

            ExtractionScanner scanner = new ExtractionScanner(in);
            ExtractionParser parser = new ExtractionParser();
            Program actual = (Program) parser.parse(scanner);
            if(parser.hasError()) {
                for(TranslationProblem prob : parser.getErrors()) {
                    out.println("~" + prob);
                }
            } else {
                List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
                OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
                String destText = actual.translate(offsetTracker, problems);

                if(problems.isEmpty()) {
                    PositionMap posMap = offsetTracker.buildPositionMap();

//                    out.println(">>>> Ends Added -> Matlab");
//                    for(TextPosition destPos : getAllTextPositions(destText)) {
//                        TextPosition sourcePos = prePosMap.getPreTranslationPosition(destPos);
//                        out.println("[" + destPos.getLine() + ", " + destPos.getColumn() + "] -> " +
//                                "(" + sourcePos.getLine() + ", " + sourcePos.getColumn() + ")");
//                    }
//
//                    out.println(">>>> Natlab -> Ends Added");
//                    for(TextPosition destPos : getAllTextPositions(destText)) {
//                        TextPosition sourcePos = posMap.getPreTranslationPosition(destPos);
//                        out.println("[" + destPos.getLine() + ", " + destPos.getColumn() + "] -> " +
//                                "(" + sourcePos.getLine() + ", " + sourcePos.getColumn() + ")");
//                    }
                    
                    if(prePosMap != null) {
                        posMap = new CompositePositionMap(posMap, prePosMap);
                    }

                    out.println(">>>> Destination Language -> Source Language");
                    for(TextPosition destPos : getAllTextPositions(destText)) {
                        TextPosition sourcePos = posMap.getPreTranslationPosition(destPos);
                        out.println("[" + destPos.getLine() + ", " + destPos.getColumn() + "] -> " +
                                "(" + sourcePos.getLine() + ", " + sourcePos.getColumn() + ")");
                    }

                    out.println(">>>> Translated Text");
                    out.print(destText);
                } else {
                    for(TranslationProblem prob : problems) {
                        out.println("~" + prob);
                    }
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
