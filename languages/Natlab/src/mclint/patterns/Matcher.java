package mclint.patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import mclint.util.Parsing;
import natlab.refactoring.AbstractNodeFunction;
import natlab.toolkits.utils.NodeFinder;
import ast.ASTNode;
import ast.Expr;
import ast.Program;
import ast.Stmt;

public class Matcher {
  private UnparsedPattern pattern;
  private Stack<Object> stack;
  private ASTNode tree;

  private static Match match(String pattern, ASTNode tree) {
    Stack<Object> stack = new Stack<Object>();
    stack.push(tree);
    return new Matcher(UnparsedPattern.fromString(pattern), tree, stack).match();
  }
  
  private static <T extends ASTNode> List<Match> findMatching(Class<T> clazz,
      final String pattern, ASTNode tree) {
    final List<Match> matches = new ArrayList<Match>();
    NodeFinder.apply(tree, clazz, new AbstractNodeFunction<T>() {
      @Override
      public void apply(T node) {
        Match match = match(pattern, node);
        if (match != null) {
          matches.add(match);
        }
      }
    });
    return Collections.unmodifiableList(matches);
  }
  
  public static List<Match> findMatchingStatements(String pattern, ASTNode tree) {
    return findMatching(Stmt.class, pattern, tree);
  }
  
  public static List<Match> findMatchingExpressions(String pattern, ASTNode tree) {
    return findMatching(Expr.class, pattern, tree);
  }

  private Matcher(UnparsedPattern pattern, ASTNode tree, Stack<Object> stack) {
    this.pattern = pattern;
    this.tree = tree;
    this.stack = stack;
  }

  private Match match() {
    Map<Character, ASTNode> bindings = new HashMap<Character, ASTNode>();
    while (!stack.isEmpty()) {
      if (stack.peek() instanceof String) {
        String top = (String) stack.peek();
        if (pattern.consume(top)) {
          stack.pop();
        } else {
          System.out.println("Top: " + top);
          System.out.println("Stack: " + stack);
          System.out.println("Pattern: " + pattern);
          return null;
        }
      } else if (stack.peek() instanceof ASTNode) {
        if (pattern.startsWithMeta()) {
          if (stack.size() == 1 && !pattern.emptyAfterMeta()) {
            return null;
          }
          bindings.put(pattern.popMeta(), (ASTNode) stack.pop());
        } else {
          List<Object> tokens = LazyUnparser.unparse((ASTNode) stack.pop());
          Collections.reverse(tokens);
          for (Object token : tokens) {
            stack.push(token);
          }
        }
      }
    }
    return new Match(tree, bindings, pattern);
  }
  
  public static void main(String[] args) {
    Program program = Parsing.string("disp(i)");
    List<Match> matches = findMatchingStatements("disp(%x)", program);
    System.out.println(matches.get(0).getBoundNode('x').getChild(0).getPrettyPrinted());
  }
}
