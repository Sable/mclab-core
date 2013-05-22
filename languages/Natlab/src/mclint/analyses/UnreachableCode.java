package mclint.analyses;

import java.util.Arrays;
import java.util.List;

import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.Project;
import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.BreakStmt;
import ast.ContinueStmt;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.ReturnStmt;
import ast.Stmt;

public class UnreachableCode extends AbstractNodeCaseHandler implements LintAnalysis {
  private static final String WARNING =
      "This statement (and possibly following ones) cannot be reached.";
  private static final List<String> THROWS =
      Arrays.asList("throw", "rethrow", "error", "throwAsCaller");
  private ASTNode<?> tree;
  private Lint lint;

  public UnreachableCode(Project project) {
    this.tree = project.asCompilationUnits();
  }

  private Message unreachableCode(ASTNode<?> node) {
    return Message.regarding(node, "DEAD_CODE", WARNING);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i) {
      node.getChild(i).analyze(this);
    }
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    tree.analyze(this);
  }

  private void caseAbruptControlFlow(ASTNode<?> node) {
    @SuppressWarnings("unchecked")
    ast.List<Stmt> body = (ast.List<Stmt>) node.getParent();
    int index = body.getIndexOfChild(node);
    if (index < body.getNumChild() - 1) {
      lint.report(unreachableCode(body.getChild(index + 1)));
    }
  }

  @Override
  public void caseBreakStmt(BreakStmt node) {
    caseAbruptControlFlow(node);
  }

  @Override
  public void caseContinueStmt(ContinueStmt node) {
    caseAbruptControlFlow(node);
  }

  @Override
  public void caseReturnStmt(ReturnStmt node) {
    caseAbruptControlFlow(node);
  }

  @Override
  public void caseParameterizedExpr(ParameterizedExpr node) {
    if (!(node.getTarget() instanceof NameExpr)) {
      return;
    }
    String name = ((NameExpr)(node.getTarget())).getName().getID();
    if (THROWS.contains(name)) {
      caseAbruptControlFlow(NodeFinder.findParent(Stmt.class, node));
    }
  }
}
