package natlab.refactoring;

import java.util.Map;
import java.util.Set;

import mclint.refactoring.Refactoring;
import mclint.refactoring.RefactoringContext;
import mclint.transform.Transformer;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.utils.NodeFinder;
import analysis.Analysis;
import ast.AssignStmt;
import ast.ExprStmt;
import ast.Function;
import ast.FunctionList;
import ast.GlobalStmt;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Row;
import ast.Stmt;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class ExtractFunction extends Refactoring {
  private Function original;
  private int from;
  private int to;
  private String extractedFunctionName;
  private Transformer transformer;

  private Function extracted;

  private Map<String, Set<Def>> reachingBefore;
  private Map<String, Set<Def>> reachingAfter;
  private Set<String> liveBefore;
  private Set<String> liveAfter;
  private Map<String, VFDatum> kinds;

  private Set<String> addedGlobals = Sets.newHashSet();
  private Set<String> addedInputs = Sets.newHashSet();

  public ExtractFunction(RefactoringContext context,
      Function original, int from, int to, String extractedFunctionName) {
    super(context);
    this.original = original;
    this.from = from;
    this.to = to;
    this.extractedFunctionName = extractedFunctionName;

    this.transformer = context.getTransformer();
  }

  private void extractStatements() {
    extracted = new Function();
    extracted.setName(extractedFunctionName);
    for (int i = from; i < to; i++) {
      extracted.addStmt((Stmt) original.getStmt(i).fullCopy());
    }
  }

  private <T extends Analysis> T analyze(T analysis) {
    analysis.analyze();
    return analysis;
  }
  private void analyzeBeforeAndAfter() {
    ReachingDefs reachingAnalysisOrig = analyze(new ReachingDefs(original));
    ReachingDefs reachingAnalysisNew = analyze(new ReachingDefs(extracted));
    LivenessAnalysis liveAnalysisOrig = analyze(new LivenessAnalysis(original));
    LivenessAnalysis liveAnalysisNew = analyze(new LivenessAnalysis(extracted));
    VFPreorderAnalysis kindAnalysis = analyze(new VFPreorderAnalysis(original));

    Stmt startStmt = original.getStmt(from);
    Stmt endStmt = original.getStmt(to - 1);

    reachingBefore = reachingAnalysisOrig.getOutFlowSets().get(startStmt);
    reachingAfter = reachingAnalysisNew.getOutFlowSets().get(extracted);
    liveBefore = liveAnalysisNew.getInFlowSets().get(extracted);
    liveAfter = liveAnalysisOrig.getOutFlowSets().get(endStmt);
    kinds = kindAnalysis.getFlowSets().get(original);
  }

  private Iterable<String> liveVariablesAtInputOfNewFunction() {
    return Iterables.filter(liveBefore, new Predicate<String>() {
      @Override public boolean apply(String id) {
        return (kinds.containsKey(id) ? kinds.get(id) : VFDatum.UNDEF).isVariable();
      }
    });
  }

  private Iterable<String> liveVariablesDefinedInNewFunction() {
    return Iterables.filter(liveAfter, new Predicate<String>() {
      @Override public boolean apply(String id) {
        return (kinds.containsKey(id) ? kinds.get(id) : VFDatum.UNDEF).isVariable() &&
            reachingAfter.containsKey(id);
      }
    });
  }

  private boolean containsGlobalStmt(Set<Def> defs) {
    return Iterables.any(defs, Predicates.instanceOf(GlobalStmt.class));
  }

  private boolean originallyGlobal(String id) {
    return containsGlobalStmt(reachingBefore.get(id));
  }

  private boolean globalInNewFunction(String id) {
    return containsGlobalStmt(reachingAfter.get(id));
  }

  private void makeGlobal(String id) {
    if (addedGlobals.contains(id)) {
      return;
    }
    GlobalStmt globalDecl =
        new GlobalStmt(new ast.List<Name>().add(new Name(id)));
    extracted.getStmts().insertChild(globalDecl, 0);
    addedGlobals.add(id);
  }

  private boolean originallyDefined(String id) {
    return !(reachingBefore.get(id).isEmpty() ||
        reachingBefore.get(id).contains(ReachingDefs.UNDEF));
  }

  private void makeInputParameter(String id) {
    if (addedInputs.contains(id)) {
      return;
    }
    extracted.addInputParam(new Name(id));
    addedInputs.add(id);
  }

  private void makeOutputParameter(String id) {
    extracted.addOutputParam(new Name(id));
  }

  private void reportVariableMightBeUndefined(String id) {
    addError(new Exceptions.FunctionInputCanBeUndefined(new Name(id)));
  }

  private void reportOutputMightBeUndefined(String id) {
    addError(new Exceptions.FunctionOutputCanBeUndefined(new Name(id)));
  }

  private void makeExtractedFunctionSiblingOfOriginal() {
    ast.List<Function> list = 
        NodeFinder.findParent(FunctionList.class, original).getFunctions();
    ast.List<Stmt> stmts = extracted.getStmts().fullCopy();
    while (extracted.getNumStmt() != 0) {
      extracted.getStmts().removeChild(0);
    }
    
    transformer.insert(list, extracted, list.getIndexOfChild(original) + 1);
    for (Stmt stmt : stmts) {
      transformer.insert(extracted.getStmts(), stmt, extracted.getNumStmt());
    }
  }

  private void replaceExtractedStatementsWithCallToExtractedFunction() {
    Stmt call = makeCallToExtractedFunction();
    for (int i = from; i < to; i++) {
      transformer.remove(original.getStmt(from));
    }
    transformer.insert(original.getStmts(), call, from);
  }

  private Stmt makeCallToExtractedFunction() {
    ParameterizedExpr call = new ParameterizedExpr();
    call.setTarget(new NameExpr(new Name(extracted.getName())));
    for (Name input : extracted.getInputParams()) {
      call.getArgs().add(new NameExpr(new Name(input.getID())));
    }
    if (extracted.getNumOutputParam() == 0) {
      return new ExprStmt(call);
    }
    AssignStmt assign = new AssignStmt();
    assign.setRHS(call);
    if (extracted.getNumOutputParam() == 1) {
      assign.setLHS(new NameExpr(new Name(extracted.getOutputParam(0).getID())));
    } else {
      Row row = new Row();
      for (Name name : extracted.getOutputParams()) {
        row.addElement(new NameExpr(new Name(name.getID())));
      }
      MatrixExpr lhs = new MatrixExpr();
      lhs.addRow(row);
      assign.setLHS(lhs);
    }
    return assign;
  }

  public Function getExtractedFunction() {
    return extracted;
  }

  @Override
  public void apply() {
    extractStatements();
    analyzeBeforeAndAfter();

    for (String id : liveVariablesAtInputOfNewFunction()) {
      if (originallyGlobal(id)) {
        makeGlobal(id);
      } else if (originallyDefined(id)) {
        makeInputParameter(id);
      } else {
        reportVariableMightBeUndefined(id);
      }
    }

    for (String id : liveVariablesDefinedInNewFunction()) {
      if (globalInNewFunction(id)) {
        continue;
      }
      if (reachingAfter.get(id).contains(ReachingDefs.UNDEF)) {
        makeOutputParameter(id);
        makeInputParameter(id);
        reportOutputMightBeUndefined(id);
      } else {
        makeOutputParameter(id);
      }
    }

    // TODO this doesn't quite match what's in the thesis; more checks related
    // to preservation of kinds are described.

    makeExtractedFunctionSiblingOfOriginal();
    replaceExtractedStatementsWithCallToExtractedFunction();
  }

  @Override
  public boolean checkPreconditions() {
    // TODO figure out real preconditions
    return true;
  }
}