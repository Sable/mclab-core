package mclint.refactoring;

import static com.google.common.collect.Iterables.filter;

import java.util.Collections;
import java.util.Set;

import mclint.Location;
import mclint.util.AstUtil;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.NameExpr;
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
  
  private static Iterable<Name> getNamesToReplace(Def def, final String name) {
    if (def instanceof AssignStmt) {
      return getLHSTargets((AssignStmt) def, name);
    }
    if (def instanceof GlobalStmt) {
      return filter(((GlobalStmt) def).getNames(), new Predicate<Name>() {
        @Override public boolean apply(Name n) {
          return n.getID().equals(name);
        }
      });
    }
    if (def instanceof Name) {
      if (((Name) def).getID().equals(name)) {
        return Collections.singleton((Name) def);
      }
    }
    return Collections.emptySet();
    
  }

  private static Iterable<Def> getAllDefs(final Name node) {
    ASTNode<?> parent = getParentFunctionOrScript(node);
    return filter(NodeFinder.find(Def.class, parent), new Predicate<Def>() {
          @Override public boolean apply(Def def) {
            if (def instanceof AssignStmt) {
              AssignStmt stmt = (AssignStmt) def;
              return !Iterables.isEmpty(getLHSTargets(stmt, node.getID()));
            }
            if (def instanceof Name) {
              return ((Name) def).getParent().getParent() instanceof Function;
            }
            return true;
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

    for (Def def : getAllDefs(node)) {
      for (NameExpr use : udduChain.getUses(def)) {
        AstUtil.replace(use.getName(), new Name(newName)); 
      }
      for (Name name : getNamesToReplace(def, node.getID())) {
        AstUtil.replace(name, new Name(newName));
      }
    }
  }
}
