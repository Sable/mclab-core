package natlab.toolkits.analysis.core;

import java.util.Set;
import java.util.stream.Collectors;

import natlab.utils.NodeFinder;
import analysis.BackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.Sets;

public class LivenessAnalysis extends
BackwardAnalysis<Set<String>> {

  public LivenessAnalysis(ASTNode<?> tree) {
    super(tree);
  }

  @Override
  public void caseStmt(Stmt s) {
    outFlowSets.put(s, copy(currentOutSet));
    caseASTNode(s);
    inFlowSets.put(s, copy(currentInSet));
  }

  private NameExpr getLValue(Expr lv) {
    while (!(lv instanceof NameExpr)) {
      if (lv instanceof ParameterizedExpr) {
        lv = ((ParameterizedExpr) lv).getTarget();
      } else if (lv instanceof CellIndexExpr) {
        lv = ((CellIndexExpr) lv).getTarget();
      } else if (lv instanceof DotExpr) {
        lv = ((DotExpr) lv).getTarget();
      }
    }
    return (NameExpr) lv;
  }

  @Override
  public void caseNameExpr(NameExpr ne) {
    currentInSet.add(ne.getName().getID());
  }

  @Override
  public void caseAssignStmt(AssignStmt s) {
    outFlowSets.put(s, copy(currentOutSet));
    Set<NameExpr> lValues = Sets.newHashSet();

    if (s.getLHS() instanceof MatrixExpr) {
      for (Expr expr : ((MatrixExpr) s.getLHS()).getRow(0).getElements()) {
        lValues.add(getLValue(expr));
      }
    } else {
      lValues.add(getLValue(s.getLHS()));
    }

    for (NameExpr n : lValues) {
      currentOutSet.remove(n.getName().getID());
    }

    caseASTNode(s.getRHS());
    currentOutSet.addAll(NodeFinder.find(NameExpr.class, s)
        .filter(name -> !lValues.contains(name))
        .map(name -> name.getName().getID())
        .collect(Collectors.toList()));

    inFlowSets.put(s, copy(currentInSet));
  }

  private void caseFunctionOrScript(ASTNode<?> node, ast.List<Stmt> stmts) {
    currentOutSet = newInitialFlow();
    currentInSet = copy(currentOutSet);
    outFlowSets.put(node, currentOutSet);
    stmts.analyze(this);
    inFlowSets.put(node, currentInSet);
  }

  @Override
  public void caseFunction(ast.Function node) {
    caseFunctionOrScript(node, node.getStmts());
  }

  @Override
  public void caseScript(Script node) {
    caseFunctionOrScript(node, node.getStmts());
  }

  @Override
  public Set<String> copy(Set<String> source) {
    return Sets.newHashSet(source);
  }

  @Override
  public Set<String> merge(Set<String> in1, Set<String> in2) {
    Set<String> out = Sets.newHashSet(in1);
    out.addAll(in2);
    return out;
  }

  @Override
  public Set<String> newInitialFlow() {
    return Sets.newHashSet();
  }
}
