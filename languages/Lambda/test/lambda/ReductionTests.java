package lambda;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import lambda.ast.Node;
import lambda.ast.Root;

public class ReductionTests extends TestCase {
	public void testReduction() throws IOException, Exception {
		checkReduction("x", "x");
		checkReduction("\\x->x", "abs(x, x)");
		checkReduction("x y", "app(x, y)");
		checkReduction("(\\x->x) (\\x->x)", "abs(x, x)");
		checkReduction("(\\y->y) (\\x->x)", "abs(x, x)");
		checkReduction("(\\x->x x) y", "app(y, y)");
		checkReduction("(\\x->x x) y", "app(y, y)");
		checkReduction("(\\x->x) (\\x->x) z", "z");
		checkReduction("(\\x->\\y->x) y", "abs(gen1, y)");
		checkReduction("(\\x->\\gen1->x) gen1", "abs(gen2, gen1)");
	}

	private static void checkReduction(String originalAsString, String expectedStructureString) throws IOException, Exception {
		LambdaScanner scanner = new LambdaScanner(new StringReader(originalAsString));
		LambdaParser parser = new LambdaParser();
		Root original = (Root) parser.parse(scanner);
		
		SPNode reduced = original.reduce();
		assertEquals(originalAsString, expectedStructureString, reduced.getStructureString());
	}
}
