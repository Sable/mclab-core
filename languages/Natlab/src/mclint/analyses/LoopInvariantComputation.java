package mclint.analyses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import mclint.Lint;
import mclint.LintAnalysis;
import mclint.Message;
import mclint.Project;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.utils.NodeFinder;
import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.ForStmt;
import ast.LiteralExpr;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.Row;
import ast.Stmt;
import ast.WhileStmt;

import com.google.common.collect.Sets;

// TODO Nested invariants? Suggest moving expressions out of several loop levels?

public class LoopInvariantComputation extends AbstractNodeCaseHandler implements LintAnalysis {
  private static final String WARNING = "Consider computing %s outside the loop.";

  private Project project;
  private ASTNode<?> tree;
  private Lint lint;
  private ReachingDefs reachingDefs;

  private Stack<ASTNode<?>> loopStack = new Stack<ASTNode<?>>();
  private Stack<Set<Expr>> invariantStack = new Stack<Set<Expr>>();
  private Set<Expr> reported = new HashSet<>();

  public LoopInvariantComputation(Project project) {
    this.tree = project.asCompilationUnits();
  }

  private Message loopInvariant(ASTNode<?> node) {
    String message = String.format(WARNING, node.getPrettyPrinted());
    return Message.regarding(node, "LOOP_INVARIANT", message);
  }

  @Override
  public void analyze(Lint lint) {
    this.lint = lint;
    this.reachingDefs = lint.getKit().getReachingDefinitionsAnalysis();
    this.tree.analyze(this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void caseASTNode(ASTNode node) {
    for (int i = 0; i < node.getNumChild(); ++i)
      node.getChild(i).analyze(this);
  }

  private boolean allDefsOutsideLoop(Set<Def> defs, ASTNode<?> loop) {
    Set<Def> loopDefs = NodeFinder.find(Def.class, loop).collect(Collectors.toSet());
    return NodeFinder.find(Def.class, loop)
        .filter(def -> def instanceof AssignStmt)
        .filter(def -> def != ReachingDefs.UNDEF)
        .allMatch(def -> !loopDefs.contains(def));
  }

  private void caseLoopStmt(ASTNode<?> node) {
    loopStack.push(node);
    invariantStack.push(Sets.<Expr>newHashSet());
    Set<Expr> oldInvariants;
    do {
      oldInvariants = new HashSet<>(invariantStack.peek());
      if (node instanceof ForStmt)
        caseASTNode(((ForStmt)node).getStmts());
      else
        caseASTNode(((WhileStmt)node).getStmts());
    } while (!(invariantStack.peek().equals(oldInvariants)));
    for (Expr invariant: invariantStack.peek()) {
      if (invariant.getNumChild() != 0 && !(invariant instanceof NameExpr)) {
        if (!reported.contains(invariant)) {
          lint.report(loopInvariant(invariant));
          reported.add(invariant);
        }
      }
    }
    invariantStack.pop();
    loopStack.pop();
  }

  @Override
  public void caseForStmt(ForStmt node) {
    caseLoopStmt(node);
  }

  @Override
  public void caseWhileStmt(WhileStmt node) {
    caseLoopStmt(node);
  }

  @Override
  public void caseAssignStmt(AssignStmt node) {
    node.getRHS().analyze(this);
  }

  private static List<Expr> getChildren(Expr node) {
    List<Expr> exprs = NodeFinder.find(Expr.class, node).collect(Collectors.toList());
    exprs.remove(node);
    return exprs;
  }

  private boolean isInvariant(Expr expr) {
    if (expr instanceof LiteralExpr)
      return true;
    if (expr instanceof MatrixExpr)
      for (Row row : ((MatrixExpr)expr).getRows())
        for (Expr cell : row.getElements())
          if (!isInvariant(cell))
            return false;
    if (expr instanceof NameExpr)
      return isInvariant(((NameExpr)expr).getName().getID());
    return false;
  }

  private boolean isInvariant(String name) {
    for (Set<Expr> set : invariantStack)
      for (Expr expr : set)
        if (expr instanceof NameExpr && ((NameExpr)expr).getName().getID().equals(name))
          return true;
    return false;
  }

  @Override
  public void caseExpr(Expr node) {
    if (loopStack.isEmpty())
      return;
    if (node.getNumChild() == 0 || node instanceof NameExpr) {
      if (isInvariant(node)) {
        invariantStack.peek().add(node);
      }
      else if (node instanceof NameExpr) {
        String name = ((NameExpr)node).getName().getID();
        Stmt parentStmt = NodeFinder.findParent(Stmt.class, node);
        if (reachingDefs.getInFlowSets().containsKey(parentStmt)) {
          Set<Def> inSet = reachingDefs.getInFlowSets().get(parentStmt).get(name);
          if (allDefsOutsideLoop(inSet, loopStack.peek()))
            invariantStack.peek().add(node);
        }
      }
    } else {
      caseASTNode(node);
      List<Expr> children = getChildren(node);

      if (invariantStack.peek().containsAll(children)) {
        invariantStack.peek().removeAll(children);
        invariantStack.peek().add(node);
      }
    }
  }
}
