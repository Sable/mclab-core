package natlab.utils;

import java.util.Iterator;
import java.util.List;

import ast.ASTNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;

/**
 * A utility that finds nodes of a certain type in the AST, and returns them as a lazy iterable.
 * This class is intended to be used together with Guava's Iterable utilities; see also
 * AstPredicates and AstFunctions for utilities related to this. For example, the
 * following snippet searches <tt>tree</tt> for all functions that aren't nested, returns
 * a list of their names:
 * 
 * <pre>
 *  NodeFinder.find(ast.Function.class, tree))
 *    .filter(Predicates.not(AstPredicates.nestedFunction()))
 *    .transform(AstFunctions.functionToName())
 *    .toImmutableList();
 * </pre>
 */
public class NodeFinder {
  private static class AstNodeTreeTraverser extends TreeTraverser<ASTNode<?>> {
    @Override
    public Iterable<ASTNode<?>> children(ASTNode<?> root) {
      // ASTNode is iterable, but it's Iterable<T extends ASTNode> (without the <?>).
      // If it was Iterable<T extends ASTNode<?>>, we could just write
      // return Iterables.concat(root);
      List<ASTNode<?>> children = Lists.newArrayList();
      for (ASTNode<?> child : root) {
        children.add(child);
      }
      return children;
    }
  }

  /**
   * Returns an iterable of all the descendants of <tt>tree</tt>. This corresponds to a
   * depth-first traversal of the subtree rooted at <tt>tree</tt>.
   */
  public static Iterable<ASTNode<?>> allDescendantsOf(ASTNode<?> tree) {
    return new AstNodeTreeTraverser().preOrderTraversal(Preconditions.checkNotNull(tree));
  }
  
  /**
   * Returns an iterable of the ancestors of <tt>tree</tt>. 
   */
  public static Iterable<ASTNode<?>> allAncestorsOf(final ASTNode<?> tree) {
    Preconditions.checkNotNull(tree);
    return new Iterable<ASTNode<?>>() {
      @Override public Iterator<ASTNode<?>> iterator() {
        return new AbstractSequentialIterator<ASTNode<?>>(tree.getParent()) {
          @Override protected ASTNode<?> computeNext(ASTNode<?> previous) {
            return previous.getParent();
          }
        };
      }
    };
  }

  /**
   * Returns a lazy iterable of the nodes of this finder's type in <tt>tree</tt>.
   */
  public static <T> FluentIterable<T> find(final Class<T> clazz, final ASTNode<?> tree) {
    return FluentIterable.from(allDescendantsOf(tree)).filter(clazz);
  }

  /**
   * Walks up the tree to find a parent of the specified type.
   * Returns null if no such parent exists.
   */
  public static <T> T findParent(Class<T> clazz, ASTNode<?> node) {
    return FluentIterable.from(allAncestorsOf(node)).filter(clazz).first().orNull();
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