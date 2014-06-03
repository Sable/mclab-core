package natlab.refactoring;


import ast.ASTNode;
import ast.AssignStmt;
import ast.CompilationUnits;
import ast.Expr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Stmt;
import mclint.refactoring.Refactoring;
import mclint.refactoring.RefactoringContext;
import natlab.refactoring.Exceptions.IDConflictException;
import natlab.refactoring.Exceptions.NameResolutionChangeException;
import natlab.refactoring.Exceptions.RefactorException;
import natlab.refactoring.Exceptions.RenameRequired;
import natlab.refactoring.Exceptions.TargetNotAFunction;
import natlab.refactoring.Exceptions.TargetNotFoundException;
import natlab.refactoring.Exceptions.TooManyInputParams;
import natlab.refactoring.Exceptions.TooManyOutputParams;
import natlab.toolkits.ContextStack;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.core.CopyAnalysis;
import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.rewrite.simplification.RightSimplification;
import natlab.utils.NodeFinder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FunctionInliner extends Refactoring {
  private CompilationUnits cu;
  private ast.Function f;

  final ParsedCompilationUnitsContextStack lookupContext;
  public FunctionInliner(RefactoringContext context, ast.Function f) {
    super(context);
    this.f = f;
    this.cu = context.getProject().asCompilationUnits();
    this.lookupContext = new ParsedCompilationUnitsContextStack(new LinkedList<>(), cu.getRootFolder(), cu);
  };

  @Override public boolean checkPreconditions() {
    return true;
  }

  @Override public void apply() {
    // TODO(isbadawi): If this requires a simplification then we're in trouble.
    // TODO(isbadawi): Maybe look into making simplifications layout preserving?
    RightSimplification rs = new RightSimplification(f, new VFPreorderAnalysis(f));
    rs.transform();
    // TODO(isbadawi): This was made to inline all the possible function calls in a function.
    // TODO(isbadawi): But I want to inline all the call sites of a function.
    // TODO(isbadawi): Need to integrate this with my callgraph instrumentation.
    addErrors(NodeFinder.find(AssignStmt.class, f.getStmts())
        .filter(stmt -> stmt.getRHS() instanceof ParameterizedExpr)
        .flatMap(stmt -> inline(stmt).stream())
        .collect(Collectors.toList()));
  }

  private List<RefactorException> inline(AssignStmt s) {
    List<RefactorException> errors = new LinkedList<>();
    ast.Function f = NodeFinder.findParent(ast.Function.class, s);
    lookupContext.push(f);
    VFFlowInsensitiveAnalysis kind_analysis_caller =
        new VFFlowInsensitiveAnalysis(f, lookupContext.peek().getFunctionOrScriptQuery());
    kind_analysis_caller.analyze();
    Map<String, VFDatum> kind_caller_results = kind_analysis_caller.getFlowSets().get(f);

    ParameterizedExpr rhs = (ParameterizedExpr) s.getRHS();
    if (!(rhs.getChild(0) instanceof NameExpr)) {
      errors.add(new TargetNotFoundException(rhs.getTarget().getNameExpressions().iterator().next().getName()));
      return errors;
    }
    NameExpr ne = (NameExpr) rhs.getChild(0);

    VFDatum neKind = kind_caller_results.getOrDefault(ne.getName().getID(), VFDatum.UNDEF);
    if (!neKind.isFunction()) {
      errors.add(new TargetNotAFunction(ne.getName(), f, neKind.toString()));
      return errors;
    }

    FunctionReference lookupReference = lookupContext.peek().resolve(ne.getName().getID());
    ASTNode lookupNode = lookupContext.resolveFunctionReference(lookupReference);
    if (lookupContext.peek().getAllOverloads(ne.getName().getID()).size() >= 2 || !(lookupNode instanceof ast.Function)) {
      errors.add(new TargetNotFoundException(ne.getName()));
      return errors;
    }
    ast.Function target = (ast.Function) lookupNode;

    if (target.getNumInputParam() < rhs.getNumArg()) {
      int inputs = target.getNumInputParam();
      // TODO(isbadawi): I don't understand what this error message is supposed to say.
      errors.add(new TooManyInputParams(new Name(rhs.getArg(inputs).getPrettyPrinted()), null,
          target.getInputParam(inputs - 1).getID() + " " + (rhs.getNumArg() - inputs)));
      return errors;
    }

    ContextStack calleeContext = new ContextStack(new LinkedList<>(), lookupReference.path.getParent());
    calleeContext.push(target);
    VFFlowInsensitiveAnalysis kind_analysis_callee = new VFFlowInsensitiveAnalysis(target);
    kind_analysis_callee.analyze();

    ASTNode<?> stmtList = s.getParent();
    int replacementIndex = stmtList.getIndexOfChild(s);
    stmtList.removeChild(replacementIndex);

    List<Stmt> newStmtList = new LinkedList<>();
    List<AssignStmt> syntheticStmts = new LinkedList<>();

    try {
      List<AssignStmt> preamble = getPreamble(rhs, target);
      List<AssignStmt> postamble = getPostamble(s, ne, target);

      newStmtList.addAll(preamble);
      target.getStmts().forEach(newStmtList::add);
      newStmtList.addAll(postamble);

      syntheticStmts.addAll(preamble);
      syntheticStmts.addAll(postamble);
    } catch (RefactorException e) {
      errors.add(e);
      return errors;
    }

    for(int j = newStmtList.size() - 1; j >= 0; j--){
      stmtList.insertChild(newStmtList.get(j), replacementIndex);
    }

    VFFlowInsensitiveAnalysis kind_analysis_post = new VFFlowInsensitiveAnalysis(f);
    kind_analysis_post.analyze();
    NodeFinder.find(NameExpr.class, target.getStmts()).forEach(nameExpr -> {
      Name n = nameExpr.getName();
      VFDatum kindCallee = kind_analysis_callee.getFlowSets()
          .getOrDefault(n, kind_analysis_callee.getFlowSets().get(target))
          .getOrDefault(n.getID(), VFDatum.UNDEF);
      VFDatum kindCaller = kind_caller_results.getOrDefault(n.getID(), VFDatum.UNDEF);
      VFDatum kindPost = kind_analysis_post.getFlowSets().get(f).getOrDefault(n.getID(), VFDatum.UNDEF);

      try {
        checkKindsBeforeAndAfter(calleeContext, n, kindCallee, kindCaller, kindPost);
      } catch (RefactorException e) {
        errors.add(e);
      }
    });

    if (errors.isEmpty()) {
      syntheticStmts.stream()
          .filter(def -> def.getLHS() instanceof NameExpr)
          .filter(def -> def.getRHS() instanceof NameExpr)
          .forEach(def -> removeSuperfluousCopies(f, def));
    }
    lookupContext.pop();
    return errors;
  }

  private Collection<NameExpr> getUses(AssignStmt s, Collection<NameExpr> all, ReachingDefs defAnalysis) {
    List<NameExpr> uses = new LinkedList<>();
    for (NameExpr n : all) {
      Stmt aUse = NodeFinder.findParent(Stmt.class, n);
      if (aUse != s && defAnalysis.getOutFlowSets()
          .getOrDefault(aUse, Collections.emptyMap())
          .getOrDefault(n.getName().getID(), Collections.emptySet())
          .contains(s)) {
        uses.add(n);
      }
    }
    return uses;
  }

  private void removeSuperfluousCopies(ast.Function f, AssignStmt s) {
    String left = ((NameExpr) s.getLHS()).getName().getID();
    String right = ((NameExpr) s.getRHS()).getName().getID();
    LivenessAnalysis l = new LivenessAnalysis(f);
    ReachingDefs defs = new ReachingDefs(f);
    CopyAnalysis copies = new CopyAnalysis(f);
    List<NameExpr> exprs = NodeFinder.find(NameExpr.class, f.getStmts()).collect(Collectors.toList());
    l.analyze();
    defs.analyze();
    copies.analyze();

    Collection<NameExpr> uses = getUses(s, exprs, defs);
    if (uses.stream()
        .map(use -> NodeFinder.findParent(Stmt.class, use))
        .allMatch(stmt -> copies.getOutFlowSets().get(stmt).get(left) == s)) {
      s.getParent().removeChild(s.getParent().getIndexOfChild(s));
      uses.forEach(use -> use.getName().setID(right));
    }
  }


  private List<AssignStmt> getPostamble(AssignStmt s, NameExpr ne, ast.Function target) {
    List<AssignStmt> postamble = new LinkedList<>();
    if (s.getLHS() instanceof MatrixExpr) {
      MatrixExpr outputArgs = (MatrixExpr) s.getLHS();
      int provided = outputArgs.getRow(0).getNumElement();
      int expected = target.getNumOutputParam();
      if (provided > expected || target.getOutputParam(expected - 1).getID().equals("varargout")) {
        throw new TooManyOutputParams(ne.getName(), null, "");
      }
      for (int i = 0; i < provided; i++){
        postamble.add(new AssignStmt(
            (Expr) outputArgs.getRow(0).getElement(i).fullCopy(),
            new NameExpr(target.getOutputParam(i))));
      }
    } else if (target.getNumOutputParam() > 0) {
      postamble.add(new AssignStmt((Expr)(s.getLHS().copy()), new NameExpr(target.getOutputParam(0))));
    }
    return postamble;
  }

  private List<AssignStmt> getPreamble(ParameterizedExpr rhs, ast.Function target) {
    List<AssignStmt> preamble = new LinkedList<>();
    for (int i = 0; i < rhs.getNumArg(); i++){
      preamble.add(new AssignStmt(new NameExpr(target.getInputParam(i)), rhs.getArg(i)));
    }
    return preamble;
  }

  private void checkKindsBeforeAndAfter(ContextStack calleeContext, Name n, VFDatum kindCallee, VFDatum kindCaller, VFDatum kindPost) {
    if (kindCallee != VFDatum.UNDEF && kindCaller != VFDatum.UNDEF && kindCallee.merge(kindCaller) == VFDatum.TOP ||
        kindCallee.isVariable() && kindCaller.isVariable() ||
        kindCallee.isID() && kindCaller.isVariable() ||
        kindCaller.isID() && kindCallee.isVariable()) {
      throw new RenameRequired(n);
    }

    if (kindCallee.isFunction() && (kindCaller.isFunction() || kindCaller == VFDatum.UNDEF)) {
      FunctionReference resolved = calleeContext.peek().resolve(n.getID());
      if (resolved != null && !resolved.equals(lookupContext.peek().resolve(n.getID()))) {
        throw new NameResolutionChangeException(n);
      }
    }

    if (kindCallee.isID() && kindPost.isFunction() ||
        kindCallee.isFunction() && kindCaller.isID()) {
      throw new IDConflictException(n);
    }
  }
}
