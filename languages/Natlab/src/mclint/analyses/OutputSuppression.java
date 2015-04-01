package mclint.analyses;

import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.Project;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;

public class OutputSuppression extends AbstractNodeCaseHandler implements LintAnalysis {
  private Lint lint;
  private ASTNode<?> tree;

  private Message suppressOutput(ASTNode<?> node) {
    return Message.regarding(node, "OUTPUT",
        "Terminate this line with a semicolon to suppress output.");
  }

  public OutputSuppression(Project project) {
    this.tree = project.asCompilationUnits();
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    tree.analyze(this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i) {
      node.getChild(i).analyze(this);
    }
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    if (!node.isOutputSuppressed()) {
      lint.report(suppressOutput(node));
    }
    caseASTNode(node);
  }

  @Override
  public void caseForStmt(ForStmt node) {
    node.getStmts().analyze(this);
  }
}
