package mclint.transform;

import mclint.util.AstUtil;
import ast.ASTNode;
import ast.Program;

class BasicTransformer implements Transformer {
  private Program program;
  
  static BasicTransformer of(Program program) {
    return new BasicTransformer(program);
  }
  
  private BasicTransformer(Program program) {
    this.program = program;
  }

  @Override
  public void replace(ASTNode<?> oldNode, ASTNode<?> newNode) {
    AstUtil.replace(oldNode, newNode);
  }

  @Override
  public void remove(ASTNode<?> node) {
    AstUtil.remove(node);
  }
  
  @Override
  public void insert(ASTNode<?> node, ASTNode<?> newNode, int i) {
    node.insertChild(newNode, i);
  }
  
  @Override @SuppressWarnings("unchecked")
  public <T extends ASTNode<?>> T copy(T node) {
    return (T) node.fullCopy();
  }

  @Override
  public Program getProgram() {
    return program;
  }

  @Override
  public String reconstructText() {
    return program.getPrettyPrinted();
  }

}
