package mclint.util;

import java.util.List;
import java.util.Map;

import mclint.transform.TokenStreamFragment;
import natlab.utils.AstFunctions;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.EmptyStmt;
import ast.Program;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * Useful methods for manipulating ASTs.
 * @author isbadawi
 */
public class AstUtil {
  /**
   * Returns true if a node is synthetic (i.e. is not the result of parsing).
   */
  public static boolean isSynthetic(ASTNode<?> node) {
    return node.getStartLine() == 0 && !(node instanceof ast.List);
  }

  /**
   * Replaces a subtree with another, correctly updating parent/child links.
   */
  public static void replace(ASTNode<?> oldNode, ASTNode<?> newNode) {
    oldNode.getParent().setChild(newNode, oldNode.getParent().getIndexOfChild(oldNode));
    newNode.setStartPosition(oldNode.getStartLine(), oldNode.getStartColumn());
    newNode.setEndPosition(oldNode.getEndLine(), oldNode.getEndColumn());
  }
  
  /**
   * Removes the given subtree, i.e. disconnects it from its parent.
   */
  public static void remove(ASTNode<?> node) {
    node.getParent().removeChild(node.getParent().getIndexOfChild(node));
  }

  /**
   * Replace a node with the contents of a list. 
   * 
   * Technically this will always work, but it really only makes sense if the replaced node is an
   * element of a list to begin with. 
   */
  public static void replaceWithContents(ASTNode<?> node, ast.List<?> source) {
    ASTNode<?> parent = node.getParent();
    int index = parent.getIndexOfChild(node);
    node.getParent().removeChild(index);
    for (Object element : source) {
      parent.insertChild((ASTNode<?>) element, index++);
    }
  }
  
  public static boolean removed(ASTNode<?> node) {
    return NodeFinder.findParent(Program.class, node) == null;
  }
  
  // This is used by the pretty printer. It's declared here because JastAdd gives a syntax
  // error for the <T extends ASTNode<?>> part.
  public static <T extends ASTNode<?>> String join(String delimiter, Iterable<T> nodes) {
    String result = Joiner.on(delimiter).join(
        Iterables.filter(
          Iterables.transform(nodes, AstFunctions.prettyPrint()),
          Predicates.not(Predicates.containsPattern("^$"))));
    if (!result.isEmpty() && delimiter.equals("\n")) {
      result += "\n";
    }
    return result;
  }
  
  // This is used by the JSON serializer, again declared here because JastAdd won't allow generic
  // methods.
  public static <T extends ASTNode<?>> List<Map<String, Object>> asJsonArray(Iterable<T> nodes) {
    return FluentIterable.from(nodes)
        .filter(Predicates.not(Predicates.instanceOf(EmptyStmt.class)))
        .transform(new Function<ASTNode<?>, Map<String, Object>>() {
      @Override public Map<String, Object> apply(ASTNode<?> node) {
        return node.getJson();
      }
    }).toList();
  }
  
  public static <T extends ASTNode<?>> TokenStreamFragment joinTokens(String delimiter, Iterable<T> nodes) {
    FluentIterable<T> fragments = FluentIterable.from(nodes);
    if (fragments.isEmpty()) {
      return TokenStreamFragment.fromSingleToken("");
    }
    TokenStreamFragment result = Iterables.getFirst(nodes, null).tokenize();
    for (T node : Iterables.skip(nodes, 1)) {
      result = result.spliceBefore(TokenStreamFragment.fromSingleToken(delimiter));
      result = result.spliceBefore(node.tokenize());
    }
    if (delimiter.equals("\n")) {
      result = result.spliceBefore(TokenStreamFragment.fromSingleToken("\n"));
    }
    return result;
  }

  private AstUtil() {}
}
