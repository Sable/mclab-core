package mclint.transform;

import mclint.McLintTestCase;
import natlab.DecIntNumericLiteralValue;
import ast.AssignStmt;
import ast.Function;
import ast.FunctionList;
import ast.IntLiteralExpr;
import ast.Name;
import ast.NameExpr;
import ast.Stmt;

public class IndentationHeuristicsTest extends McLintTestCase {
  private Stmt makeStatement() {
    return new AssignStmt(
      new NameExpr(new Name("y")),
      new IntLiteralExpr(new DecIntNumericLiteralValue("1")));
  }

  public void testInsertingNewStatementIntoFunctionMatchesFollowingIndentation() {
    parse(
      "function f",
      "    x = 4;",
      "end"
    );

    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getStmts(), makeStatement(), 0);

    assertEquals(join(
      "function f",
      "    y = 1;",
      "    x = 4;",
      "end"
    ), transformer.reconstructText());
  }

  public void testInsertingNewStatementIntoFunctionMatchesPriorIndentation() {
    parse(
      "function f",
      "    x = 4;",
      "end"
    );

    Function f = ((FunctionList) program.parse()).getFunction(0);
    Transformer transformer = program.getLayoutPreservingTransformer();
    transformer.insert(f.getStmts(), makeStatement(), 1);

    assertEquals(join(
      "function f",
      "    x = 4;",
      "    y = 1;",
      "end"
    ), transformer.reconstructText());
  }
}
