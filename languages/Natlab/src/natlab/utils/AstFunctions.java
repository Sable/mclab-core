package natlab.utils;

import ast.ASTNode;
import ast.Name;
import ast.NameExpr;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Some useful Guava functions for ASTs.
 */
public class AstFunctions {
  public static Function<NameExpr, Name> nameExprToName() {
    return NAME_EXPR_NAME;
  }
  
  public static Function<Name, String> nameToID() {
    return NAME_ID;
  }
  
  public static Function<NameExpr, String> nameExprToID() {
    return NAME_EXPR_ID;
  }
  
  public static Function<ast.Function, String> functionToName() {
    return FUNCTION_NAME;
  }
  
  public static <T extends ASTNode<?>> Function<T, String> prettyPrint() {
    return new Function<T, String>() {
      @Override public String apply(T node) {
        return node.getPrettyPrinted();
      }
    };
  }
  
  private static Function<ast.Function, String> FUNCTION_NAME =
      new Function<ast.Function, String>() {
    @Override public String apply(ast.Function node) {
      return node.getName();
    }
  };
  
  private static Function<NameExpr, Name> NAME_EXPR_NAME = new Function<NameExpr, Name>() {
    @Override public Name apply(NameExpr node) {
      return node.getName();
    }
  };
  
  private static Function<Name, String> NAME_ID = new Function<Name, String>() {
    @Override public String apply(Name node) {
      return node.getID();
    }
  };
  
  private static Function<NameExpr, String> NAME_EXPR_ID =
      Functions.compose(NAME_ID, NAME_EXPR_NAME);
}
