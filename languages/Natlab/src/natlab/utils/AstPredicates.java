package natlab.utils;

import ast.ASTNode;
import ast.Function;
import ast.Name;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Some useful Guava predicates for ASTs.
 */
public class AstPredicates {
  public static Predicate<Name> named(final String name) {
    return new Predicate<Name>() {
      @Override public boolean apply(Name node) {
        return node.getID().equals(name);
      }
    };
  }
  
  public static <T extends ASTNode<?>, U extends ASTNode<?>> 
    Predicate<T> parentInstanceOf(final Class<U> clazz) {
    return Predicates.compose(Predicates.instanceOf(clazz), AstFunctions.<T>getParent());
  }

  public static Predicate<Function> nestedFunction() {
    return NESTED_FUNCTION;
  }

  private static Predicate<Function> NESTED_FUNCTION = new Predicate<Function>() {
    @Override public boolean apply(Function f) {
      return f.getParent().getParent() instanceof Function;
    }
  };
}
