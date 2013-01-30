package mclint.refactoring;

import mclint.McLintTestCase;
import mclint.refactoring.RenameVariable;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.FunctionList;
import ast.Name;
import ast.NameExpr;
import ast.Script;

public class RenameVariableTest extends McLintTestCase {
  private void rename(Name node, String newName) {
    RenameVariable.exec(node, newName, kit.getUseDefDefUseChain());
  }
  
  private void assertRenameFails(ASTNode<?> tree, Name name, String newName) {
    ASTNode<?> copy = tree.fullCopy();
    try {
      rename(name, newName);
      fail(String.format("Expected renaming of %s to %s to fail", name.getID(), newName));
    } catch (RenameVariable.NameConflict e) {}
    assertEquals(copy.getPrettyPrinted(), tree.getPrettyPrinted());
  }

  public void testRenameUse() {
    parse("x = 0; y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();
    
    rename(x, "z");
    
    assertEquivalent("z = 0; y = z;", script);
  }
  
  public void testRenameDef() {
    parse("x = 0; y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(0)).getLHS()).getName();
    
    rename(x, "z");
    
    assertEquivalent("z = 0; y = z;", script);
  }
  
  public void testRenameWithConflict() {
    parse("x = 0; y = x;");
    Script script = (Script) kit.getAST();
    Name x = ((NameExpr) ((AssignStmt) script.getStmt(1)).getRHS()).getName();
    
    assertRenameFails(script, x, "y");
  }
  
  public void testRenameWithInputParamConflict() {
    parse(new StringBuilder()
        .append("function x(in)\n")
        .append("x = 0; y = x;\n")
        .append("end").toString());
    Function function = ((FunctionList) kit.getAST()).getFunction(0);
    Name x = ((NameExpr) ((AssignStmt) function.getStmt(1)).getRHS()).getName();
    
    assertRenameFails(function, x, "in");
  }
}
