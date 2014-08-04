package mclint.transform;

import mclint.MatlabProgram;
import mclint.McLintTestCase;
import natlab.DecIntNumericLiteralValue;
import ast.AssignStmt;
import ast.Function;
import ast.FunctionList;
import ast.IntLiteralExpr;
import ast.Name;
import ast.NameExpr;

public class LayoutPreservingTransformerTest extends McLintTestCase {
  public void testRemovingFirstStatement() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2;",
        "  y =  3   +2 ; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt first = (AssignStmt) f.getStmt(0);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(first);
    
    assertEquals(join(
        "function f",
        "  y =  3   +2 ; ",
        "end"
    ), transformer.reconstructText());
  }

  public void testRemovingMiddleStatement() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2;",
        "  z =  4  ",
        "  y =  3   +2 ; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt second = (AssignStmt) f.getStmt(1);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(second);
    
    assertEquals(join(
        "function f",
        "  x =     2;",
        "  y =  3   +2 ; ",
        "end"
    ), transformer.reconstructText());
  }

  public void testRemovingLastStatement() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2;",
        "  y =  3   +2 ; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt second = (AssignStmt) f.getStmt(1);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(second);
    
    assertEquals(join(
        "function f",
        "  x =     2;",
        "end"
    ), transformer.reconstructText());
  }

  public void testRemovingInlineStatementAtEndOfLine() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2; x = 3;",
        "  y =  3   +2 ; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt second = (AssignStmt) f.getStmt(1);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(second);
    
    assertEquals(join(
        "function f",
        "  x =     2;",
        "  y =  3   +2 ; ",
        "end"
    ), transformer.reconstructText());
  }

  public void testRemovingInlineStatementInMiddle() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2; x = 3; y = 3   + 2; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt second = (AssignStmt) f.getStmt(1);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(second);
    
    assertEquals(join(
        "function f",
        "  x =     2; y = 3   + 2; ",
        "end"
    ), transformer.reconstructText());
  }

  public void testRemovingFirstInlineStatement() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2; x = 3; y = 3   + 2; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt first = (AssignStmt) f.getStmt(0);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(first);
    
    assertEquals(join(
        "function f",
        " x = 3; y = 3   + 2; ",
        "end"
    ), transformer.reconstructText());
  }

  public void testRemovingTwoThings() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2; ",
        "  y = 3   + 2; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt first = (AssignStmt) f.getStmt(0);
    AssignStmt second = (AssignStmt) f.getStmt(1);
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.remove(first);
    transformer.remove(second);
    
    assertEquals(join(
        "function f",
        "end"
    ), transformer.reconstructText());
  }
  
  public void testCopyingAStatement() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt first = (AssignStmt) f.getStmt(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(first.getParent(), transformer.copy(first), 1);
    assertEquals(join(
        "function f",
        "  x =     2; ",
        "  x =     2; ",
        "end"
    ), transformer.reconstructText());
  }
  
  public void testInsertingNewCode() {
    MatlabProgram program = parse("f.m",
        "function f",
        "  x =     2; ",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt first = (AssignStmt) f.getStmt(0);
    AssignStmt newCode = new AssignStmt(
        new NameExpr(new Name("x")),
        new IntLiteralExpr(new DecIntNumericLiteralValue("1")));
    
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(first.getParent(), newCode, 1);
    
    assertEquals(join(
        "function f",
        "  x =     2; ",
        "  x = 1;",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingNewCodeIntoEmptyFunction() {
    MatlabProgram program = parse("f.m",
        "function f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    AssignStmt newCode = new AssignStmt(
        new NameExpr(new Name("x")),
        new IntLiteralExpr(new DecIntNumericLiteralValue("1")));

    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getStmts(), newCode, 0);

    assertEquals(join(
        "function f",
        "  x = 1;",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingFirstInputParamWhenParensAreOmitted() {
    MatlabProgram program = parse("f.m",
        "function f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getInputParams(), new Name("x"), 0);
    assertEquals(join(
        "function f(x)",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingFirstInputParamWhenParensArePresent() {
    MatlabProgram program = parse("f.m",
        "function f()",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getInputParams(), new Name("x"), 0);
    assertEquals(join(
        "function f(x)",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingSecondInputParamAfterFirst() {
    MatlabProgram program = parse("f.m",
        "function f(x)",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getInputParams(), new Name("y"), 1);
    assertEquals(join(
        "function f(x, y)",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingSecondInputParamBeforeFirst() {
    MatlabProgram program = parse("f.m",
        "function f(x)",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getInputParams(), new Name("y"), 0);
    assertEquals(join(
        "function f(y, x)",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingInputParamBetweenTwoParams() {
    MatlabProgram program = parse("f.m",
        "function f(x, y)",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getInputParams(), new Name("z"), 1);
    assertEquals(join(
        "function f(x, z, y)",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingFirstOutputParamWhenParensAreOmitted() {
    MatlabProgram program = parse("f.m",
        "function f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getOutputParams(), new Name("x"), 0);
    assertEquals(join(
        "function [x] = f",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingFirstOutputParamWhenParensArePresent() {
    MatlabProgram program = parse("f.m",
        "function [] = f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getOutputParams(), new Name("x"), 0);
    assertEquals(join(
        "function [x] = f",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingSecondOutputParamAfterFirst() {
    MatlabProgram program = parse("f.m",
        "function [x] = f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getOutputParams(), new Name("y"), 1);
    assertEquals(join(
        "function [x, y] = f",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingSecondOutputParamBeforeFirst() {
    MatlabProgram program = parse("f.m",
        "function [x] = f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getOutputParams(), new Name("y"), 0);
    assertEquals(join(
        "function [y, x] = f",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingOutputParamBetweenTwoParams() {
    MatlabProgram program = parse("f.m",
        "function [x, y] = f",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getOutputParams(), new Name("z"), 1);
    assertEquals(join(
        "function [x, z, y] = f",
        "end"
    ), transformer.reconstructText());
  }

  public void testInsertingTwoInputParams() {
    MatlabProgram program = parse("f.m",
        "function f(x)",
        "end"
    );
    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getInputParams(), new Name("y"), 1);
    transformer.insert(f.getInputParams(), new Name("z"), 2);
    assertEquals(join(
        "function f(x, y, z)",
        "end"
    ), transformer.reconstructText());
  }

}