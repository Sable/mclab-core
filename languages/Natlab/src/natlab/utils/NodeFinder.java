package natlab.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ast.ASTNode;

import com.google.common.collect.TreeTraverser;

/*
 * A utility that finds nodes of a certain type in the AST, and returns them as a lazy stream.
 * This class is intended to be used together with the java.util.stream APIs. For example, the
 * following snippet searches <tt>tree</tt> for all functions that aren't nested, returns
 * a list of their names:
 * 
 * <pre>
 *  NodeFinder.find(ast.Function.class, tree))
 *    .filter(f -> !(f.getParent().getParent() instanceof ast.Function))
 *    .map(ast.Function::getName)
 *    .collect(Collectors.toList())
 * </pre>
 */
public class NodeFinder {
  private static class AstNodeTreeTraverser extends TreeTraverser<ASTNode<?>> {
    @Override
    public Iterable<ASTNode<?>> children(ASTNode<?> root) {
      // ASTNode is iterable, but it's Iterable<T extends ASTNode> (without the <?>).
      // If it was Iterable<T extends ASTNode<?>>, we could just write
      // return Iterables.concat(root);
      List<ASTNode<?>> children = new ArrayList<>();
      for (ASTNode<?> child : root) {
        children.add(child);
      }
      return children;
    }
  }

  private static Iterable<ASTNode<?>> allDescendantsOf(ASTNode<?> tree) {
    return new AstNodeTreeTraverser().preOrderTraversal(Objects.requireNonNull(tree));
  }
  
  private static <T> Stream<T> asStream(Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  /**
   * Returns a lazy iterable of the nodes of this finder's type in <tt>tree</tt>.
   */
  public static <T> Stream<T> find(final Class<T> clazz, final ASTNode<?> tree) {
    return asStream(allDescendantsOf(tree)).filter(clazz::isInstance).map(clazz::cast);
  }

  /**
   * Walks up the tree to find a parent of the specified type.
   * Returns null if no such parent exists.
   */
  public static <T> T findParent(Class<T> clazz, ASTNode<?> node) {
    return Stream.iterate(node, ASTNode::getParent)
        .filter(n -> clazz.isInstance(n) || n.getParent() == null)
        .findFirst()
        .filter(clazz::isInstance)
        .map(clazz::cast)
        .orElse(null);
  }
}
