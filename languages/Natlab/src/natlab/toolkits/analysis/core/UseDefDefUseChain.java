package natlab.toolkits.analysis.core;

import java.util.Set;

import ast.AssignStmt;
import ast.NameExpr;

public interface UseDefDefUseChain {
  Set<Def> getDefs(NameExpr use);
  Set<NameExpr> getUses(Def def);
}
