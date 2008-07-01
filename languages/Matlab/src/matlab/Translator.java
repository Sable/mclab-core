package matlab;

import java.io.*;

import matlab.TranslationException.Problem;
import matlab.ast.Program;
import beaver.Parser;

/**
 * A utility for translating from Matlab source to Natlab source.
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
			
			ExtractionScanner scanner = new ExtractionScanner(in);
			ExtractionParser parser = new ExtractionParser();
			Program actual = (Program) parser.parse(scanner);
			PrintWriter out = new PrintWriter(new FileWriter(basename + ".n"));
			if(parser.hasError()) {
				for(String error : parser.getErrors()) {
					System.err.println(error);
				}
			} else {
				out.print(actual.translate(null));
			}
			out.close();
			in.close();
			System.exit(0);
		} catch(TranslationException e) {
		    for(Problem prob : e.getProblems()) {
		        System.err.println("[" + prob.getLine() + ", " + prob.getColumn() + "]  " + prob.getMessage());
		    }
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (Parser.Exception e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}
