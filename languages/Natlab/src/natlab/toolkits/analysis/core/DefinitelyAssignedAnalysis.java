package natlab.toolkits.analysis.core;

import natlab.toolkits.analysis.HashSetFlowSet;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.Script;
import ast.Stmt;

public class DefinitelyAssignedAnalysis extends
    AbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> {

  public DefinitelyAssignedAnalysis(ASTNode tree) {
    super(tree);
  }

  @Override
  public void caseScript(Script node) {
    currentInSet = newInitialFlow();
    currentOutSet = currentInSet.copy();
    node.getStmts().analyze(this);
    outFlowSets.put(node, currentOutSet);
  }

  @Override
  public void caseFunction(Function node) {
    currentInSet = newInitialFlow();
    currentOutSet = currentInSet.copy();

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
    currentOutSet = currentInSet.copy();
    currentOutSet.addAll(node.getLValues());
    outFlowSets.put(node, currentOutSet);
  }
  
  @Override
  public void caseGlobalStmt(GlobalStmt node) {
    inFlowSets.put(node, currentInSet);
    currentOutSet = currentInSet.copy();
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
  public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest) {
    source.copy(dest);
  }

  @Override
  public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2,
      HashSetFlowSet<String> out) {
    in1.intersection(in2,out);
  }

  @Override
  public HashSetFlowSet<String> newInitialFlow() {
    return new HashSetFlowSet<String>();
  }
  
  public boolean isDefinitelyAssignedAtInputOf(Stmt node, String name) {
    return inFlowSets.get(node).contains(name);
  }
}
