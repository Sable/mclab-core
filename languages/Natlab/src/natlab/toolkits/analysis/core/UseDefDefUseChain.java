package natlab.toolkits.analysis.core;

import java.util.Set;

import ast.AssignStmt;
import ast.NameExpr;

public interface UseDefDefUseChain {
  Set<AssignStmt> getDefs(NameExpr use);
  Set<NameExpr> getUses(AssignStmt def);
}
