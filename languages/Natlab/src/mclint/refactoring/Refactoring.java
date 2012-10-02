package mclint.refactoring;

import java.util.Collections;
import java.util.List;

import mclint.patterns.Match;
import mclint.patterns.MatchHandler;
import mclint.patterns.Matcher;
import ast.ASTNode;

public class Refactoring {
  private String pattern;
  private MatchHandler handler;
  private Visit visit;
  
  public static enum Visit {
    Statements {
      public List<Match> getMatches(String pattern, ASTNode tree) {
        return Matcher.findMatchingStatements(pattern, tree);
      }
    },
    Expressions {
      public List<Match> getMatches(String pattern, ASTNode tree) {
        return Matcher.findMatchingExpressions(pattern, tree);
      }
    },
    All {
      public List<Match> getMatches(String pattern, ASTNode tree) {
        return Matcher.findAllMatches(pattern, tree);
      }
    };
    
    public List<Match> getMatches(String pattern, ASTNode tree) {
      return Collections.emptyList();
    }
  }
  
  public static Refactoring of(String pattern, MatchHandler handler) {
    return of(pattern, handler, Visit.All);
  }
  
  public static Refactoring of(String pattern, MatchHandler handler, Visit visit) {
    return new Refactoring(pattern, handler, visit);
  }
  
  public void apply(ASTNode tree) {
    for (Match match : visit.getMatches(pattern, tree)) {
      handler.handle(match);
    }
  }

  private Refactoring(String pattern, MatchHandler handler, Visit visit) {
    this.pattern = pattern;
    this.handler = handler;
    this.visit = visit;
  }
}
