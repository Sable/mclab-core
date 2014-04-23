package mclint.refactoring;

import mclint.McLintTestCase;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.FunctionList;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;

public class RenameVariableTest extends McLintTestCase {
  private void rename(Name node, String newName) {
    Refactoring rename = Refactorings.renameVariable(createBasicTransformerContext(), node, newName);
    assertTrue(rename.checkPreconditions());
    rename.apply();
  }

  private void assertRenameFails(ASTNode<?> tree, Name name, String newName) {
    Refactoring rename = Refactorings.renameVariable(createBasicTransformerContext(), name, newName);
    assertFalse(String.format("Expected renaming of %s to %s to fail", name.getID(), newName),
        rename.checkPreconditions());
  }

  public void testRenameUse() {
    parse("x = 0; y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();

    rename(x, "z");

    assertEquivalent(script, "z = 0; y = z;");
  }

  public void testRenameDef() {
    parse("x = 0; y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(0)).getLHS()).getName();

    rename(x, "z");

    assertEquivalent(script, "z = 0; y = z;");
  }

  public void testRenameArrayGet() {
    parse("x = [1,2]; y = x(1);");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((ParameterizedExpr) 
        ((AssignStmt) script.getStmt(1)).getRHS()).getTarget()).getName();

    rename(x, "z");

    assertEquivalent(script, "z = [1,2]; y = z(1);");
  }

  public void testRenameArraySet() {
    parse("x(2) = 4; y = x(1);");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((ParameterizedExpr) 
        ((AssignStmt) script.getStmt(1)).getRHS()).getTarget()).getName();

    rename(x, "z");

    assertEquivalent(script, "z(2) = 4; y = z(1);");
  }

  public void testRenameWithConflict() {
    parse("x = 0; y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();

    assertRenameFails(script, x, "y");
  }

  public void testRenameWithInputParamConflict() {
    parse(
        "function x(in)",
        "  x = 0; y = x;",
        "end"
    );
    Function function = ((FunctionList) kit.getAST()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) function.getStmt(1)).getRHS()).getName();

    assertRenameFails(function, x, "in");
  }

  public void testRenameWithOutputParamConflict() {
    parse(
        "function out = x(i)",
        "  x = 0; y = x;",
        "end"
    );
    Function function = ((FunctionList) kit.getAST()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) function.getStmt(1)).getRHS()).getName();

    assertRenameFails(function, x, "out");
  }

  public void testRenameMultipleAssignment() {
    parse("[y, x] = f(); y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();

    rename(x, "z");

    assertEquivalent(script, "[y, z] = f(); y = z;");
  }

  public void testRenameFunctionParameter() {
    parse(
        "function f(x)",
        "  y = x;",
        "end"
    );

    Name x = ((FunctionList) kit.getAST()).getFunction(0).getInputParam(0);

    rename(x, "z");

    assertEquivalent(kit.getAST(),
        "function f(z)",
        "  y = z;",
        "end"
    );
  }

  public void testRenameGlobalVariableAcrossFunctions() {
    parse(
        "function f1()",
        "  global x; y = x;",
        "end",
        "function f2()",
        "  global x; x = 4;",
        "end"
    );

    Function f1 = ((FunctionList) kit.getAST()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) f1.getStmt(1)).getRHS()).getName();

    rename(x, "z");

    assertEquivalent(kit.getAST(), 
        "function f1()",
        "  global z; y = z;",
        "end",
        "function f2()",
        "  global z; z = 4;",
        "end"
    );
  }

  public void testRenameGlobalVariableWithNameConflict() {
    parse(
        "function f()",
        "  global x; y = x;",
        "end",
        "function f2()",
        "  global x; x = z;",
        "end"
    );

    Function f1 = ((FunctionList) kit.getAST()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) f1.getStmt(1)).getRHS()).getName();

    assertRenameFails(kit.getAST(), x, "z");
  }
}
