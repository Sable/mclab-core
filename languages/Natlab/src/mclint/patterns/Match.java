package mclint.patterns;

import java.util.Map;

import ast.ASTNode;
import ast.Expr;
import ast.List;
import ast.Stmt;

public class Match {
  private UnparsedPattern pattern;
  private ASTNode<?> tree;
  private Map<Character, ASTNode<?>> bindings;
  
  Match(ASTNode<?> tree, Map<Character, ASTNode<?>> bindings, UnparsedPattern pattern) {
    this.tree = tree;
    this.bindings = bindings;
    this.pattern = pattern;
  }
  
  public ASTNode<?> getMatchingNode() {
    return tree;
  }
  
  public ASTNode<?> getBoundNode(char var) {
    return bindings.get(var);
  }
  
  public <T extends ASTNode<?>> T getBoundNode(char var, Class<T> clazz) {
    return clazz.cast(getBoundNode(var));
  }
  
  public List<?> getBoundList(char var) {
    return getBoundNode(var, List.class);
  }
  
  public Stmt getBoundStmt(char var) {
    return getBoundNode(var, Stmt.class);
  }
  
  public Expr getBoundExpr(char var) {
    return getBoundNode(var, Expr.class);
  }
  
  public UnparsedPattern getPattern() {
    return pattern;
  }
  
  private String getBindingsAsString() {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<Character, ASTNode<?>> binding : bindings.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(binding.getKey());
      sb.append(": ");
      if (binding.getValue() instanceof ast.List) {
        for (int i = 0; i < binding.getValue().getNumChild(); ++i) {
          if (i != 0) {
            sb.append(", ");
          }
          sb.append(binding.getValue().getChild(i).getPrettyPrinted());
        }
      } else {
        sb.append(binding.getValue().getPrettyPrinted());
      }
    }
    sb.append("}");
    return sb.toString();
  }
  
  @Override
  public String toString() {
    return String.format("Match %s against %s\nBindings: %s", pattern.asString(),
        tree.getPrettyPrinted(), getBindingsAsString()); 
  }
}
