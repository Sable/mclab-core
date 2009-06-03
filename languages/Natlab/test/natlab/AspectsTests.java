package natlab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import natlab.ParserPassTestBase.Structure;
import natlab.ast.Program;
import beaver.Scanner;
import beaver.Symbol;

public class AspectsTests extends TestCase
{
	private final String fileNameScanner = "test/aspects_scanner";
	private final String fileNameParser = "test/aspects_parser";

	private static final Pattern SYMBOL_PATTERN = Pattern.compile("^\\s*([_A-Z]+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*(?:\\s(\\d+))?\\s*(?:\\s=(.*))?$");
	
	public void test_aspects_scanner() throws Exception
	{
		CommentBuffer commentBuffer = new CommentBuffer();
		AspectsScanner scanner = new AspectsScanner(new BufferedReader(new FileReader(fileNameScanner+".in")));
		scanner.setCommentBuffer(commentBuffer);
		List<Symbol> symbols = new ArrayList<Symbol>();
		List<Symbol> comments = new ArrayList<Symbol>();
		Scanner.Exception exception = parseSymbols(fileNameScanner+".out", symbols, comments);
		checkScan(scanner, symbols, comments, exception);
	}

	public void test_aspects_parser() throws Exception {
		CommentBuffer commentBuffer = new CommentBuffer();
		AspectsScanner scanner = new AspectsScanner(new BufferedReader(new FileReader(fileNameParser+".in")));
		scanner.setCommentBuffer(commentBuffer);
		NatlabParser parser = new NatlabParser();
		parser.setCommentBuffer(commentBuffer);
		Program actual = (Program) parser.parse(scanner);
		actual.rawAST = true;
		Structure expected = ParserPassTestBase.parseStructure(fileNameParser+".out");
		ParserPassTestBase.assertEquiv(actual, expected);
	}

	static void checkScan(AspectsScanner scanner, List<Symbol> symbols, List<Symbol> comments, Scanner.Exception exception) 
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
			short expectedId = NatlabParser.Terminals.EOF;
			if(actualId != expectedId) {
				fail("Token #" + tokenNum + ": incorrect token type - " +
						"expected: " + expectedId + " (" + NatlabParser.Terminals.NAMES[expectedId] + ") " +
						"but was: " + actualId + " (" + NatlabParser.Terminals.NAMES[actualId] + ")");
			}
		} catch(Scanner.Exception e) {
			assertEquals(e.getMessage(), e, exception);
		}
		List<Symbol> actualComments = scanner.getCommentBuffer().pollAllComments();
		assertEquals("Number of comments: ", comments.size(), actualComments.size());
		for(int commentNum = 0; commentNum < comments.size(); commentNum++) {
			assertEquals("Comment #" + commentNum + ": ", actualComments.get(commentNum), comments.get(commentNum));
		}
	}

	static Scanner.Exception parseSymbols(String filename, /*OUT*/ List<Symbol> symbols, /*OUT*/ List<Symbol> comments) 
	throws IOException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		BufferedReader in = new BufferedReader(new FileReader(filename));
		while(in.ready()) {
			String line = in.readLine();
			if(isExceptionLine(line)) {
				return parseException(line);
			}
			if(isCommentLine(line)) {
				comments.add(parseComment(line));
			} else {
				symbols.add(parseSymbol(line));
			}
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

	private static boolean isCommentLine(String line) {
		return line.charAt(0) == '#';
	}

	private static Symbol parseComment(String line) 
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		return parseSymbol(line.substring(1));
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
	
	public static void assertEquals(String msg, Symbol actual, Symbol expected) {
		final short expectedId = expected.getId();
		final short actualId = actual.getId();
		if(actualId != expectedId) {
			fail(msg + ": incorrect token type - " +
					"expected: " + expectedId + " (" + NatlabParser.Terminals.NAMES[expectedId] + ") " +
					"but was: " + actualId + " (" + NatlabParser.Terminals.NAMES[actualId] + ")");
		}
		final String expectedValue = (String) expected.value;
		final String actualValue = AspectsScannerTestTool.stringifyValue(actual.value);
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
	
	protected static String getSymbolString(Symbol symbol) {
		return NatlabParser.Terminals.NAMES[symbol.getId()] + (symbol.value == null ? "" : "(" + symbol.value + ")");
	}
}