package natlab.utils;

import java.util.List;

import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/**
 * A utility that finds nodes in an AST, optionally satisfying a given
 * predicate, and optionally transforms them using a given function. For example, the
 * following snippet searches <tt>tree</tt> for all functions that aren't nested, and
 * returns a list of their names:
 * 
 * <pre>
 * NodeFinder.of(ast.Function.class)
 *     .filter(new Predicate<ast.Function>() {
 *       @Override public boolean apply(ast.Function f) {
 *         return !(f.getParent().getParent() instanceof ast.Function);
 *       }
 *     })
 *     .transform(new Function<ast.Function, String>() {
 *       @Override public String apply(ast.Function f) {
 *         return f.getName();
 *       }
 *     })
 *     .findIn(tree);
 * </pre>
 */
public class NodeFinder<F extends ASTNode<?>, T> {
  private Class<F> clazz;
  private Predicate<F> predicate;
  private Function<F, T> transformer;

  /**
   * Returns a finder that finds nodes of the given class.
   */
  public static <F extends ASTNode<?>> NodeFinder<F, F> of(Class<F> clazz) {
    return new NodeFinder<F, F>(clazz, Predicates.<F>alwaysTrue(), Functions.<F>identity());
  }

  private NodeFinder(Class<F> clazz, Predicate<F> predicate, Function<F, T> transformer) {
    this.clazz = clazz;
    this.predicate = predicate;
    this.transformer = transformer;
  }

  /**
   * Returns a finder with the same behavior as this finder, except omitting nodes
   * that don't satisfy the given predicate. This predicate replaces any previous
   * predicate, so there's no reason to call this more than once.
   */
  public NodeFinder<F, T> filter(Predicate<F> predicate) {
    return new NodeFinder<F, T>(clazz, Preconditions.checkNotNull(predicate), transformer);
  }

  /**
   * Returns a finder with the same behavior as this finder, except that nodes
   * found are transformed using the given function.
   */
  public <S> NodeFinder<F, S> transform(Function<F, S> transformer) {
    return new NodeFinder<F, S>(clazz, predicate, Preconditions.checkNotNull(transformer));
  }

  /**
   * Returns a list of the nodes of this finder's type in <tt>tree</tt>;
   * if a predicate was previously given, the nodes must satisfy it, and if a transformer was
   * previously given, it is applied.
   */
  // TODO(isbadawi): figure out how to make this return a lazy iterable
  public List<T> findIn(ASTNode<?> tree) {
    Preconditions.checkNotNull(tree);
    final List<T> res = Lists.newLinkedList();
    new AbstractNodeCaseHandler() {
      public void caseASTNode(@SuppressWarnings("rawtypes") ASTNode n) {
        if (clazz.isInstance(n)) {
          F cast = clazz.cast(n);
          if (predicate.apply(cast)) {
            res.add(transformer.apply(cast));
          }
        }
        for (int i = 0; i < n.getNumChild(); i++) {
          caseASTNode(n.getChild(i));
        }
      }
    }.caseASTNode(tree);
    return res;
  }
  
  /**
   * Returns the parent of the given node which is of this finder's type, or null if
   * no such parent exists, or if it does but doesn't satisfy the previously provided
   * predicate, if any. If a transformer was previously given, it is applied.
   */
  public T findParent(ASTNode<?> node) {
    Preconditions.checkNotNull(node);
    while (node != null && (!clazz.isInstance(node))) {
      node = node.getParent();
    }
    if (clazz.isInstance(node)) {
      F cast = clazz.cast(node);
      if (predicate.apply(cast)) {
        return transformer.apply(cast);
      }
    }
    return null;
  }
  
  /**
   * Applies the given function (for its side effects)
   * on children of <tt>tree</tt> with this finder's type.
   */
  public void applyIn(ASTNode<?> tree, final AbstractNodeFunction<F> function) {
    filter(new Predicate<F>() {
      @Override public boolean apply(F node) {
        boolean result = predicate.apply(node);
        if (result) {
          function.apply(node);
        }
        return result;
      }
    })
    .findIn(tree);
  }

  /**
   * Search <tt>n</tt> for nodes of type <tt>type</tt>.
   * Equivalent to <tt>NodeFinder.of(type).findIn(n)</tt>.
   */
  @Deprecated
  public static <T extends ASTNode<?>> List<T> find(ASTNode<?> n, Class<T> type) {
    return NodeFinder.of(type).findIn(n);
  }

  /**
   * Walks up the tree to find a parent of the specified type.
   * Returns null if no such parent exists.
   * Equivalent to <tt>NodeFinder.of(type).findParent(n)</tt>.
   */
  @Deprecated
  public static <T extends ASTNode<?>> T findParent(ASTNode<?> n, Class<T> type) {
    return NodeFinder.of(type).findParent(n);
  }

  /**
   * Applies <tt>func</tt> to each node of type <tt>type</tt> in <tt>n</tt>.
   * Equivalent to <tt>NodeFinder.of(type).applyIn(n, func)</tt>.
   */
  @Deprecated
  public static <T extends ASTNode<?>> void apply(final ASTNode<?> n, final Class<T> type,
      final AbstractNodeFunction<T> func) {
    NodeFinder.of(type).applyIn(n, func);
  }
}