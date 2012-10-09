package mclint.refactoring;

import static mclint.util.ASTBuilders.call;
import static mclint.util.ASTBuilders.not;
import mclint.patterns.Match;
import mclint.patterns.MatchHandler;
import ast.ASTNode;

public class Refactorings {
  private static void replace(ASTNode oldNode, ASTNode newNode) {
    oldNode.getParent().setChild(newNode, oldNode.getParent().getIndexOfChild(oldNode));
  }

  public static Refactoring repmatToZeros() {
    return Refactoring.of("repmat(0, %x)", new MatchHandler() {
      @Override
      public void handle(Match match) {
        replace(match.getMatchingNode(), call("zeros", match.getBoundList('x')));
      }
    }, Refactoring.Visit.Expressions);
  }

  public static Refactoring repmatToOnes() {
    return Refactoring.of("repmat(1, %x)", new MatchHandler() {
      @Override
      public void handle(Match match) {
        replace(match.getMatchingNode(), call("ones", match.getBoundList('x')));
      }
    }, Refactoring.Visit.Expressions);
  }

  public static Refactoring dispSprintfToFprintf() {
    return Refactoring.of("disp(sprintf(%x))", new MatchHandler() {
      @Override
      public void handle(Match match) {
        replace(match.getMatchingNode(), call("fprintf", match.getBoundList('x')));
      }
    }, Refactoring.Visit.Expressions);
  }
  
  public static Refactoring lengthEqZeroToIsempty() {
    return Refactoring.of("(length(%x) == 0)", new MatchHandler() {
      @Override
      public void handle(Match match) {
        replace(match.getMatchingNode(), call("isempty", match.getBoundList('x')));
      }
    }, Refactoring.Visit.Expressions);
  }

  public static Refactoring lengthNeqZeroToNotIsempty() {
    return Refactoring.of("(length(%x) ~= 0)", new MatchHandler() {
      @Override
      public void handle(Match match) {
        replace(match.getMatchingNode(), not(call("isempty", match.getBoundList('x'))));
      }
    }, Refactoring.Visit.Expressions);
  }
}
