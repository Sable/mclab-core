package natlab.toolkits.analysis.core;

import java.util.Set;

import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.DotExpr;
import ast.NameExpr;
import ast.Stmt;

import com.google.common.collect.ImmutableSetMultimap;

public class UseDefDefUseChain {
  private ImmutableSetMultimap<NameExpr, Def> useDefChain;
  private ImmutableSetMultimap<Def, NameExpr> defUseChain;
  
  public Set<Def> getDefs(NameExpr use) {
    return useDefChain.get(use);
  }

  public Set<NameExpr> getUses(Def def) {
    return defUseChain.get(def);
  }

  public static UseDefDefUseChain fromReachingDefs(ReachingDefs analysis) {
    Builder builder = new Builder(analysis);
    analysis.getTree().analyze(builder);
    return builder.build();
  }

  private UseDefDefUseChain(ImmutableSetMultimap<NameExpr, Def> useDefChain,
          ImmutableSetMultimap<Def, NameExpr> defUseChain) {
    this.useDefChain = useDefChain;
    this.defUseChain = defUseChain;
  }

  private static class Builder extends AbstractNodeCaseHandler {
    private ReachingDefs analysis;
    private ImmutableSetMultimap.Builder<NameExpr, Def> useDefChainBuilder =
            ImmutableSetMultimap.builder();
    private ImmutableSetMultimap.Builder<Def, NameExpr> defUseChainBuilder =
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
      if (analysis.isDef(use.getName())) {
        return;
      }
      Set<Def> defs = getReachingDefs(use);
      useDefChainBuilder.putAll(use, defs);
      for (Def def : defs) {
        if (def != ReachingDefs.UNDEF) {
          defUseChainBuilder.put(def, use);
        }
      }
    }

    private UseDefDefUseChain build() {
      return new UseDefDefUseChain(useDefChainBuilder.build(), defUseChainBuilder.build());
    }
  }
}
