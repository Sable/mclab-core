package mclint.util;

import ast.Expr;
import ast.List;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;

/**
 * Various useful helpers to make manual AST construction less tedious. Designed to be imported
 * statically.
 * 
 * @author isbadawi
 *
 */
public class ASTBuilders {
  public static NameExpr name(String name) {
    return new NameExpr(new Name(name));
  }

  public static List<Expr> exprs(Expr... args) {
    List<Expr> list = new List<Expr>();
    for (Expr expr : args) {
      list.add(expr);
    }
    return list;
  }
  
  public static ParameterizedExpr call(String name, List<Expr> args) {
    return new ParameterizedExpr(name(name), args);
  }

  public static ParameterizedExpr call(String name, Expr... args) {
    return call(name, exprs(args));
  }

  private ASTBuilders() {}
}
