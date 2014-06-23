package mclint.util;

import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Function;
import ast.Name;
import ast.ParameterizedExpr;

/**
 * Provides a quick way to visit every node where a name is (potentially)
 * introduced -- assignments, function input and output parameters, and
 * function names.
 * @author ismail
 *
 */
public abstract class DefinitionVisitor extends AbstractNodeCaseHandler {
  private boolean inLHS = false;
  protected ASTNode<?> tree;
  public DefinitionVisitor(ASTNode<?> tree) {
    this.tree = tree;
  }

  public void caseDefinition(Name node) {
    return;
  }

  public void caseLHSName(Name node) {
    caseDefinition(node);
  }
  public void caseFunctionName(Name node) {
    caseDefinition(node);
  }
  public void caseInParam(Name node) {
    caseDefinition(node);
  }
  public void caseOutParam(Name node) {
    caseDefinition(node);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i)
      node.getChild(i).analyze(this);
  }

  @Override
  public void caseParameterizedExpr(ParameterizedExpr node) {
    node.getTarget().analyze(this);
  }

  @Override
  public void caseCellIndexExpr(CellIndexExpr node) {
    node.getTarget().analyze(this);
  }

  @Override
  public void caseDotExpr(DotExpr node) {
    node.getTarget().analyze(this);
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    inLHS = true;
    node.getLHS().analyze(this);
    inLHS = false;
  }

  @Override
  public void caseName(Name node) {
    if (inLHS)
      caseLHSName(node);
  }

  @Override
  public void caseFunction(Function node) {
    caseFunctionName(node.getName());
    for (Name name : node.getInputParams())
      caseInParam(name);
    for (Name name : node.getOutputParams())
      caseOutParam(name);
    node.getStmts().analyze(this);
  }
}
