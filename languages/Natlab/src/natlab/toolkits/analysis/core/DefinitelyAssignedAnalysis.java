package natlab.toolkits.analysis.core;

import java.util.Set;

import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.Sets;

public class DefinitelyAssignedAnalysis extends
    AbstractSimpleStructuralForwardAnalysis<Set<String>> {

  public DefinitelyAssignedAnalysis(ASTNode tree) {
    super(tree);
  }

  @Override
  public void caseScript(Script node) {
    currentInSet = newInitialFlow();
    currentOutSet = Sets.newHashSet(currentInSet);
    node.getStmts().analyze(this);
    outFlowSets.put(node, currentOutSet);
  }

  @Override
  public void caseFunction(Function node) {
    currentInSet = newInitialFlow();
    currentOutSet = Sets.newHashSet(currentInSet);

    for (Name n : node.getInputParams()) {
      currentInSet.add(n.getID());
    }

    node.getStmts().analyze(this);
    outFlowSets.put( node, currentOutSet);

    node.getNestedFunctions().analyze(this);
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = Sets.newHashSet(currentInSet);
    currentOutSet.addAll(node.getLValues());
    outFlowSets.put(node, currentOutSet);
  }
  
  @Override
  public void caseGlobalStmt(GlobalStmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = Sets.newHashSet(currentInSet);
    for (Name name : node.getNames()) {
      currentOutSet.add(name.getID());
    }
    outFlowSets.put(node, currentOutSet);
  }

  @Override
  public void caseStmt(Stmt node){
    inFlowSets.put(node, currentInSet);
    currentOutSet = currentInSet;
    outFlowSets.put(node, currentOutSet);
  }

  @Override
  public void copy(Set<String> source, Set<String> dest) {
    if (source == dest) {
      return;
    }
    dest.clear();
    dest.addAll(source);
  }

  @Override
  public void merge(Set<String> in1, Set<String> in2, Set<String> out) {
    if (in1 == in2 && in2 == out) {
      return;
    }
    if (in1 == out) {
      out.retainAll(in2);
    } else if (in2 == out) {
      out.retainAll(in1);
    }
    out.clear();
    out.addAll(in1);
    out.retainAll(in2);
  }

  @Override
  public Set<String> newInitialFlow() {
    return Sets.newHashSet();
  }
  
  public boolean isDefinitelyAssignedAtInputOf(Stmt node, String name) {
    return inFlowSets.get(node).contains(name);
  }
}
