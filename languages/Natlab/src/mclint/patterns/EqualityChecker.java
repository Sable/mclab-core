package mclint.patterns;

import ast.ASTNode;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.List;
import ast.Name;
import ast.StringLiteralExpr;

public class EqualityChecker {
  public static boolean equals(ASTNode<?> left, ASTNode<?> right) {
    // It's convenient to consider a singleton list equivalent to its sole element.
    if (left instanceof List && !(right instanceof List)) {
      return left.getNumChild() == 1 && equals(left.getChild(0), right);
    }
    if (right instanceof List && !(left instanceof List)) {
      return right.getNumChild() == 1 && equals(left, right.getChild(0));
    }
    if (!left.getClass().equals(right.getClass())) {
      return false;
    }
    if (left.getNumChild() != right.getNumChild()) {
      return false;
    }
    if (left.getNumChild() > 0) {
      for (int i = 0; i < left.getNumChild(); ++i) {
        if (!equals(left.getChild(i), right.getChild(i))) {
          return false;
        }
      }
      return true;
    }
    if (left instanceof Name) {
      return ((Name) left).getID().equals(((Name) right).getID());
    }
    if (left instanceof StringLiteralExpr) {
      return ((StringLiteralExpr) left).getValue().equals(((StringLiteralExpr) right).getValue());
    }
    if (left instanceof IntLiteralExpr) {
      return ((IntLiteralExpr) left).getValue().getValue().equals(
          ((IntLiteralExpr) right).getValue().getValue());
    }
    if (left instanceof FPLiteralExpr) {
      return ((FPLiteralExpr) left).getValue().getValue().equals(
          ((FPLiteralExpr) right).getValue().getValue());
    }
    return false;
  }
}
