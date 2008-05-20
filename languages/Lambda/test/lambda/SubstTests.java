package lambda;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;
import lambda.ast.Node;
import lambda.ast.Root;
import lambda.ast.Var;
import beaver.Parser.Exception;

public class SubstTests extends TestCase {
	public void testSubst() throws IOException, Exception {
		checkSubst("x", "x", "y", "y");
		checkSubst("x", "y", "z", "x");
		checkSubst("x x", "x", "q r", "app(app(q, r), app(q, r))");
		checkSubst("\\x->x", "x", "y", "abs(x, x)");
		checkSubst("\\z->x", "x", "y", "abs(z, y)");
		checkSubst("\\y->x", "x", "y", "abs(gen1, y)");
		checkSubst("\\gen1->x", "x", "gen1", "abs(gen2, gen1)");
	}

	private static void checkSubst(String originalAsString, String varName, String valueAsString, String expectedStructureString) throws IOException, Exception {
		LambdaScanner scanner = new LambdaScanner(new StringReader(originalAsString));
		LambdaParser parser = new LambdaParser();
		Root original = (Root) parser.parse(scanner);

		scanner = new LambdaScanner(new StringReader(valueAsString));
		parser = new LambdaParser();
		Root value = (Root) parser.parse(scanner);
		
		Node revised = original.subst(new Var(varName), value.getNode());
		assertEquals(originalAsString, expectedStructureString, revised.getStructureString());
	}
}
