package natlab.refactoring;

import mclint.McLintTestCase;
import mclint.refactoring.Refactorings;
import mclint.transform.StatementRange;
import ast.Function;
import ast.FunctionList;


public class ExtractFunctionTest extends McLintTestCase {
  private ExtractFunction extract(int from, int to) {
    Function enclosingFunction = ((FunctionList) kit.getAST()).getFunction(0);
    ExtractFunction xf = Refactorings.extractFunction(createBasicTransformerContext(),
        StatementRange.create(enclosingFunction, from, to), "xf");
    xf.apply();
    return xf;
  }

  public void testExtractFunction() {
    parse(
        "function f()",
        "  f = 1;",
        "  b = f + 1;",
        "  disp(b);",
        "end"
    );
    ExtractFunction xf = extract(1, 2);
    assertEquivalent(xf.getExtractedFunction(),
        "function b = xf(f)",
        "  b = f + 1;",
        "end"
    );
  } 

  public void testExtractFunctionMaybeUndefInput() {
    parse(
        "function f()",
        "  if (1)",
        "    f = 1;",
        "  end",
        "  b = f+1;",
        "  disp(b);",
        "end"
    );
    ExtractFunction xf = extract(1, 2);
    assertEquals(Exceptions.FunctionInputCanBeUndefined.class, 
        xf.getErrors().get(0).getClass());
  }

  public void testExtractFunctionMaybeUndefOutput() {
    parse(
        "function f()",
        "  f = 1;",
        "  b = 1;",
        "  if (1)",
        "    b = f+1;",
        "  end",
        "  disp(b);",
        "end");
    ExtractFunction xf = extract(2, 3);
    assertEquals(Exceptions.FunctionOutputCanBeUndefined.class, 
        xf.getErrors().get(0).getClass());

    assertEquivalent(xf.getExtractedFunction(), 
        "function b = xf(f, b)",
        "  if 1",
        "    b = (f + 1);",
        "  end",
        "end"
    );
  }
}