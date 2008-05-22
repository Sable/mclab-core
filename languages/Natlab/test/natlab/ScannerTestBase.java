package natlab;

import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import beaver.Scanner;
import beaver.Symbol;

class ScannerTestBase extends TestCase {
	static void checkScan(Scanner scanner, List<Symbol> symbols, Scanner.Exception exception) 
			throws IOException, Scanner.Exception {
		int i = 0;
		for(Symbol expected : symbols) {
			try {
				Symbol actual = scanner.nextToken();
				assertEquals("Token #" + i, actual, expected);
				i++;
			} catch(Scanner.Exception e) {
				assertEquals(e.getMessage(), e, exception);
				return;
			}
		}
		assertEquals(scanner.nextToken().getId(), NatlabParser.Terminals.EOF);
	}

	static Scanner getScanner(String filename) throws FileNotFoundException {
		return new NatlabScanner(new BufferedReader(new FileReader(filename)));
	}

	static Scanner.Exception parseSymbols(String filename, /*OUT*/ List<Symbol> symbols) 
			throws IOException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		while(in.ready()) {
			String line = in.readLine();
			if(isExceptionLine(line)) {
				return parseException(line);
			}
			symbols.add(parseSymbol(line));
		}
		in.close();
		return null;
	}

	private static boolean isExceptionLine(String line) {
		return Character.isDigit(line.charAt(0));
	}

	private static Scanner.Exception parseException(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		assertEquals("Number of tokens in exception line: " + line, 2, tokenizer.countTokens());
		int lineNum = Integer.parseInt(tokenizer.nextToken());
		int colNum = Integer.parseInt(tokenizer.nextToken());
		return new Scanner.Exception(lineNum, colNum, null);
	}

	private static Symbol parseSymbol(String line) 
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		StringTokenizer tokenizer = new StringTokenizer(line);
		String value = null;
		switch(tokenizer.countTokens()) {
		case 5:
			value = tokenizer.nextToken();
			//NB: fall through
		case 4:
			String idString = tokenizer.nextToken();
			short id = NatlabParser.Terminals.class.getField(idString).getShort(null); //null since the field is static
			int lineNum = Integer.parseInt(tokenizer.nextToken());
			int colNum = Integer.parseInt(tokenizer.nextToken());
			int length = Integer.parseInt(tokenizer.nextToken());
			return new Symbol(id, lineNum, colNum, length, value);
		default:
			fail("Number of tokens in symbol line: " + line);
		return null; //won't actually be reached because of fail
		}
	}

	public static void assertEquals(String msg, Symbol actual, Symbol expected) {
		assertEquals(msg, actual.getId(), expected.getId());
		assertEquals(msg, actual.getStart(), expected.getStart());
		assertEquals(msg, actual.getEnd(), expected.getEnd());
		assertEquals(msg, actual.value, expected.value);
	}

	public static void assertEquals(String msg, Scanner.Exception actual, Scanner.Exception expected) {
		assertEquals(msg, actual.line, expected.line);
		assertEquals(msg, actual.column, expected.column);
	}
}
