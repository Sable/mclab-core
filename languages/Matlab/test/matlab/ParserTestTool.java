package matlab;

import java.io.*;

import matlab.ast.Program;
import beaver.Parser;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 */
public class ParserTestTool {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java matlab.ParserTestTool {basename}");
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
                for(TranslationProblem prob : parser.getErrors()) {
                    System.err.println("[" + prob.getLine() + ", " + prob.getColumn() + "]  " + prob.getMessage());
                }
			} else {
				int startPos = actual.getStart();
				int endPos = actual.getEnd();
				out.println(Program.getLine(startPos) + " " + Program.getColumn(startPos));
				out.println(Program.getLine(endPos) + " " + Program.getColumn(endPos));
				out.print(actual.getStructureString());
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
}
