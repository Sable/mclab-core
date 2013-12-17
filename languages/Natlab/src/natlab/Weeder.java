package natlab;

import java.util.List;

import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.BreakStmt;
import ast.ContinueStmt;
import ast.Expr;
import ast.ForStmt;
import ast.LValueExpr;
import ast.MatrixExpr;
import ast.Program;
import ast.Script;
import ast.Stmt;
import ast.WhileStmt;

public class Weeder extends AbstractNodeCaseHandler {
  private List<CompilationProblem> errors;
  private boolean inMatrix;
  public Weeder(List<CompilationProblem> errors) {
    super();
    this.errors = errors;
  }
  
  public static void check(Program program, List<CompilationProblem> errors) {
    program.analyze(new Weeder(errors));
  }
  
  private void reportError(ASTNode node, String message) {
    errors.add(new CompilationProblem(node.getStartLine(), node.getStartColumn(), message));
  }

  @Override public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i) {
      node.getChild(i).analyze(this);
    }
  }
  
  private boolean insideLoopOrScript(Stmt stmt) {
    return NodeFinder.findParent(Script.class, stmt) != null ||
        NodeFinder.findParent(ForStmt.class, stmt) != null ||
        NodeFinder.findParent(WhileStmt.class, stmt) != null;
  }
  
  @Override public void caseBreakStmt(BreakStmt stmt) {
    if (!insideLoopOrScript(stmt)) {
      reportError(stmt, "Break statement outside loop or script");
    }
  }
  
  @Override public void caseContinueStmt(ContinueStmt stmt) {
    if (!insideLoopOrScript(stmt)) {
      reportError(stmt, "Continue statement outside loop or script");
    }
  }
  
  @Override public void caseAssignStmt(AssignStmt stmt) {
    if (stmt.getLHS() instanceof MatrixExpr) {
      if (((MatrixExpr) stmt.getLHS()).getNumRow() != 1) {
        reportError(stmt, "Invalid left-hand side of assignment");
      }
      inMatrix = true;
      caseASTNode(stmt.getLHS());
      inMatrix = false;
    } else if (!(stmt.getLHS() instanceof LValueExpr)) {
      reportError(stmt, "Invalid left-hand side of assignment");
    }
  }
  
  @Override public void caseExpr(Expr node) {
    if (inMatrix && !(node instanceof LValueExpr)) {
      reportError(node, "Invalid left-hand side of assignment");
    }
  }
}
