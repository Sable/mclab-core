package natlab.utils;

import java.util.Iterator;
import java.util.Stack;

import ast.ASTNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;

/**
 * A utility that finds nodes of a certain type in the AST, and returns them as a lazy iterable.
 * This class is intended to be used together with Guava's Iterable utilities. For example, the
 * following snippet searches <tt>tree</tt> for all functions that aren't nested, and
 * returns a list of their names:
 * 
 * <pre>
 *  // Iterables.transform and Iterables.filter imported statically
 *  
 *  transform(filter(NodeFinder.find(ast.Function.class, tree), new Predicate<ast.Function>() {
 *   @Override public boolean apply(ast.Function f) {
 *     return !(f.getParent().getParent() instanceof ast.Function);
 *   }), new Function<ast.Function, String>() {
 *   @Override public String apply(ast.Function f) {
 *     return f.getName();
 *   }
 *  });
 * </pre>
 */
public class NodeFinder {
  /**
   * Returns a lazy iterable of the nodes of this finder's type in <tt>tree</tt>.
   */
  public static <T> Iterable<T> find(final Class<T> clazz, final ASTNode<?> tree) {
    Preconditions.checkNotNull(tree);
    return new Iterable<T>() {
      @Override public Iterator<T> iterator() {
        final Stack<ASTNode<?>> toVisit = new Stack<ASTNode<?>>();
        toVisit.push(tree);
        return new AbstractIterator<T>() {
          @Override protected T computeNext() {
            while (!toVisit.empty()) {
              ASTNode<?> next = toVisit.pop();
              for (int i = 0; i < next.getNumChild(); i++) {
                toVisit.push(next.getChild(i));
              }
              if (clazz.isInstance(next)) {
                return clazz.cast(next);
              }
            }
            return endOfData();
          }
        };
      }
    };
  }

  /**
   * Walks up the tree to find a parent of the specified type.
   * Returns null if no such parent exists.
   */
  public static <T> T findParent(Class<T> clazz, ASTNode<?> node) {
    Preconditions.checkNotNull(node);
    while (node != null && (!clazz.isInstance(node))) {
      node = node.getParent();
    }
    if (clazz.isInstance(node)) {
      return clazz.cast(node);
    }
    return null;
  }

  /**
   * Applies <tt>func</tt> to each node of type <tt>type</tt> in <tt>n</tt>.
   */
  public static <T> void apply(final Class<T> type, final ASTNode<?> n,
      final AbstractNodeFunction<T> func) {
    for (T node : NodeFinder.find(type, n)) {
      func.apply(node);
    }
  }
}