package natlab.refactoring;

import mclint.McLintTestCase;
import ast.FunctionList;


public class ExtractFunctionTest extends McLintTestCase {
  private ExtractFunction extract(int from, int to) {
    ExtractFunction xf = new ExtractFunction(createContext(),
        ((FunctionList) kit.getAST()).getFunction(0), from, to, "xf");
    xf.apply();
    return xf;
  }

  public void testExtractFunction() {
    parse("function f()\n" +
        "f = 1;\n" +
        "b = f+1;\n" +
        "disp(b);\n" +
        "end");
    ExtractFunction xf = extract(1, 2);
    assertEquals("function [b] = xf(f)\n" +
        "  b = (f + 1);\n" +
        "end", xf.getExtractedFunction().getPrettyPrinted());
  } 

  public void testExtractFunctionMaybeUndefInput() {
    parse("function f()\n" +
        "if (1)\n" + 
        "  f = 1;\n" +
        "end\n" + 
        "b = f+1;\n" +
        "disp(b);\n" +
        "end");
    ExtractFunction xf = extract(1, 2);
    assertEquals(Exceptions.FunctionInputCanBeUndefined.class, 
        xf.getErrors().get(0).getClass());
  }

  public void testExtractFunctionMaybeUndefOutput() {
    parse("function f()\n" +
        "f = 1;\n" +
        "b = 1;\n" +
        "if (1)\n" +
        "  b = f+1;\n" +
        "end\n" +
        "disp(b);\n" +
        "end");
    ExtractFunction xf = extract(2, 3);
    assertEquals(Exceptions.FunctionOutputCanBeUndefined.class, 
        xf.getErrors().get(0).getClass());

    assertEquals("function [b] = xf(f, b)\n" +
        "  if 1\n" +
        "    b = (f + 1);\n" +
        "  end\n" +
        "end", xf.getExtractedFunction().getPrettyPrinted());
  }
}