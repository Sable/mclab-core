package natlab.refactoring;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mclint.refactoring.Refactoring;
import mclint.refactoring.RefactoringContext;
import mclint.transform.Transformer;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowSensitiveAnalysis;
import natlab.toolkits.filehandling.GenericFile;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.CompilationUnits;
import ast.ExprStmt;
import ast.FunctionHandleExpr;
import ast.FunctionList;
import ast.Name;
import ast.NameExpr;
import ast.Script;

import com.google.common.collect.Lists;

public class MScriptInliner extends Refactoring {
  private CompilationUnits cu;
  private Script target;

  public MScriptInliner(RefactoringContext context, Script script) {
    super(context);
    this.cu = context.getProject().asCompilationUnits();
    this.target = script;
  };

  @Override
  public boolean checkPreconditions() {
    // TODO(isbadawi): What goes here?
    return true;
  }

  @Override
  public void apply() {
    context.getProject().getMatlabPrograms().stream()
        .filter(program -> program.parse() instanceof FunctionList)
        .flatMap(program -> ((FunctionList) program.parse()).getFunctions().stream())
        .forEach(this::inlineCallsToScript);
  }

  private static Set<String> findNested(ast.Function f) {
    if (f.getParent().getParent() instanceof ast.Function) {
      f = (ast.Function) f.getParent().getParent();
    }
    return NodeFinder.find(ast.Function.class, f)
        .map(ast.Function::getName)
        .collect(Collectors.toSet());
  }

  private static Set<Name> findNameExpr(ASTNode f) {
    return NodeFinder.find(Name.class, f)
        .filter(name -> name.getParent() instanceof NameExpr ||
                        name.getParent() instanceof FunctionHandleExpr)
        .collect(Collectors.toSet());
  }

  private ASTNode<?> resolveCall(ExprStmt call) {
    String target = ((NameExpr) call.getExpr()).getName().getID();
    ParsedCompilationUnitsContextStack context =
        new ParsedCompilationUnitsContextStack(Lists.<GenericFile>newLinkedList(), cu.getRootFolder(), cu);
    ast.Function enclosingFunction = NodeFinder.findParent(ast.Function.class, call);
    context.push(enclosingFunction);
    return context.resolveFunctionReference(context.peek().resolve(target));
  }

  private boolean isCallToScript(ExprStmt call) {
    return call.getExpr() instanceof NameExpr && resolveCall(call) == target;
  }


  private void performInlining(ExprStmt callStmt, Script target) {
    Transformer transformer = context.getTransformer(callStmt);
    ASTNode<?> parent = callStmt.getParent();
    int i = parent.getIndexOfChild(callStmt);
    transformer.remove(callStmt);
    for(int j = target.getNumStmt() - 1; j >= 0; --j){
      transformer.insert(parent, transformer.copy(target.getStmt(j)), i);
    }
  }
  
  public void inlineCallsToScript(ast.Function f) {
    NodeFinder.find(ExprStmt.class, f)
        .filter(this::isCallToScript)
        .forEach(this::inlineCallToScript);
  }

  public void inlineCallToScript(ExprStmt callStmt) {
    ast.Function f = NodeFinder.findParent(ast.Function.class, callStmt);
    VFFlowSensitiveAnalysis kind_analysis_f = new VFFlowSensitiveAnalysis(f);
    kind_analysis_f.analyze();

    VFFlowSensitiveAnalysis kind_analysis_s = new VFFlowSensitiveAnalysis(target);
    kind_analysis_s.analyze();
    ReachingDefs reachingDefs = new ReachingDefs(f);
    reachingDefs.analyze();
    Map<String, Set<Def>> assigned=reachingDefs.getOutFlowSets().get(callStmt);
    if(assigned==null )
      System.out.println("NULL");
    Set<String> nested= findNested(f);
    Set<Name> symbols_s = findNameExpr(target);

    performInlining(callStmt, target);

    Map<String, VFDatum> kind_f_results = kind_analysis_f.getOutFlowSets().get(f);
    VFFlowSensitiveAnalysis kind_analysis_post = new VFFlowSensitiveAnalysis(f);
    kind_analysis_post.analyze();
    for (Name n: symbols_s){
      String sym = n.getID();
      VFDatum kind_s;
      kind_s=kind_analysis_s.getOutFlowSets().get(target).get(sym);
      if (kind_s == null) kind_s = VFDatum.UNDEF;
      VFDatum kind_f=kind_f_results.get(sym);
      if (kind_f == null) kind_f = VFDatum.UNDEF;
      VFDatum kind_post = kind_analysis_post.getOutFlowSets().get(f).get(sym);
      if (kind_post == null) kind_post = VFDatum.UNDEF;
      if (kind_s!=VFDatum.UNDEF && kind_f!=VFDatum.UNDEF && kind_s.merge(kind_f)==VFDatum.TOP)
        addError(new Exceptions.RenameRequired(n));
      if (kind_s.isFunction() || kind_s.isID()){
        if (kind_f.isFunction()  || kind_f==VFDatum.UNDEF) {
          if (nested.contains(sym))
            addError(new Exceptions.NameResolutionChangeException(n));
        }
      }
      if (kind_s.isID() && (kind_f.isVariable()))
        if ((!assigned.containsKey(sym)) || (assigned.get(sym).contains(ReachingDefs.UNDEF)))
          addError(new Exceptions.UnassignedVariableException(n));	
      if (kind_s.isID() && kind_post.isFunction())
        addError(new Exceptions.WarnIDToFunException(n));
    }
  }
}