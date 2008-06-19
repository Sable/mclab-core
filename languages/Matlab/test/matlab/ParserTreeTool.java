package matlab;

import java.io.*;

import matlab.ast.ListNode;

import beaver.Parser;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 * This tool prints out the parse tree of the program of the input file,
 * and currently, it also prints out the structured string of the program.
 * The output file is named as: basename + ".tree" 
 */
public class ParserTreeTool {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java matlab.ParserTreeTool {basename}");
			System.exit(1);
		}
		String basename = args[0];
		try {
			BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));
			
			ExtractionScanner scanner = new ExtractionScanner(in);
			ExtractionParser parser = new ExtractionParser();
			ListNode actual = (ListNode) parser.parse(scanner);
			PrintWriter out = new PrintWriter(new FileWriter(basename + ".tree"));
			if(parser.hasError()) {
				for(String error : parser.getErrors()) {
					out.println(error);
				}
			} else {
				int startPos = actual.getStart();
				int endPos = actual.getEnd();
				out.println(ListNode.getLine(startPos) + " " + ListNode.getColumn(startPos));
				out.println(ListNode.getLine(endPos) + " " + ListNode.getColumn(endPos));
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
