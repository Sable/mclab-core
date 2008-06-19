package matlab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import beaver.Scanner;
import beaver.Symbol;

/** 
 * Parent class of the generated ExtractionScannerTests class.
 * Provides helper methods to keep ExtractionScannerTests short.
 */
class ScannerTestBase extends TestCase {
	private static final Pattern SYMBOL_PATTERN = Pattern.compile("^\\s*([_A-Z]+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*(?:\\s(\\d+))?\\s*(?:\\s=(.*))?$");

	/* Compare the output of the scanner with a list of expected symbols and an optional exception. */
	static void checkScan(ExtractionScanner scanner, List<Symbol> symbols, Scanner.Exception exception) 
			throws IOException, Scanner.Exception {
		int tokenNum = 0;
		for(Symbol expected : symbols) {
			try {
				Symbol actual = scanner.nextToken();
				assertEquals("Token #" + tokenNum, actual, expected);
				tokenNum++;
			} catch(Scanner.Exception e) {
				assertEquals(e.getMessage(), e, exception);
				return;
			}
		}
		try {
			short actualId = scanner.nextToken().getId();
			short expectedId = ExtractionParser.Terminals.EOF;
			if(actualId != expectedId) {
				fail("Token #" + tokenNum + ": incorrect token type - " +
						"expected: " + expectedId + " (" + ExtractionParser.Terminals.NAMES[expectedId] + ") " +
						"but was: " + actualId + " (" + ExtractionParser.Terminals.NAMES[actualId] + ")");
			}
		} catch(Scanner.Exception e) {
			assertEquals(e.getMessage(), e, exception);
		}
	}

	/* Construct a scanner that will read from the specified file. */
	static ExtractionScanner getScanner(String filename) throws FileNotFoundException {
		return new ExtractionScanner(new BufferedReader(new FileReader(filename)));
	}

	/* 
	 * Read symbols from the .out file.
	 * Returns an exception if there is an exception line and null otherwise.
	 */
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

	/* Returns true if the line should be treated as an exception line. */
	private static boolean isExceptionLine(String line) {
		return line.charAt(0) == '~';
	}

	/*
	 * Constructs a Scanner.Exception from a line of text.
	 * Format:
	 *   ~ start_line start_col
	 */
	private static Scanner.Exception parseException(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line.substring(1));
		assertEquals("Number of tokens in exception line: " + line, 2, tokenizer.countTokens());
		int lineNum = Integer.parseInt(tokenizer.nextToken());
		int colNum = Integer.parseInt(tokenizer.nextToken());
		return new Scanner.Exception(lineNum, colNum, null);
	}

	/*
	 * Constructs a Symbol from a line of text.
	 * Format:
	 *   TYPE start_line start_col length [=value]
	 *   e.g. FOR 2 1 3
	 *        IDENTIFIER 3 2 1 =x
	 */
	private static Symbol parseSymbol(String line) 
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		Matcher matcher = SYMBOL_PATTERN.matcher(line);
		if(!matcher.matches()) {
			fail("Invalid line: " + line);
		}
		String idString = matcher.group(1);
		short id = ExtractionParser.Terminals.class.getField(idString).getShort(null); //null since the field is static
		int lineNum = Integer.parseInt(matcher.group(2));
		int colNum = Integer.parseInt(matcher.group(3));
		String value = matcher.group(6);
		if(matcher.group(5) == null) {
			int length = Integer.parseInt(matcher.group(4));
			return new Symbol(id, lineNum, colNum, length, value);
		} else {
			int endLine = Integer.parseInt(matcher.group(4));
			int endCol = Integer.parseInt(matcher.group(5));
			int startPos = Symbol.makePosition(lineNum, colNum);
			int endPos = Symbol.makePosition(endLine, endCol);
			return new Symbol(id, startPos, endPos, value);
		}
	}

	/* Checks deep equality of two Symbols (complexity comes from giving nice error messages). */
	public static void assertEquals(String msg, Symbol actual, Symbol expected) {
		final short expectedId = expected.getId();
		final short actualId = actual.getId();
		if(actualId != expectedId) {
			fail(msg + ": incorrect token type - " +
					"expected: " + expectedId + " (" + ExtractionParser.Terminals.NAMES[expectedId] + ") " +
					"but was: " + actualId + " (" + ExtractionParser.Terminals.NAMES[actualId] + ")");
		}
		final String expectedValue = (String) expected.value;
		final String actualValue = ScannerTestTool.stringifyValue(actual.value);
		if(((actualValue == null || expectedValue == null) && (actualValue != expectedValue)) || 
				(actualValue != null && !actualValue.equals(expectedValue))) {
			fail(msg + " - " + ExtractionParser.Terminals.NAMES[actualId] + ": incorrect token value - " +
					"expected: " + expectedValue + " " +
					"but was: " + actualValue);
			
		}
		final int expectedStart = expected.getStart();
		final int actualStart = actual.getStart();
		if(actualStart != expectedStart) {
			fail(msg + " - " + getSymbolString(actual) + ": incorrect start position - " +
					"expected: line " + Symbol.getLine(expectedStart) + " col " + Symbol.getColumn(expectedStart) + " " +
					"but was: line " + Symbol.getLine(actualStart) + " col " + Symbol.getColumn(actualStart));
		}
		final int expectedEnd = expected.getEnd();
		final int actualEnd = actual.getEnd();
		if(actualEnd != expectedEnd) {
			fail(msg + " - " + getSymbolString(actual) + ": incorrect end position - " +
					"expected: line " + Symbol.getLine(expectedEnd) + " col " + Symbol.getColumn(expectedEnd) + " " +
					"but was: line " + Symbol.getLine(actualEnd) + " col " + Symbol.getColumn(actualEnd));
		}
	}

	/* Checks deep equality of two Scanner.Exceptions. */
	public static void assertEquals(String msg, Scanner.Exception actual, Scanner.Exception expected) {
		if(actual == null) {
			if(expected == null) {
				return;
			} else {
				fail("Actual exception was unexpectedly null (expected: [" + actual.line + ", " + actual.column + "] " + expected + ")");
			}
		} else if(expected == null) {
			fail("Unexpected exception: [" + actual.line + ", " + actual.column + "] " + actual);
		}
		assertEquals(msg + " - exception line number", actual.line, expected.line);
		assertEquals(msg + " - exception column number", actual.column, expected.column);
	}

	/* Returns a pretty-printed version of a Symbol. */
	protected static String getSymbolString(Symbol symbol) {
		return ExtractionParser.Terminals.NAMES[symbol.getId()] + (symbol.value == null ? "" : "(" + symbol.value + ")");
	}
}
