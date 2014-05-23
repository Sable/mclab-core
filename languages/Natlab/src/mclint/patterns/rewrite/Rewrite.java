package mclint.patterns.rewrite;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import mclint.patterns.Match;
import mclint.patterns.Matcher;
import mclint.util.AstUtil;
import ast.ASTNode;
import ast.ExprStmt;

public class Rewrite {
  private String pattern;
  private Consumer<Match> handler;
  private Visit visit;

  public static enum Visit {
    Statements {
      public List<Match> getMatches(String pattern, ASTNode<?> tree) {
        return Matcher.findMatchingStatements(pattern, tree);
      }
    },
    Expressions {
      public List<Match> getMatches(String pattern, ASTNode<?> tree) {
        return Matcher.findMatchingExpressions(pattern, tree);
      }
    },
    All {
      public List<Match> getMatches(String pattern, ASTNode<?> tree) {
        return Matcher.findAllMatches(pattern, tree);
      }
    };

    public List<Match> getMatches(String pattern, ASTNode<?> tree) {
      return Collections.emptyList();
    }
  }

  public static Rewrite of(String fromPattern, String toPattern, final Visit visit) {
    final TreeWithPlaceholders preprocessed = TreeWithPlaceholders.fromPattern(toPattern);
    return of(fromPattern, match -> {
      preprocessed.accept(match);
      ASTNode<?> tree = preprocessed.getTree();
      if (tree instanceof ExprStmt && visit == Visit.Expressions) {
        tree = ((ExprStmt) tree).getExpr();
      }
      AstUtil.replace(match.getMatchingNode(), tree);
    }, visit);
  }

  public static Rewrite of(String pattern, Consumer<Match> handler) {
    return of(pattern, handler, Visit.All);
  }

  public static Rewrite of(String pattern, Consumer<Match> handler, Visit visit) {
    return new Rewrite(pattern, handler, visit);
  }

  public void apply(ASTNode<?> tree) {
    visit.getMatches(pattern, tree).forEach(handler);
  }

  private Rewrite(String pattern, Consumer<Match> handler, Visit visit) {
    this.pattern = pattern;
    this.handler = handler;
    this.visit = visit;
  }
}
