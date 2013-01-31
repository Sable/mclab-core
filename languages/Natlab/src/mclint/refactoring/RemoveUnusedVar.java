package mclint.refactoring;

import mclint.util.AstUtil;
import natlab.utils.NodeFinder;
import ast.AssignStmt;
import ast.Name;

public class RemoveUnusedVar{
  public static void exec(Name node) {
    // The name is either the lhs of an assignment, or an input parameter
    AssignStmt def = NodeFinder.findParent(AssignStmt.class, node);
    if (def != null) {
      AstUtil.remove(def);
    } else {
      AstUtil.remove(node);
    }
  }
}
