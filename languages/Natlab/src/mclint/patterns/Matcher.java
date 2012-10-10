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
import ast.ForStmt;
import ast.Program;
import ast.Stmt;

public class Matcher {
  private static final boolean DEBUG = false;
  private UnparsedPattern pattern;
  private Stack<Object> stack;
  private ASTNode tree;
  private Map<Character, ASTNode> bindings = new HashMap<Character, ASTNode>();

  private static Match match(String pattern, ASTNode tree) {
    Stack<Object> stack = new Stack<Object>();
    stack.push(tree);
    Matcher matcher = new Matcher(UnparsedPattern.fromString(pattern), tree, stack);
    Match match = matcher.match();
    if (match == null && DEBUG) {
      matcher.dumpState();
    }
    return match;
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

  public static List<Match> findAllMatches(String pattern, ASTNode tree) {
    List<Match> matches = new ArrayList<Match>();
    matches.addAll(findMatchingStatements(pattern, tree));
    matches.addAll(findMatchingExpressions(pattern, tree));
    return Collections.unmodifiableList(matches);
  }

  private Matcher(UnparsedPattern pattern, ASTNode tree, Stack<Object> stack) {
    this.pattern = pattern;
    this.tree = tree;
    this.stack = stack;
  }

  private Object lookahead() {
    return stack.get(stack.size() - 2);
  }

  private void dumpState() {
    System.err.println("Stack: " + stack);
    System.err.println("Pattern: " + pattern);
    System.err.println("Bindings: " + bindings);
  }

  private Match match() {
    while (!stack.isEmpty()) {
      if (stack.peek() instanceof String) {
        String top = (String) stack.peek();
        if (pattern.consume(top)) {
          stack.pop();
        } else {
          return null;
        }
      } else if (stack.peek() instanceof ASTNode) {
        // Resolve bind/unparse conflict
        // Lookahead; if we bind, will we fail?
        if (pattern.startsWithMeta()) {
          UnparsedPattern afterMeta = pattern.afterMeta();
          if (stack.size() == 1) {
            if (!pattern.emptyAfterMeta()) {
              unparse();
            }
          } else if (lookahead() instanceof ASTNode && !afterMeta.startsWithMeta()
                  || lookahead() instanceof String && !afterMeta.consume((String) lookahead())) {
            unparse();
          } else {
            if (!bind()) {
              return null;
            }
          }
        } else {
          unparse();
        }
      }
    }
    if (!pattern.finished()) {
      return null;
    }
    return new Match(tree, bindings, pattern);
  }

  private boolean bind() {
    char meta = pattern.popMeta();
    ASTNode tree = (ASTNode) stack.pop();
    if (meta == '_') {
      return true;
    }
    if (bindings.containsKey(meta) && !EqualityChecker.equals(tree, bindings.get(meta))) {
      return false;
    }
    bindings.put(meta, tree);
    return true;
  }

  private void unparse() {
    List<Object> tokens = LazyUnparser.unparse((ASTNode) stack.pop());
    Collections.reverse(tokens);
    for (Object token : tokens) {
      stack.push(token);
    }
  }

  public static void main(String[] args) {
    String pattern = new StringBuilder()
      .append("for %i=(%l:%r)\n")
      .append("  %a(%i) = %_;\n")
      .append("end").toString();
    Program program = Parsing.string(new StringBuilder()
      .append("for i = (1:10) \n")
      .append("  a(i) = 5; \n")
      .append("end \n").toString());
    List<Match> matches = findMatching(ForStmt.class, pattern, program);
    System.out.println(matches);
  }
}
