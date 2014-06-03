package natlab.toolkits.analysis.core;

import java.util.Set;
import java.util.stream.Collectors;

import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.DotExpr;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.NameExpr;
import ast.Stmt;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public class UseDefDefUseChain {
  private ImmutableSetMultimap<Name, Def> useDefChain;
  private ImmutableSetMultimap<Def, Name> defUseChain;
  
  private ImmutableSetMultimap<Stmt, Name> defs;
  private ImmutableSetMultimap<Stmt, Name> uses;
  
  /**
   * Given an occurrence of a variable, return all of the reaching definitions.
   */
  public Set<Def> getDefs(Name use) {
    return useDefChain.get(use);
  }
  
  /**
   * Given a definition return all of the uses reached by this definition. Since
   * definitions can define multiple names, the resulting set might contain occurrences
   * of several different names.
   */
  public Set<Name> getUses(Def def) {
    return defUseChain.get(def);
  }

  /**
    Given a definition, and one of the names it defines, return all of the uses
    of that name reached by this definition.
   */
  public Set<Name> getUsesOf(String name, Def def) {
    return getUses(def).stream()
        .filter(n -> n.getID().equals(name))
        .collect(Collectors.toSet());
  }
  
  /**
   * Given a def, return the names defined by that def.
   */
  public Set<Name> getDefinedNames(Def def) {
    if (def instanceof Name) {
      if (((ASTNode<?>) def).getParent().getParent() instanceof Function) {
        return ImmutableSet.of((Name) def);
      }
      return ImmutableSet.of();
    }
    return getDefinedNames((Stmt) def);
  }
  
  /**
   * Given a statement, return the names defined by that statement.
   */
  public Set<Name> getDefinedNames(Stmt node) {
    return defs.get(node);
  }
  
  /**
   * Given a statement, and one of the names it defines, return the name nodes
   * corresponding to that name.
   */
  public Set<Name> getDefinedNamesOf(String name, Stmt node) {
    return getDefinedNames(node).stream()
        .filter(n -> n.getID().equals(name))
        .collect(Collectors.toSet());
  }
  
  /**
   * Given a def, and one of the names it defines, return the name nodes
   * corresponding to that name.
   */
  public Set<Name> getDefinedNamesOf(String name, Def def) {
    return getDefinedNames(def).stream()
        .filter(n -> n.getID().equals(name))
        .collect(Collectors.toSet());
  }

  /**
   * Given a statement. return the names used by that statement.
   */
  public Set<Name> getUsedNames(Stmt node) {
    return uses.get(node);
  }

  /**
   * Given a statement, and one of the names it uses, return the name nodes
   * corresponding to that name.
   */
  public Set<Name> getUsedNamesOf(String name, Stmt node) {
    return getUsedNames(node).stream()
        .filter(n -> n.getID().equals(name))
        .collect(Collectors.toSet());
  }

  public static UseDefDefUseChain fromReachingDefs(ReachingDefs analysis) {
    Builder builder = new Builder(analysis);
    analysis.getTree().analyze(builder);
    return builder.build();
  }

  private UseDefDefUseChain(
      ImmutableSetMultimap<Name, Def> useDefChain,
      ImmutableSetMultimap<Def, Name> defUseChain,
      ImmutableSetMultimap<Stmt, Name> defs,
      ImmutableSetMultimap<Stmt, Name> uses) {
    this.useDefChain = useDefChain;
    this.defUseChain = defUseChain;
    this.defs = defs;
    this.uses = uses;
  }

  private static class Builder extends AbstractNodeCaseHandler {
    private ReachingDefs analysis;
    private ImmutableSetMultimap.Builder<Name, Def> useDefChainBuilder =
        ImmutableSetMultimap.builder();
    private ImmutableSetMultimap.Builder<Def, Name> defUseChainBuilder =
        ImmutableSetMultimap.builder();
    private ImmutableSetMultimap.Builder<Stmt, Name> defsBuilder =
        ImmutableSetMultimap.builder();
    private ImmutableSetMultimap.Builder<Stmt, Name> usesBuilder =
        ImmutableSetMultimap.builder();

    private Builder(ReachingDefs analysis) {
      this.analysis = analysis;
    }

    private Set<Def> getReachingDefs(NameExpr node) {
      Stmt parent = NodeFinder.findParent(Stmt.class, node);
      return analysis.getInFlowSets().get(parent).get(node.getName().getID());
    }

    @Override public void caseASTNode(ASTNode node) {
      for (int i = 0; i < node.getNumChild(); i++) {
        node.getChild(i).analyze(this);
      }
    }

    @Override public void caseDotExpr(DotExpr node) {
      node.getTarget().analyze(this);
    }

    @Override public void caseNameExpr(NameExpr use) {
      Stmt parentStmt = NodeFinder.findParent(Stmt.class, use);
      if (analysis.isDef(use.getName())) {
        defsBuilder.put(parentStmt, use.getName());
        return;
      }
      usesBuilder.put(parentStmt, use.getName());
      Set<Def> defs = getReachingDefs(use);
      useDefChainBuilder.putAll(use.getName(), defs);
      for (Def def : defs) {
        if (def != ReachingDefs.UNDEF) {
          defUseChainBuilder.put(def, use.getName());
        }
      }
    }
    
    @Override public void caseGlobalStmt(GlobalStmt node) {
      for (Name name : node.getNames()) {
        defsBuilder.put(node, name);
      }
    }

    private UseDefDefUseChain build() {
      return new UseDefDefUseChain(
          useDefChainBuilder.build(),
          defUseChainBuilder.build(),
          defsBuilder.build(),
          usesBuilder.build());
    }
  }
}
