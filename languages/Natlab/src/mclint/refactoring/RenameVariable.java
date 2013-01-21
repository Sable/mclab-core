package mclint.refactoring;

import mclint.util.AstUtil;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;
import ast.AssignStmt;
import ast.Name;
import ast.NameExpr;
import ast.Program;

import com.google.common.base.Predicate;

public class RenameVariable {
  private static Iterable<AssignStmt> getAllDefs(final Name node) {
    Program program = NodeFinder.of(Program.class).findParent(node);
    return NodeFinder.of(AssignStmt.class)
        .filter(new Predicate<AssignStmt>() {
          @Override public boolean apply(AssignStmt stmt) {
            if (!(stmt.getLHS() instanceof NameExpr)) {
              return false;
            }
            return ((NameExpr) stmt.getLHS()).getName().getID().equals(node.getID());
          }
        })
        .findIn(program);
  }
  public static void exec(Name node, String newName, UseDefDefUseChain udduChain) {
    for (AssignStmt def : getAllDefs(node)) {
      for (NameExpr use : udduChain.getUses(def)) {
        AstUtil.replace(use.getName(), new Name(newName)); 
      }
      AstUtil.replace(def, new AssignStmt(new NameExpr(new Name(newName)), def.getRHS()));
    }
  }
}
