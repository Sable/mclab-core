package mclint.refactoring;

import static com.google.common.collect.Iterables.filter;
import mclint.Location;
import mclint.util.AstUtil;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.Name;
import ast.NameExpr;
import ast.Program;
import ast.Script;

import com.google.common.base.Predicate;

public class RenameVariable {
  public static class NameConflict extends RuntimeException {
    private static final long serialVersionUID = 6581696075512377817L;

    public NameConflict(Name name) {
      super(String.format("The name %s is already used at %s.", name.getID(), Location.of(name)));
    }
  }
  
  private static ASTNode<?> getParentFunctionOrScript(ASTNode<?> node) {
    Function f = NodeFinder.findParent(Function.class, node);
    if (f != null) {
      return f;
    }
    return NodeFinder.findParent(Script.class, node);
  }
  
  private static Name getLHSTarget(AssignStmt stmt) {
    ASTNode<?> leftChild = stmt;
    while (leftChild != null && leftChild.getNumChild() > 0) {
      leftChild = leftChild.getChild(0);
    }
    if (!(leftChild instanceof Name)) {
      return null;
    }
    return (Name) leftChild;
  }

  private static Iterable<AssignStmt> getAllDefs(final Name node) {
    Program program = NodeFinder.findParent(Program.class, node);
    return filter(NodeFinder.find(AssignStmt.class, program), new Predicate<AssignStmt>() {
          @Override public boolean apply(AssignStmt stmt) {
            Name target = getLHSTarget(stmt);
            return target != null && target.getID().equals(node.getID());
          }
        });
  }
  
  private static void checkSafeRename(Name node, String newName) {
    ASTNode<?> parent = getParentFunctionOrScript(node);
    for (Name name : NodeFinder.find(Name.class, parent)) {
      if (name.getID().equals(newName)) {
        throw new NameConflict(name);
      }
    }
  }

  public static void exec(Name node, String newName, UseDefDefUseChain udduChain) {
    checkSafeRename(node, newName);

    for (AssignStmt def : getAllDefs(node)) {
      for (NameExpr use : udduChain.getUses(def)) {
        AstUtil.replace(use.getName(), new Name(newName)); 
      }
      AstUtil.replace(getLHSTarget(def), new Name(newName));
    }
  }
}
