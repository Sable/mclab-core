package mclint.transform;

import ast.ASTNode;
import ast.Program;

/**
 * A Transformer performs AST transformations.
 */
public interface Transformer {
  void replace(ASTNode<?> oldNode, ASTNode<?> newNode);
  void remove(ASTNode<?> node);
  void insert(ASTNode<?> node, ASTNode<?> newNode, int i);
  Program getProgram();
  String reconstructText();
}
