package mclint.patterns;

import java.util.List;
import java.util.Map;

import ast.ASTNode;
import ast.Expr;
import ast.Stmt;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
  
  public ast.List<?> getBoundList(char var) {
    return getBoundNode(var, ast.List.class);
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
    return String.format("{%s}", Joiner.on(", ").withKeyValueSeparator(": ")
        .join(Maps.transformValues(bindings, new Function<ASTNode<?>, String>() {
          @Override public String apply(ASTNode<?> node) {
            if (!(node instanceof ast.List)) {
              return node.getPrettyPrinted();
            }
            List<String> parts = Lists.newArrayList();
            for (ASTNode<?> child : node) {
              parts.add(child.getPrettyPrinted());
            }
            return Joiner.on(", ").join(parts);
          }
        })));
  }

  @Override
  public String toString() {
    return String.format("Match %s against %s\nBindings: %s", pattern.asString(),
        tree.getPrettyPrinted(), getBindingsAsString()); 
  }
}
