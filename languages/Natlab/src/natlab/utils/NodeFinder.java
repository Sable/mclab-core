package natlab.utils;

import java.util.List;

import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;

import com.google.common.collect.Lists;

public class NodeFinder {
  public static <T extends ASTNode<?>> List<T> find(ASTNode<?> n, final Class<T> type) {
    final List<T> res = Lists.newLinkedList();
    new AbstractNodeCaseHandler() {
      public void caseASTNode(@SuppressWarnings("rawtypes") ASTNode n) {
        if (type.isInstance(n)) {
          res.add(type.cast(n));
        }
        for (int i = 0; i < n.getNumChild(); i++) {
          caseASTNode(n.getChild(i));
        }
      }
    }.caseASTNode(n);
    return res;
  }

  /**
   * Walks up the tree to find a parent of the specified type.
   * Returns null if no such parent exists.
   */
  public static <T extends ASTNode<?>> T findParent(ASTNode<?> n, Class<T> type) {
    while (n != null && (!type.isInstance(n))) {
      n = n.getParent();
    }
    if (type.isInstance(n)) {
      return type.cast(n);
    }
    return null;
  }

  public static <T extends ASTNode<?>> void apply(final ASTNode<?> n, final Class<T> type,
      final AbstractNodeFunction<T> func) {
    new AbstractNodeCaseHandler() {
      public void caseASTNode(@SuppressWarnings("rawtypes") ASTNode n) {
        if (type.isInstance(n)) {
          func.apply(type.cast(n));
        }
        for (int i = 0; i < n.getNumChild(); i++) {
          caseASTNode(n.getChild(i));
        }
      }
    }.caseASTNode(n);
  }
}