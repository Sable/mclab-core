package natlab.utils;

import java.util.Arrays;

import ast.AssignStmt;
import ast.Expr;
import ast.ExprStmt;
import ast.FunctionHandleExpr;
import ast.IntLiteralExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.StringLiteralExpr;
import natlab.DecIntNumericLiteralValue;

/**
 * This class provides some helpers that you can import statically to make
 * manual AST construction less verbose. For instance, creating an
 * assignment statement like:
 *
 * x = f(4, 'x');
 *
 * looks like:
 *
 * assign(var("x"), call("f", args(integer(4), string("x"))))
 *
 * instead of:
 *
 * new AssignStmt(
 *   new NameExpr(new Name("x")),
 *   new ParameterizedExpr(
 *     new NameExpr(new Name("f")),
 *     new ast.List<>()
 *       .add(new IntLiteralExpr(new DecIntNumericLiteralValue("4")))
 *       .add(new StringLiteralExpr("x")))
 *   )
 * )
 */
public class Asts {
  public static AssignStmt assign(Expr lhs, Expr rhs) {
    return new AssignStmt(lhs, rhs);
  }

  public static ExprStmt stmt(Expr expr) {
    return new ExprStmt(expr);
  }

  public static NameExpr var(String name) {
    return new NameExpr(new Name(name));
  }

  public static ast.List<Expr> args(Expr... args) {
    return new ast.List<Expr>().addAll(Arrays.asList(args));
  }

  public static ParameterizedExpr call(Expr target, ast.List<Expr> args) {
    return new ParameterizedExpr(target, args);
  }

  public static ParameterizedExpr call(String target, ast.List<Expr> args) {
    return call(var(target), args);
  }

  public static ParameterizedExpr index(Expr target, ast.List<Expr> args) {
    return call(target, args);
  }

  public static ParameterizedExpr index(String target, ast.List<Expr> args) {
    return index(var(target), args);
  }

  public static IntLiteralExpr integer(int value) {
    return new IntLiteralExpr(new DecIntNumericLiteralValue(String.valueOf(value)));
  }

  public static StringLiteralExpr string(String value) {
    return new StringLiteralExpr(value);
  }

  public static FunctionHandleExpr handleTo(String name) {
    return new FunctionHandleExpr(new Name(name));
  }
}
