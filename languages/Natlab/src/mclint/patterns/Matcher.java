package mclint.patterns;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import mclint.util.Parsing;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.Expr;
import ast.ForStmt;
import ast.Program;
import ast.Stmt;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class Matcher {
  private UnparsedPattern pattern;
  private Stack<Object> stack;
  private ASTNode<?> tree;
  private Map<Character, ASTNode<?>> bindings = Maps.newHashMap();

  private static Optional<Match> match(String pattern, ASTNode<?> tree) {
    Stack<Object> stack = new Stack<Object>();
    stack.push(tree);
    return new Matcher(UnparsedPattern.fromString(pattern), tree, stack).match();
  }

  private static <T extends ASTNode<?>> Function<T, Optional<Match>> matchFunction(final String p) {
    return new Function<T, Optional<Match>>() {
      @Override public Optional<Match> apply(T node) {
        return match(p, node);
      }
    };
  }

  private static <T extends ASTNode<?>> List<Match> findMatching(Class<T> clazz, String pattern,
      ASTNode<?> tree) {
    return ImmutableList.copyOf(Optional.presentInstances(NodeFinder.find(clazz, tree)
        .transform(matchFunction(pattern))));
  }

  public static List<Match> findMatchingStatements(String pattern, ASTNode<?> tree) {
    return findMatching(Stmt.class, pattern, tree);
  }

  public static List<Match> findMatchingExpressions(String pattern, ASTNode<?> tree) {
    return findMatching(Expr.class, pattern, tree);
  }

  public static List<Match> findAllMatches(String pattern, ASTNode<?> tree) {
    return ImmutableList.<Match>builder()
        .addAll(findMatchingStatements(pattern, tree))
        .addAll(findMatchingExpressions(pattern, tree))
        .build();
  }

  private Matcher(UnparsedPattern pattern, ASTNode<?> tree, Stack<Object> stack) {
    this.pattern = pattern;
    this.tree = tree;
    this.stack = stack;
  }

  private String lookahead() {
    Object lookahead = stack.get(stack.size() - 2);
    if (lookahead instanceof String) {
      return (String) lookahead;
    }
    return LazyUnparser.lookahead((ASTNode<?>) lookahead);
  }

  private boolean shouldUnparse() {
    return !pattern.startsWithMeta()
        || stack.size() == 1 && !pattern.emptyAfterMeta()
        || stack.size() == 1
        || !pattern.afterMeta().consume(lookahead());
  }

  private Optional<Match> match() {
    while (!stack.isEmpty()) {
      if (stack.peek() instanceof String) {
        String top = (String) stack.peek();
        if (!pattern.consume(top)) {
          return Optional.absent();
        }
        stack.pop();
      } else if (shouldUnparse()) {
        unparse();
      } else if (!bind()) {
        return Optional.absent();
      }
    }
    return Optional.fromNullable(pattern.finished() ? new Match(tree, bindings, pattern) : null);
  }

  private boolean bind() {
    char meta = pattern.popMeta();
    ASTNode<?> tree = (ASTNode<?>) stack.pop();
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
    List<Object> tokens = LazyUnparser.unparse((ASTNode<?>) stack.pop());
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
