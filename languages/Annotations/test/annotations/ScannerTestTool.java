package annotations;

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
			System.err.println("Usage: java annotations.ScannerTestTool {basename}");
			System.exit(1);
		}
		String basename = args[0];
		try {
			BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));
			AnnotationScanner scanner = new AnnotationScanner(in);
			PrintWriter out = new PrintWriter(new FileWriter(basename + ".out"));
			while(true) {
				Symbol curr = null;
				try {
					curr = scanner.nextToken();
				} catch (Scanner.Exception e) {
					out.print('~');
					out.print(' ');
					out.print(e.line);
					out.print(' ');
					out.println(e.column);
					break;
				}
				if(curr.getId() == AnnotationParser.Terminals.EOF) {
					break;
				}
				printSymbol(out, curr);
			}
			out.close();
			in.close();
			System.exit(0);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	public static void printSymbol(PrintWriter out, Symbol symbol) {
		out.print(AnnotationParser.Terminals.NAMES[symbol.getId()]);
		out.print(' ');
		int start = symbol.getStart();
		int startLine = Symbol.getLine(start);
		int startCol = Symbol.getColumn(start);
		int end = symbol.getEnd();
		int endLine = Symbol.getLine(end);
		int endCol = Symbol.getColumn(end);
		if(startLine == endLine) {
			out.print(startLine);
			out.print(' ');
			out.print(startCol);
			out.print(' ');
			out.print(endCol - startCol + 1);
		} else {
			out.print(startLine);
			out.print(' ');
			out.print(startCol);
			out.print(' ');
			out.print(endLine);
			out.print(' ');
			out.print(endCol);
		}
		if(symbol.value != null) {
			out.print(' ');
			out.print('=');
			out.print(stringifyValue(symbol.value));
		}
		out.println();
	}
	
	public static String stringifyValue(Object value) {
		if(value == null) {
			return null;
		}
		return value.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
	}
}
