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
}