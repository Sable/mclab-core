package mclint.refactoring;

import mclint.MatlabProgram;
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
    MatlabProgram program = project.getMatlabProgram("f.m");
    Refactoring rename = Refactorings.renameVariable(basicContext(), node, newName);
    assertTrue(rename.checkPreconditions());
    rename.apply();
  }

  private void assertRenameFails(ASTNode<?> tree, Name name, String newName) {
    MatlabProgram program = project.getMatlabProgram("f.m");
    Refactoring rename = Refactorings.renameVariable(basicContext(), name, newName);
    assertFalse(String.format("Expected renaming of %s to %s to fail", name.getID(), newName),
        rename.checkPreconditions());
  }

  public void testRenameUse() {
    MatlabProgram program = parse("f.m", "x = 0; y = x;");
    Script script = (Script) program.parse();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();

    rename(x, "z");

    assertEquivalent(script, "z = 0; y = z;");
  }

  public void testRenameDef() {
    MatlabProgram program = parse("f.m", "x = 0; y = x;");
    Script script = (Script) program.parse();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(0)).getLHS()).getName();

    rename(x, "z");

    assertEquivalent(script, "z = 0; y = z;");
  }

  public void testRenameArrayGet() {
    MatlabProgram program = parse("f.m", "x = [1,2]; y = x(1);");
    Script script = (Script) program.parse();
    Name x = ((NameExpr) ((ParameterizedExpr) 
        ((AssignStmt) script.getStmt(1)).getRHS()).getTarget()).getName();

    rename(x, "z");

    assertEquivalent(script, "z = [1,2]; y = z(1);");
  }

  public void testRenameArraySet() {
    MatlabProgram program = parse("f.m", "x(2) = 4; y = x(1);");
    Script script = (Script) program.parse();
    Name x = ((NameExpr) ((ParameterizedExpr) 
        ((AssignStmt) script.getStmt(1)).getRHS()).getTarget()).getName();

    rename(x, "z");

    assertEquivalent(script, "z(2) = 4; y = z(1);");
  }

  public void testRenameWithConflict() {
    MatlabProgram program = parse("f.m", "x = 0; y = x;");
    Script script = (Script) program.parse();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();

    assertRenameFails(script, x, "y");
  }

  public void testRenameWithInputParamConflict() {
    MatlabProgram program = parse("f.m", 
        "function x(in)",
        "  x = 0; y = x;",
        "end"
    );
    Function function = ((FunctionList) program.parse()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) function.getStmt(1)).getRHS()).getName();

    assertRenameFails(function, x, "in");
  }

  public void testRenameWithOutputParamConflict() {
    MatlabProgram program = parse("f.m", 
        "function out = x(i)",
        "  x = 0; y = x;",
        "end"
    );
    Function function = ((FunctionList) program.parse()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) function.getStmt(1)).getRHS()).getName();

    assertRenameFails(function, x, "out");
  }

  public void testRenameMultipleAssignment() {
    MatlabProgram program = parse("f.m", "[y, x] = f(); y = x;");
    Script script = (Script) program.parse();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();

    rename(x, "z");

    assertEquivalent(script, "[y, z] = f(); y = z;");
  }

  public void testRenameFunctionParameter() {
    MatlabProgram program = parse("f.m", 
        "function f(x)",
        "  y = x;",
        "end"
    );

    Name x = ((FunctionList) program.parse()).getFunction(0).getInputParam(0);

    rename(x, "z");

    assertEquivalent(program.parse(),
        "function f(z)",
        "  y = z;",
        "end"
    );
  }

  public void testRenameGlobalVariableAcrossFunctions() {
    MatlabProgram program = parse("f.m", 
        "function f1()",
        "  global x; y = x;",
        "end",
        "function f2()",
        "  global x; x = 4;",
        "end"
    );

    Function f1 = ((FunctionList) program.parse()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) f1.getStmt(1)).getRHS()).getName();

    rename(x, "z");

    assertEquivalent(program.parse(), 
        "function f1()",
        "  global z; y = z;",
        "end",
        "function f2()",
        "  global z; z = 4;",
        "end"
    );
  }

  public void testRenameGlobalVariableWithNameConflict() {
    MatlabProgram program = parse("f.m", 
        "function f()",
        "  global x; y = x;",
        "end",
        "function f2()",
        "  global x; x = z;",
        "end"
    );

    Function f1 = ((FunctionList) program.parse()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) f1.getStmt(1)).getRHS()).getName();

    assertRenameFails(program.parse(), x, "z");
  }
}
