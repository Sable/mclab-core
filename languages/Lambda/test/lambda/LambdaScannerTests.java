package lambda;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import beaver.Symbol;
import beaver.Scanner.Exception;

public class LambdaScannerTests extends TestCase {
	public void testLambdaScanner() throws IOException, Exception {
		checkScan("hello", Arrays.asList(new Symbol(LambdaParser.Terminals.IDENTIFIER, 1, 1, 5, "hello")));
		checkScan("\\ x -> x", Arrays.asList(
				new Symbol(LambdaParser.Terminals.LAMBDA, 1, 1, 1),
				new Symbol(LambdaParser.Terminals.IDENTIFIER, 1, 3, 1, "x"),
				new Symbol(LambdaParser.Terminals.ARG_BODY_SEP, 1, 5, 2),
				new Symbol(LambdaParser.Terminals.IDENTIFIER, 1, 8, 1, "x")));
	}

	private void checkScan(String input, List<Symbol> symbols) throws IOException, Exception {
		LambdaScanner scanner = new LambdaScanner(new StringReader(input));
		int i = 0;
		for(Symbol expected : symbols) {
			Symbol actual = scanner.nextToken();
			assertEquals("Token #" + i, actual, expected);
			i++;
		}
		assertEquals(scanner.nextToken().getId(), LambdaParser.Terminals.EOF);
	}

	private static void assertEquals(String msg, Symbol actual, Symbol expected) {
		assertEquals(msg, actual.getId(), expected.getId());
		assertEquals(msg, actual.getStart(), expected.getStart());
		assertEquals(msg, actual.getEnd(), expected.getEnd());
		assertEquals(msg, actual.value, expected.value);
	}
}
