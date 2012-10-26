package matlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;

import org.antlr.runtime.ANTLRReaderStream;

/**
 * A utility for translating from Matlab source to Natlab source.
 * 
 * Exit status:
 *   0 - normal return: successful parse or list of errors
 *   1 - incorrect usage
 *   2 - I/O issue
 *   3 - unexpected parse issue
 */
public class Translator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java matlab.Translator {basename}");
            System.exit(1);
        }
        String basename = args[0];
        try {
            BufferedReader in = new BufferedReader(new FileReader(basename + ".m"));

            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();

            if(result instanceof NoChangeResult) {
                in = new BufferedReader(new FileReader(basename + ".m")); //just re-open original file
            } else if(result instanceof ProblemResult) {
                for(TranslationProblem prob : ((ProblemResult) result).getProblems()) {
                    System.err.println(prob);
                }
                System.exit(0); //terminate early since extraction parser can't work without balanced 'end's
            } else if(result instanceof TranslationResult) {
                TranslationResult transResult = (TranslationResult) result;
                in = new BufferedReader(new StringReader(transResult.getText()));
            }

            OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
            List<TranslationProblem> problems = new ArrayList<TranslationProblem>();
            String destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems);
            
            PrintWriter out = new PrintWriter(new FileWriter(basename + ".n"));
            if(problems.isEmpty()) {
                out.print(destText);
            } else {
                for(TranslationProblem prob : problems) {
                    System.err.println("~" + prob);
                }
            }
            out.close();
            in.close();
            System.exit(0);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
