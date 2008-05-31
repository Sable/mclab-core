package natlab;

import java.io.*;

import natlab.ast.Root;
import beaver.Parser;
import beaver.Scanner;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 */
public class ParserTestTool {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java natlab.ParserTestTool {basename}");
			System.exit(1);
		}
		String basename = args[0];
		try {
			BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));
			Scanner scanner = new NatlabScanner(in);
			NatlabParser parser = new NatlabParser();
			Root actual = (Root) parser.parse(scanner);
			PrintWriter out = new PrintWriter(new FileWriter(basename + ".out"));
			if(parser.hasError()) {
				for(String error : parser.getErrors()) {
					out.println(error);
				}
			} else {
				int startPos = actual.getStart();
				int endPos = actual.getEnd();
				out.println(Root.getLine(startPos) + " " + Root.getColumn(startPos));
				out.println(Root.getLine(endPos) + " " + Root.getColumn(endPos));
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
