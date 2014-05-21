package mclint.refactoring;

import mclint.transform.Transformer;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.Name;
import ast.NameExpr;
import ast.Stmt;

public class ExtractVariable extends Refactoring {
  private Expr expression;
  private String extractedVariableName;

  ExtractVariable(RefactoringContext context, Expr expression, String extractedVariableName) {
    super(context);
    this.expression = expression;
    this.extractedVariableName = extractedVariableName;
  }

  @Override
  public boolean checkPreconditions() {
    // TODO(isbadawi): check extractedVariableName is not in scope?
    return true;
  }

  @Override
  public void apply() {
    Transformer transformer = context.getTransformer(expression);

    AssignStmt newStmt = new AssignStmt(
        new NameExpr(new Name(extractedVariableName)),
        transformer.copy(expression));
    Stmt parentStmt = NodeFinder.findParent(Stmt.class, expression);
    ASTNode<?> stmtList = parentStmt.getParent();
    transformer.insert(stmtList, newStmt, stmtList.getIndexOfChild(parentStmt));
    transformer.replace(expression, new NameExpr(new Name(extractedVariableName)));
  }
}