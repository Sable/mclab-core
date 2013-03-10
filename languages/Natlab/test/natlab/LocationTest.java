package natlab;

import junit.framework.TestCase;
import mclint.util.Parsing;
import ast.Function;
import ast.FunctionList;
import ast.Name;
import ast.Program;

public class LocationTest extends TestCase {
  public void testSingleOutParamHasLocation() {
    Program program = Parsing.string("function x = f\nend\n");
    Function f = ((FunctionList) program).getFunction(0);
    Name x = f.getOutputParam(0);
    assertEquals(1, x.getStartLine());
    assertEquals(10, x.getStartColumn());
  }
  
  public void testOutParamsHaveLocation() {
    Program program = Parsing.string("function [x, y] = f\nend\n");
    Function f = ((FunctionList) program).getFunction(0);
    Name x = f.getOutputParam(0);
    Name y = f.getOutputParam(1);
    assertEquals(1, x.getStartLine());
    assertEquals(11, x.getStartColumn());
    assertEquals(1, y.getStartLine());
    assertEquals(14, y.getStartColumn());
  }
  
  public void testInParamsHaveLocation() {
    Program program = Parsing.string("function f(x, y)\nend\n");
    Function f = ((FunctionList) program).getFunction(0);
    Name x = f.getInputParam(0);
    Name y = f.getInputParam(1);
    assertEquals(1, x.getStartLine());
    assertEquals(12, x.getStartColumn());
    assertEquals(1, y.getStartLine());
    assertEquals(15, y.getStartColumn());
  }
}
