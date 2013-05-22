package mclint.util;

import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.Program;

/**
 * Useful methods for manipulating ASTs.
 * @author isbadawi
 */
public class AstUtil {
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

  private AstUtil() {}
}
