import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import ast.Root;
import beaver.Parser;
import beaver.Scanner;
import beaver.Symbol;

public class LambdaParserTests extends TestCase {
	public void testLambdaScanner() throws IOException, Exception {
		checkParse("x", "x", 1, 1, 1, 1);
		checkParse("f x", "app(f, x)", 1, 1, 1, 3);
		checkParse("f x y z", "app(app(app(f, x), y), z)", 1, 1, 1, 7);
		checkParse("\\ x -> x", "abs(x, x)", 1, 1, 1, 8);
		checkParse("y \\ x -> x", "app(y, abs(x, x))", 1, 1, 1, 10);
		checkParse("\\ x -> x y", "abs(x, app(x, y))", 1, 1, 1, 10);
		checkParse("\\ x -> \\ y -> x y", "abs(x, abs(y, app(x, y)))", 1, 1, 1, 17);

		checkParse("(((x)))", "x", 1, 1, 1, 7);
		checkParse("f (x (y z))", "app(f, app(x, app(y, z)))", 1, 1, 1, 11);
		checkParse("  x\ny\nz  ", "app(app(x, y), z)", 1, 3, 3, 1);
	}

	private void checkParse(String input, String expected, int expectedStartLine, int expectedStartCol, int expectedEndLine, int expectedEndCol) throws IOException, Scanner.Exception, Parser.Exception {
		LambdaScanner scanner = new LambdaScanner(new StringReader(input));
		LambdaParser parser = new LambdaParser();
		Root result = (Root) parser.parse(scanner);
		assertEquals(input, expected, result.getStructureString());

		int actualStartPos = result.getStart();
		int actualStartLine = Symbol.getLine(actualStartPos);
		int actualStartCol = Symbol.getColumn(actualStartPos);
		assertEquals(input, expectedStartLine, actualStartLine);
		assertEquals(input, expectedStartCol, actualStartCol);

		int actualEndPos = result.getEnd();
		int actualEndLine = Symbol.getLine(actualEndPos);
		int actualEndCol = Symbol.getColumn(actualEndPos);
		assertEquals(input, expectedEndLine, actualEndLine);
		assertEquals(input, expectedEndCol, actualEndCol);
	}
}
