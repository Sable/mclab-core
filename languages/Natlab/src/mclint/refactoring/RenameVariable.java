package mclint.refactoring;

import static com.google.common.collect.Iterables.filter;
import mclint.Location;
import mclint.transform.Transformer;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.UseDefDefUseChain;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.Program;
import ast.Script;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

class RenameVariable extends Refactoring {
  private Name node;
  private String newName;
  private UseDefDefUseChain udduChain;
  public RenameVariable(Transformer transformer, Name node, String newName,
      UseDefDefUseChain udduChain) {
    super(transformer);
    this.node = node;
    this.newName = newName;
    this.udduChain = udduChain;
  }

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

  private Iterable<Def> getAllDefsOfTargetName() {
    ASTNode<?> parent = getParentFunctionOrScript(node);
    return filter(NodeFinder.find(Def.class, parent), new Predicate<Def>() {
      @Override public boolean apply(Def def) {
        return !udduChain.getDefinedNamesOf(node.getID(), def).isEmpty();
      }
    });
  }
  
  private void checkSafeRename() {
    ASTNode<?> parent = getParentFunctionOrScript(node);
    for (Name name : NodeFinder.find(Name.class, parent)) {
      if (name.getID().equals(newName)) {
        throw new NameConflict(name);
      }
    }
  }
  
  private ASTNode<?> getRoot(Def node) {
    Program program = NodeFinder.findParent(Program.class, (ASTNode<?>) node);
    CompilationUnits cu = NodeFinder.findParent(CompilationUnits.class, program);
    return Objects.firstNonNull(cu, program);
  }
  
  private static Name findGlobalNamed(final String name, ASTNode<?> tree) {
    return FluentIterable.from(NodeFinder.find(Name.class, tree))
        .firstMatch(new Predicate<Name>() {
          @Override public boolean apply(Name node) {
            return node.getParent().getParent() instanceof GlobalStmt
                && node.getID().equals(name);
          }
        })
        .orNull();
  }

  public void apply(boolean renameGlobally) {
    checkSafeRename();

    for (Def def : getAllDefsOfTargetName()) {
      if (def instanceof GlobalStmt && renameGlobally) {
        ASTNode<?> parent = getParentFunctionOrScript(node);
        for (Function f : NodeFinder.find(Function.class, getRoot(def))) {
          if (parent == f) {
            continue;
          }
          Name decl = findGlobalNamed(node.getID(), f);
          if (decl != null) {
            new RenameVariable(transformer, decl, newName, udduChain).apply(false);
          }
        }
      }
      for (Name use : udduChain.getUsesOf(node.getID(), def)) {
        transformer.replace(use, new Name(newName)); 
      }
      for (Name name : udduChain.getDefinedNamesOf(node.getID(), def)) {
        transformer.replace(name, new Name(newName));
      }
    }
  }

  @Override
  public void apply() {
    apply(true);
  }
}
