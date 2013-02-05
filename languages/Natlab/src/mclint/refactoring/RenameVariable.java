package mclint.refactoring;

import static com.google.common.collect.Iterables.filter;

import java.util.Set;

import mclint.Location;
import mclint.util.AstUtil;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Name;
import ast.NameExpr;
import ast.Program;
import ast.Script;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class RenameVariable {
  public static class NameConflict extends RuntimeException {
    private static final long serialVersionUID = 6581696075512377817L;

    public NameConflict(Name name) {
      super(String.format("The name %s is already used at %s.", name.getID(), Location.of(name)));
    }
  }
  
  private static ASTNode<?> getParentFunctionOrScript(ASTNode<?> node) {
    ast.Function f = NodeFinder.findParent(ast.Function.class, node);
    if (f != null) {
      return f;
    }
    return NodeFinder.findParent(Script.class, node);
  }
  
  private static Set<Name> getLHSTargets(AssignStmt stmt, final String name) {
    Set<Name> names = Sets.newHashSet();
    for (NameExpr node : stmt.getLHS().getNameExpressions()) {
      if (node.getName().getID().equals(name)) {
        names.add(node.getName());
      }
    }
    return names;
  }

  private static Iterable<AssignStmt> getAllDefs(final Name node) {
    Program program = NodeFinder.findParent(Program.class, node);
    return filter(NodeFinder.find(AssignStmt.class, program), new Predicate<AssignStmt>() {
          @Override public boolean apply(AssignStmt stmt) {
            return !Iterables.isEmpty(getLHSTargets(stmt, node.getID()));
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
      for (Name name : getLHSTargets(def, node.getID())) {
        AstUtil.replace(name, new Name(newName));
      }
    }
  }
}
