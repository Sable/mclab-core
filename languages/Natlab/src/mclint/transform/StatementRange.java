package mclint.transform;

import java.util.Iterator;

import ast.Function;
import ast.Stmt;

public class StatementRange implements Iterable<Stmt> {
  private Function enclosingFunction;
  private ast.List<Stmt> statements;
  private int from;
  private int to;
  
  public static StatementRange create(Function enclosingFunction,
      ast.List<Stmt> statements, int from, int to) {
    return new StatementRange(enclosingFunction, statements, from, to);
  }
  
  public static StatementRange create(Function enclosingFunction, int from, int to) {
    return create(enclosingFunction, enclosingFunction.getStmts(), from, to);
  }
  
  private StatementRange(Function enclosingFunction, ast.List<Stmt> statements,
      int from, int to) {
    this.enclosingFunction = enclosingFunction;
    this.statements = statements;
    this.from = from;
    this.to = to;
  }
  
  public Function getEnclosingFunction() {
    return enclosingFunction;
  }
  
  public ast.List<Stmt> getStatements() {
    return statements;
  }
  
  public int getStartIndex() {
    return from;
  }
  
  public int getEndIndex() {
    return to;
  }
  
  public Stmt getStartStatement() {
    return getStatements().getChild(getStartIndex());
  }
  
  public Stmt getEndStatement() {
    return getStatements().getChild(getEndIndex() - 1);
  }
  
  public int size() {
    return getEndIndex() - getStartIndex();
  }
  
  @Override public Iterator<Stmt> iterator() {
    return getStatements().stream()
        .skip(getStartIndex())
        .limit(size())
        .iterator();
  }
}