package mclint.refactoring;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import mclint.patterns.Match;
import mclint.patterns.MatchHandler;
import mclint.patterns.Matcher;
import mclint.util.Parsing;
import natlab.refactoring.AbstractNodeFunction;
import natlab.toolkits.utils.NodeFinder;
import ast.ASTNode;
import ast.ExprStmt;
import ast.NameExpr;
import ast.Script;

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

  public static void replace(ASTNode oldNode, ASTNode newNode) {
    oldNode.getParent().setChild(newNode, oldNode.getParent().getIndexOfChild(oldNode));
  }

  private static ASTNode preprocess(String pattern) {
    Pattern p = Pattern.compile("%[a-zA-Z]");
    java.util.regex.Matcher m = p.matcher(pattern);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, "INTERNAL_BINDING_" + m.group().charAt(1));
    }
    m.appendTail(sb);
    return ((Script) Parsing.string(sb.toString())).getStmtList().getChild(0);
  }

  public static Refactoring of(String fromPattern, String toPattern, Visit visit) {
    final ASTNode preprocessed = preprocess(toPattern);
    return of(fromPattern, new MatchHandler() {
      @Override
      public void handle(final Match match) {
        NodeFinder.apply(preprocessed, NameExpr.class, new AbstractNodeFunction<NameExpr>() {
          @Override
          public void apply(NameExpr node) {
            String name = node.getName().getID();
            if (name.startsWith("INTERNAL_BINDING_")) {
              ASTNode binding = match.getBoundNode(name.charAt(name.length() - 1));
              ASTNode parent = node.getParent();
              if (binding instanceof ast.List && parent instanceof ast.List) {
                int index = parent.getIndexOfChild(node);
                node.getParent().removeChild(index);
                for (Object element : (ast.List) binding) {
                  parent.insertChild((ASTNode) element, index++);
                }
              } else {
                replace(node, match.getBoundNode(name.charAt(name.length() - 1)));
              }
            }
          }
        });
        if (preprocessed instanceof ExprStmt) {
          ASTNode expr = ((ExprStmt) preprocessed).getExpr();
          if (expr instanceof List && expr.getNumChild() == 1) {
            System.out.println("got here: preprocessed expr was list");
            expr = expr.getChild(0);
          }
          replace(match.getMatchingNode(), expr);
        } else {
          replace(match.getMatchingNode(), preprocessed);
        }
      }
    }, visit);
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

  public static void main(String[] args) {
    preprocess("%x = %y");
  }
}
