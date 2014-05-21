package natlab.refactoring;

import mclint.MatlabProgram;
import mclint.McLintTestCase;
import mclint.refactoring.Refactorings;
import mclint.transform.StatementRange;
import ast.Function;
import ast.FunctionList;


public class ExtractFunctionTest extends McLintTestCase {
  private ExtractFunction extract(int from, int to) {
    MatlabProgram f = project.getMatlabProgram("f.m");
    Function enclosingFunction = ((FunctionList) f.parse()).getFunction(0);
    ExtractFunction xf = Refactorings.extractFunction(basicContext(),
        StatementRange.create(enclosingFunction, from, to), "xf");
    xf.apply();
    return xf;
  }

  public void testExtractFunction() {
    parse("f.m",
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
    parse("f.m",
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
    parse("f.m",
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