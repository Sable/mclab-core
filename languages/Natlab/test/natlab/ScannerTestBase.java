package natlab;

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

class ScannerTestBase extends TestCase {
	private static final Pattern SYMBOL_PATTERN = Pattern.compile("^\\s*([_A-Z]+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*(?:\\s=(.*))?$");
	
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
		try {
			short actualId = scanner.nextToken().getId();
			short expectedId = NatlabParser.Terminals.EOF;
			if(actualId != expectedId) {
				fail("Token #" + i + ": incorrect token type - " +
						"expected: " + expectedId + " (" + NatlabParser.Terminals.NAMES[expectedId] + ") " +
						"but was: " + actualId + " (" + NatlabParser.Terminals.NAMES[actualId] + ")");
			}
		} catch(Scanner.Exception e) {
			assertEquals(e.getMessage(), e, exception);
		}
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
		return line.charAt(0) == '~';
	}

	private static Scanner.Exception parseException(String line) {
		StringTokenizer tokenizer = new StringTokenizer(line.substring(1));
		assertEquals("Number of tokens in exception line: " + line, 2, tokenizer.countTokens());
		int lineNum = Integer.parseInt(tokenizer.nextToken());
		int colNum = Integer.parseInt(tokenizer.nextToken());
		return new Scanner.Exception(lineNum, colNum, null);
	}

	private static Symbol parseSymbol(String line) 
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		Matcher matcher = SYMBOL_PATTERN.matcher(line);
		if(!matcher.matches()) {
			fail("Invalid line: " + line);
		}
		String idString = matcher.group(1);
		short id = NatlabParser.Terminals.class.getField(idString).getShort(null); //null since the field is static
		int lineNum = Integer.parseInt(matcher.group(2));
		int colNum = Integer.parseInt(matcher.group(3));
		int length = Integer.parseInt(matcher.group(4));
		String value = matcher.group(5);
		return new Symbol(id, lineNum, colNum, length, value);
	}

	public static void assertEquals(String msg, Symbol actual, Symbol expected) {
		final short expectedId = expected.getId();
		final short actualId = actual.getId();
		if(actualId != expectedId) {
			fail(msg + ": incorrect token type - " +
					"expected: " + expectedId + " (" + NatlabParser.Terminals.NAMES[expectedId] + ") " +
					"but was: " + actualId + " (" + NatlabParser.Terminals.NAMES[actualId] + ")");
		}
		final String expectedValue = (String) expected.value;
		final String actualValue = ScannerTestTool.stringifyValue(actual.value);
		if(((actualValue == null || expectedValue == null) && (actualValue != expectedValue)) || 
				(actualValue != null && !actualValue.equals(expectedValue))) {
			fail(msg + " - " + NatlabParser.Terminals.NAMES[actualId] + ": incorrect token value - " +
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

	public static void assertEquals(String msg, Scanner.Exception actual, Scanner.Exception expected) {
		assertEquals(msg + " - exception line number", actual.line, expected.line);
		assertEquals(msg + " - exception column number", actual.column, expected.column);
	}

	protected static String getSymbolString(Symbol symbol) {
		return NatlabParser.Terminals.NAMES[symbol.getId()] + (symbol.value == null ? "" : "(" + symbol.value + ")");
	}
}
