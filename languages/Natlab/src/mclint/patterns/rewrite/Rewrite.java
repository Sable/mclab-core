package mclint.patterns.rewrite;

import java.util.Collections;
import java.util.List;

import mclint.patterns.Match;
import mclint.patterns.MatchHandler;
import mclint.patterns.Matcher;
import mclint.util.AstUtil;
import ast.ASTNode;
import ast.ExprStmt;

public class Rewrite {
  private String pattern;
  private MatchHandler handler;
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
    return of(fromPattern, new MatchHandler() {
      public void handle(final Match match) {
        preprocessed.handle(match);
        ASTNode<?> tree = preprocessed.getTree();
        if (tree instanceof ExprStmt && visit == Visit.Expressions) {
          tree = ((ExprStmt) tree).getExpr();
        }
        AstUtil.replace(match.getMatchingNode(), tree);
      }
    }, visit);
  }

  public static Rewrite of(String pattern, MatchHandler handler) {
    return of(pattern, handler, Visit.All);
  }

  public static Rewrite of(String pattern, MatchHandler handler, Visit visit) {
    return new Rewrite(pattern, handler, visit);
  }

  public void apply(ASTNode<?> tree) {
    for (Match match : visit.getMatches(pattern, tree)) {
      handler.handle(match);
    }
  }

  private Rewrite(String pattern, MatchHandler handler, Visit visit) {
    this.pattern = pattern;
    this.handler = handler;
    this.visit = visit;
  }
}
