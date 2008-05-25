package natlab;

import java.io.*;

import beaver.Scanner;
import beaver.Symbol;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 */
public class ScannerTestTool {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java natlab.ScannerTestTool {basename}");
			System.exit(1);
		}
		String basename = args[0];
		try {
			BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));
			Scanner scanner = new NatlabScanner(in);
			PrintWriter out = new PrintWriter(new FileWriter(basename + ".out"));
			while(true) {
				Symbol curr = null;
				try {
					curr = scanner.nextToken();
				} catch (Scanner.Exception e) {
					out.print('~');
					out.print(' ');
					out.print(e.line + 1);
					out.print(' ');
					out.println(e.column + 1);
					break;
				}
				if(curr.getId() == NatlabParser.Terminals.EOF) {
					break;
				}
				out.print(NatlabParser.Terminals.NAMES[curr.getId()]);
				out.print(' ');
				int start = curr.getStart();
				int startLine = Symbol.getLine(start);
				int startCol = Symbol.getColumn(start);
				out.print(startLine);
				out.print(' ');
				out.print(startCol);
				out.print(' ');
				out.print(curr.getEnd() - start + 1);
				if(curr.value != null) {
					out.print(' ');
					out.print(curr.value);
				}
				out.println();
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
