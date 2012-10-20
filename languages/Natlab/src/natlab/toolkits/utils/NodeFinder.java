package natlab.toolkits.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import natlab.refactoring.AbstractNodeFunction;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.EmptyStmt;

public class NodeFinder {
  public static <T extends ASTNode> List<T> find(ASTNode n, final Class<T> type) {
    final List<T> res = new LinkedList<T>();
    new AbstractNodeCaseHandler() {
      public void caseASTNode(ASTNode n) {
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
  public static <T extends ASTNode> T findParent(ASTNode n, Class<T> type) {
    while (n != null && (!type.isInstance(n))) {
      n = n.getParent();
    }
    if (type.isInstance(n)) {
      return type.cast(n);
    }
    return null;
  }

  public static <T extends ASTNode> void apply(final ASTNode n, final Class<T> type,
      final AbstractNodeFunction<T> func) {
    new AbstractNodeCaseHandler() {
      public void caseASTNode(ASTNode n) {
        if (type.isInstance(n)) {
          func.apply(type.cast(n));
        }
        for (int i = 0; i < n.getNumChild(); i++) {
          caseASTNode(n.getChild(i));
        }
      }
    }.caseASTNode(n);
  }

  public static Map<ASTNode, Integer> nodeCount(ASTNode t) {
    final Map<ASTNode, Integer> res = new HashMap<ASTNode, Integer>();
    new AbstractNodeCaseHandler() {
      int count = 0;
      public void caseASTNode(ASTNode n) {
        int before = count;
        if (n instanceof EmptyStmt) {
          res.put(n, 0);
          return;
        }
        count++;
        for (int i = 0; i < n.getNumChild(); i++) {
          caseASTNode(n.getChild(i));
        }
        res.put(n, count - before);
      }
    }.caseASTNode(t);
    return res;
  }
}