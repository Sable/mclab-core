package mclint.patterns;

import java.util.Map;

import ast.ASTNode;

public class Match {
  private UnparsedPattern pattern;
  private ASTNode tree;
  private Map<Character, ASTNode> bindings;
  
  Match(ASTNode tree, Map<Character, ASTNode> bindings, UnparsedPattern pattern) {
    this.tree = tree;
    this.bindings = bindings;
    this.pattern = pattern;
  }
  
  public ASTNode getMatchingNode() {
    return tree;
  }
  
  public ASTNode getBoundNode(char var) {
    return bindings.get(var);
  }
  
  public UnparsedPattern getPattern() {
    return pattern;
  }
  
  private String getBindingsAsString() {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<Character, ASTNode> binding : bindings.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(binding.getKey());
      sb.append(": ");
      sb.append(binding.getValue().getPrettyPrinted());
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
