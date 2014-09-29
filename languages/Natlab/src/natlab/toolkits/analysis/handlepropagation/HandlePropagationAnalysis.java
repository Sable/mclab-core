package natlab.toolkits.analysis.handlepropagation;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import natlab.LookupFile;
import natlab.toolkits.analysis.handlepropagation.handlevalues.AbstractValue;
import natlab.toolkits.analysis.handlepropagation.handlevalues.AnonymousHandleValue;
import natlab.toolkits.analysis.handlepropagation.handlevalues.NamedHandleValue;
import natlab.toolkits.analysis.handlepropagation.handlevalues.Value;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.utils.LoadFunction;
import analysis.ForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellArrayExpr;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Expr;
import ast.ExprStmt;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.GlobalStmt;
import ast.LambdaExpr;
import ast.LiteralExpr;
import ast.MatrixExpr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.PersistentStmt;
import ast.Row;
import ast.Stmt;
import ast.StringLiteralExpr;

/**
 * This is an intraprocedural function handle propagation analysis.
 * For every statement it computes mapping from variable name strings
 * to possible handle targets. If no entry exists then nothing is
 * known about that variable name, it could not be a handle, or it
 * could be a handle with no information associated with it such as
 * input parameters.
 */
public class HandlePropagationAnalysis extends ForwardAnalysis<HandleFlowset> {
  /*
    CONTRACT for the expression cases:
    - At the end of a case the newHandleTargets should contain what
    it previously contained plus what is intended to be added
      - Don't clear it
      - Only put values into the newHandleTargets set if they need to be
      there at the end of the case. Do not pollute it.
    - Do not depend on any values in the incoming
    newHandleTargets. You can depend on values generated from
    children and descendents though.
      - This means that you can backup the newHandleTargets and
      start with a fresh one when analyzing children.
  */

  //flags to control behaviour of how function call are handled
  private boolean destructiveCalls = true;

  private TreeSet<Value> newHandleTargets = new TreeSet<>();

  private HashSet<String> globalNames = new HashSet<>();
  private HashSet<String> persistentNames = new HashSet<>();

  private Stmt currentStmt = null;

  private boolean change = false;

  private VFPreorderAnalysis kindAnalysis;

  private TreeSet<Value> allHandleTargets = new TreeSet<>();
  private boolean inAssignment = false;
  private boolean inExprStmt = false;

  public HandleFlowset newInitialFlow() {
    return new HandleFlowset();
  }

  public HandlePropagationAnalysis(ASTNode tree) {
    this(tree, new VFPreorderAnalysis(tree));
  }

  public HandlePropagationAnalysis(ASTNode tree, VFPreorderAnalysis kind) {
    super(tree);
    kindAnalysis = kind;
    if (!kind.isAnalyzed()) {
      kind.analyze();
    }
    currentOutSet = newInitialFlow();
  }

  public HandleFlowset copy(HandleFlowset source) {
    return source.copy();
  }

  public HandleFlowset merge(HandleFlowset in1, HandleFlowset in2) {
    HandleFlowset out = new HandleFlowset();
    in1.union(in2, out);
    return out;
  }

  /**
   * Function case sets up the in data for the function body. It
   * does this based on the in and out parameters and the identifier
   * information from the kind analysis.
   */
  public void caseFunction(Function node) {
    analyze(node.getNestedFunctions());

    HandleFlowset newOutSet = new HandleFlowset();
    node.getOutputParams().forEach(out -> newOutSet.add(out.getID(), newUndefSet()));
    node.getInputParams().forEach(in -> newOutSet.add(in.getID(), newGeneralSet()));

    inFlowSets.put(node, new HandleFlowset());
    currentInSet = newOutSet;
    currentOutSet = newOutSet;
    analyze(node.getStmts());
    outFlowSets.put(node, currentOutSet.copy());
  }


  //store flow sets for all stmts. do it after analyzing it's
  //children. This is because handle expressions will cause a copy
  //so you want to store the copy of the flowset in those cases and
  //not a copy otherwise.
  public void caseStmt(Stmt node) {
    inAssignment = false;
    boolean oldChange = change;
    change = false;
    currentStmt = node;
    HandleFlowset backupIn = currentInSet.copy();
    caseASTNode(node);
    inFlowSets.put(node, change ? backupIn : currentInSet);
    outFlowSets.put(node, currentOutSet.copy());
    change = oldChange || change;
  }

  /**
   * Default case for expressions. Unless explicitly written
   * otherwise, an expressions will generate {DO}. If this behavior
   * is not desired for a given expression type, then override the
   * appropriate case.
   * A recursive traversal is still performed in order to capture
   * effects of fn calls.
   */
  public void caseExpr(Expr node) {
    TreeSet<Value> handleBackup = newHandleTargets;
    newHandleTargets = new TreeSet<>();
    caseASTNode(node);
    newHandleTargets = handleBackup; //discard results of children...
    newHandleTargets.add(AbstractValue.newDataOnly()); //..and force a 'data' result
  }


  /**
   * case for cell index and struct access expressions
   * DO in target -> DO
   * DHO in target -> H
   * DWH in target -> H,DWH
   */
  public void dataAccess(Expr target, ast.List<Expr> args) {
    TreeSet<Value> handleBackup = newHandleTargets;

    newHandleTargets = new TreeSet<>();
    //analyze children, discard result, target last
    analyze(args);

    newHandleTargets = new TreeSet<>();
    analyze(target);

    if (newHandleTargets.contains(AbstractValue.newDataOnly())) {
      handleBackup.add(AbstractValue.newDataOnly());
    }
    if (newHandleTargets.contains(AbstractValue.newDataHandleOnly())) {
      handleBackup.add(AbstractValue.newHandle());
    }
    if (newHandleTargets.contains(AbstractValue.newDataWithHandles())) {
      handleBackup.add(AbstractValue.newHandle());
      handleBackup.add(AbstractValue.newDataWithHandles());
    }
    newHandleTargets = handleBackup;
  }

  public void caseCellIndexExpr(CellIndexExpr node) {
    dataAccess(node.getTarget(), node.getArgList());
  }

  public void caseDotExpr(DotExpr node) {
    dataAccess(node.getTarget(), new ast.List<>()); //the arguments are empty...
  }

  /**
   * gen(node) = struct(union(gen(node.children)))
   */
  public void arrayExpr(Expr node) {
    TreeSet<Value> handleBackup = newHandleTargets;
    newHandleTargets = new TreeSet<>();
    caseASTNode(node);
    handleBackup.addAll(makeStructDataSet(newHandleTargets));
    newHandleTargets = handleBackup;
  }

  public void caseCellArrayExpr(CellArrayExpr node) {
    arrayExpr(node);
  }

  public void caseMatrixExpr(MatrixExpr node) {
    arrayExpr(node);
  }

  public void caseExprStmt(ExprStmt node) {
    inExprStmt = true;
    caseStmt(node);
  }

  private void caseGlobalOrPersistentStmt(Set<String> set, ASTNode<?> node, ast.List<Name> names) {
    inFlowSets.put(node, currentInSet);
    HandleFlowset newOut = currentInSet.copy();

    for (Name n : names) {
      newOut.add(n.getID(), newGeneralSet());
      set.add(n.getID());
    }
    currentOutSet = newOut;
    outFlowSets.put(node, currentOutSet);
  }

  public void caseGlobalStmt(GlobalStmt node) {
    caseGlobalOrPersistentStmt(globalNames, node, node.getNames());
  }

  public void casePersistentStmt(PersistentStmt node) {
    caseGlobalOrPersistentStmt(persistentNames, node, node.getNames());
  }

  public void caseFunctionHandleExpr(FunctionHandleExpr node) {
    //note this is wrong due to the implicit ans assignment
    Value target = new NamedHandleValue(node.getName().getID());
    newHandleTargets.add(target);
    allHandleTargets.add(target);
  }

  public void caseLambdaExpr(LambdaExpr node) {
    //note this is wrong due to the implicit ans assignment
    if (inAssignment) {
      Value target = new AnonymousHandleValue(node);
      newHandleTargets.add(target);
      allHandleTargets.add(target);
    }
  }

  /**
   * There are 4 cases for what to generate for a NameExpr.
   * 1) a copy of the values of the NameExpr if the kind of the NameExpr
   * is a variable.
   * 2) {H,DWH} if it is a function
   * 3) a copy of the values can't be undefined
   * 4) {H,DWH} unioned with the values of the NameExpr except for
   * undefined values,  other wise
   * <p>
   * Note that case 2 and 4 are treated as function calls.
   */
  public void caseNameExpr(NameExpr node) {
    currentOutSet = currentInSet;
    VFDatum kind = getKindDatum(node);
    if (kind != null) {
      //case 1
      if (kind.isVariable()) {
        TreeSet<Value> namesTargets = new TreeSet<>();
        TreeSet<Value> inTargets = currentInSet.get(node.getName().getID());
        if (inTargets != null) {
          namesTargets.addAll(inTargets);
        }
        namesTargets.remove(AbstractValue.newUndef());
        newHandleTargets.addAll(namesTargets);
        return;
      }
      //case 2
      else if (kind.isFunction()) {
        TreeSet<Value> functionResult = handleFunctionCall(node.getName().getID());
        if (functionResult != null) {
          newHandleTargets.addAll(functionResult);
        }
        return;
      }
    }
    //case 3
    if (!isIdUndef(node.getName().getID())) {
      TreeSet<Value> inTargets = currentInSet.get(node.getName().getID());
      if (inTargets != null) {
        newHandleTargets.addAll(inTargets);
      }
      return;
    }

    TreeSet<Value> newValues = handleFunctionCall(node.getName().getID());
    TreeSet<Value> idIn = currentInSet.get(node.getName().getID());
    if (idIn != null) {
      newValues.addAll(idIn);
    }
    newValues.remove(AbstractValue.newUndef());
    newHandleTargets.addAll(newValues);
  }

  /**
   * The arguments need to be analyzed. If the target is not a
   * NameExpr then analyze the target. If the target is a NameExpr
   * then there are 2 cases.
   * <p>
   * 1) the name has kind NFN and is treated as a function call
   * giving values dependent on the function call
   * 2) otherwise it has some value and the values generated are the
   * union for all values v of:
   * (a) handle call return if v is some sort of handle
   * (b) {H,DWH} if v=U and kind(name)!=VAR, or
   * (c) simply v otherwise.
   * <p>
   * If the target is not a NameExpr then, analyze the target.
   * There are two cases based on what is generated by that
   * analysis.
   * 3) {DO} if DO is in the what is generated
   * 4) {H,DWH} otherwise (NOTE: it would need to contain DWH or DHO
   * for it not to be an error)
   */
  public void caseParameterizedExpr(ParameterizedExpr node) {
    currentOutSet = currentInSet;
    //Store the incoming handle targets so deeper analysis doesn't
    //add anything to it.
    TreeSet<Value> newHandleTargetsBackup = newHandleTargets;
    newHandleTargets = new TreeSet<Value>();

    analyze(node.getArgs());

    if (node.getTarget() instanceof NameExpr) {
      //restore the correct newHandleTargets
      newHandleTargets = newHandleTargetsBackup;

      NameExpr nameExpr = (NameExpr) node.getTarget();
      VFDatum kind = getKindDatum(nameExpr);
      // Case 1
      if (kind != null && kind.isFunction()) {
        newHandleTargets.addAll(handleFunctionCall(nameExpr.getName().getID(), node.getArgs()));
        return;
      }
      //Case 2
      for (Value v : currentInSet.getOrDefault(nameExpr.getName().getID(), new TreeSet<>())) {
        //case 2.a
        if (v instanceof NamedHandleValue) {
          NamedHandleValue nhv = (NamedHandleValue) v;
          newHandleTargets.addAll(handleFunctionCall(nhv.getName(), node.getArgs()));
          break;
        }
        if (v instanceof AnonymousHandleValue) {
          newHandleTargets.addAll(handleUnknownFunctionCall());
          break;
        }
        if (v instanceof AbstractValue) {
          AbstractValue av = (AbstractValue) v;
          if (av.isHandle()) {
            newHandleTargets.addAll(handleUnknownFunctionCall());
            break;
          }
          //case 2.b
          else if (av.isUndef()) {
            if (kind != null && kind.isVariable()) {
              newHandleTargets.addAll(handleFunctionCall(nameExpr.getName().getID(), node.getArgs()));
              break;
            }
          }
        }
        newHandleTargets.add(v);
      }
    }
    //if it isn't a name expr, analyze the target.
    else {
      analyze(node.getTarget());
      TreeSet<Value> generatedTargets = newHandleTargets;
      newHandleTargets = newHandleTargetsBackup;
      //case 3
      if (newHandleTargets.contains(AbstractValue.newDataOnly()))
        newHandleTargets.addAll(newDOSet());
        //case 4
      else
        newHandleTargets.addAll(handleUnknownFunctionCall());
    }
  }

  public void caseLiteralExpr(LiteralExpr node) {
    newHandleTargets.addAll(newDOSet());
  }

  //param, cellindex, dot, matrix, cellArray
  //  set to may of Exp primary symbol or of {} if no val exists for it
  public void caseAssignStmt(AssignStmt node) {
    inAssignment = true;
    currentStmt = node;
    boolean oldChange = change;
    change = false;

    HandleFlowset tmpInSet = currentInSet.copy();
    HandleFlowset newOutSet = tmpInSet.copy();

    Collection<String> lvalues = node.getLValues();

    currentOutSet = newOutSet;
    newHandleTargets = new TreeSet<Value>();
    analyze(node.getRHS());

    lvalues.forEach(newOutSet::remove);

    handleLHS(newOutSet, node.getLHS(), tmpInSet);

    inFlowSets.put(node, tmpInSet);
    outFlowSets.put(node, currentOutSet.copy());
    inAssignment = false;
    change = oldChange || change;
  }

  /**
   * Associates the correct data with the give lhs with the given
   * set.
   * <p>
   * If lhs is a MatrixExpr, recurse on each expression.
   * <p>
   * There are 4 cases for how to associate the date
   * 1) lhs is a NameExpr, in which case you just associate the
   * data directly.
   * 2) lhs is a ParameterizedExpr, in which case the arguments must
   * be analyzed for effects and then there are two cases
   * 2.a) the target is a NameExpr, in which case the structured
   * data from name's data merged with the structured version of the
   * generated data are associated with the name.
   * 2.b) recurse into the target
   * 3) lhs is a cell indexing, deal with the arguments and
   * 3.a) struct(in(name))Ustruct(generated data)
   * 3.b) recurse
   * 4) lhs is a field access so
   * 4.a) structOnly(in(id))Ustruct(generated Data)
   * 4.b) recurse
   */
  public void handleLHS(HandleFlowset set, Expr lhs, HandleFlowset in) {
    TreeSet<Value> backupTargets = newHandleTargets;
    TreeSet<Value> data;
    if (lhs instanceof MatrixExpr) {
      MatrixExpr me = (MatrixExpr) lhs;
      //is matrixExpr, since on LHS assume exactly one row.
      Row row = me.getRow(0);
      for (Expr elmt : row.getElements()) {
        //BREAKING
        handleLHS(set, elmt, in);
      }
    }
    //case 1
    else if (lhs instanceof NameExpr)
      storeDataToNameExpr((NameExpr) lhs, set, newHandleTargets);
      //case 2
    else if (lhs instanceof ParameterizedExpr) {
      ParameterizedExpr pe = (ParameterizedExpr) lhs;
      newHandleTargets = new TreeSet<>();

      analyze(pe.getArgs());

      newHandleTargets = backupTargets;
      //case 2.a
      if (pe.getTarget() instanceof NameExpr) {
        String id = ((NameExpr) pe.getTarget()).getName().getID();
        data = makeStructDataSet(newHandleTargets);
        data.addAll(getStructuredDataOnly(in.get(id)));
        storeDataToNameExpr((NameExpr) pe.getTarget(), set, data);
      } else {
        // case 2.b
        handleLHS(set, pe.getTarget(), in);
      }
    }
    //case 3
    else if (lhs instanceof CellIndexExpr) {
      CellIndexExpr cie = (CellIndexExpr) lhs;
      newHandleTargets = new TreeSet<>();
      analyze(cie.getArgs());

      newHandleTargets = backupTargets;
      //case 3.a
      if (cie.getTarget() instanceof NameExpr) {
        String id = ((NameExpr) cie.getTarget()).getName().getID();
        data = makeStructDataSet(newHandleTargets);
        data.addAll(getStructuredDataOnly(in.get(id)));
        storeDataToNameExpr((NameExpr) cie.getTarget(), set, data);
      } else {
        //case 3.b
        handleLHS(set, cie.getTarget(), in);
      }
    }
    //case 4
    else if (lhs instanceof DotExpr) {
      DotExpr de = (DotExpr) lhs;
      //case 4.a
      if (de.getTarget() instanceof NameExpr) {
        String id = ((NameExpr) de.getTarget()).getName().getID();
        data = makeStructDataSet(newHandleTargets);
        data.addAll(getStructuredDataOnly(in.get(id)));
        storeDataToNameExpr((NameExpr) de.getTarget(), set, data);
      } else {
        //case 4.b
        handleLHS(set, de.getTarget(), in);
      }
    }
  }

  /**
   * Associates newHandleTargets to the given NameExpr in the given
   * set.
   */
  private void storeDataToNameExpr(NameExpr nameExpr, HandleFlowset set, TreeSet<Value> values) {
    set.addAll(nameExpr.getName().getID(), values);
  }

  /**
   * Converts the values in a set into the struct data versions. So
   * if these values were in a struct, the result represents the
   * struct's possible values.
   * Does not alter the argument.
   */
  public TreeSet<Value> makeStructDataSet(TreeSet<Value> set) {
    if (set == null || set.isEmpty()) {
      return new TreeSet<>();
    } else if (set.size() == 1 && set.contains(AbstractValue.newDataOnly())) {
      return new TreeSet<>(set);
    } else {
      return set.stream().allMatch(Value::isHandle) ?
          setOf(AbstractValue.newDataHandleOnly()) :
          setOf(AbstractValue.newDataWithHandles());
    }
  }

  /**
   * Gets values from a set, but only structured data values. This
   * also means that the set returned will have at most one value,
   * since all sets can have at most one structured data value.
   */
  public TreeSet<Value> getStructuredDataOnly(TreeSet<Value> set) {
    if (set == null) {
      return new TreeSet<>();
    }
    TreeSet<Value> newSet = setOf(
        AbstractValue.newDataOnly(),
        AbstractValue.newDataHandleOnly(),
        AbstractValue.newDataWithHandles());
    newSet.retainAll(set);
    return newSet;
  }

  private TreeSet<Value> setOf(Value... values) {
    TreeSet<Value> set = new TreeSet<>();
    Collections.addAll(set, values);
    return set;
  }

  /**
   * Builds a version of the most general set for assigned values, a
   * set containing {H,DWH}
   */
  private TreeSet<Value> newGeneralAssignedSet() {
    return setOf(AbstractValue.newHandle(), AbstractValue.newDataWithHandles());
  }

  /**
   * Builds a version of the most general set for values that can be
   * unassigned. This set is {UNDEF,H,DWH}
   */
  private TreeSet<Value> newGeneralSet() {
    return setOf(
        AbstractValue.newHandle(),
        AbstractValue.newDataWithHandles(),
        AbstractValue.newUndef());
  }

  /**
   * Builds a set containing only UNDEF.
   */
  private TreeSet<Value> newUndefSet() {
    return setOf(AbstractValue.newUndef());
  }

  /**
   * Builds a set containing only DataOnly.
   */
  private TreeSet<Value> newDOSet() {
    return setOf(AbstractValue.newDataOnly());
  }

  /**
   * Builds a set containing only Handle.
   */
  private TreeSet<Value> newHSet() {
    return setOf(AbstractValue.newHandle());
  }

  /**
   * Abstracts getting a kind flowset. This is to deal with possible
   * differences in getting it for scripts and functions.
   */
  private Map<String, VFDatum> getKindSet() {
    return kindAnalysis.getFlowSets().get(currentStmt);
  }

  /**
   * Abstracts getting the kind datum for a given NameExpr.
   */
  private VFDatum getKindDatum(NameExpr n) {
    return kindAnalysis.getResult(n.getName());
  }

  /**
   * Checks if a given id has a possible undefined value. This is
   * done in the context of the current stmt. An id has such a value
   * if it's set of possible values contains UNDEF or if it has no
   * possible values and it's kind is ID (Bottom) or VAR.
   */
  public boolean isIdUndef(String id) {
    TreeSet<Value> values = currentInSet.get(id);
    if (values != null && values.contains(AbstractValue.newUndef()))
      return true;
    else if (values == null || values.size() == 0) {
      if (getKindSet() == null)
        return true;
      VFDatum kind = getKindSet().get(id);
      if (kind == null || kind.isID() || kind.isVariable())
        return true;
    }
    return false;
  }

  /**
   * Deals with the effects of function calls. All knowledge of
   * globals variables is destroyed. If set to do so then this will
   * destroy all knowledge of all identifiers. Will also return the
   * values resulting from the call.
   */
  private TreeSet<Value> handleFunctionCall(String name) {
    if (name.equals("eval")) {
      destroyInfo();
      return newGeneralAssignedSet();
    } else if (name.equals("clear")) {
      //note, clear does not remove elements from global set
      //because we don't check what type of clear it is, so it
      //could be clear globals
      undefAll();
      return newGeneralAssignedSet();
    }
    String outputInfo = LookupFile.getOutputInfo(name);
    if (outputInfo == null) {
      return handleUnknownFunctionCall();
    } else {
      switch (outputInfo) {
      case "DO":
        return newDOSet();
      case "H":
        return newHSet();
      case "DWH,H":
      case "H,DWH":
        return newGeneralAssignedSet();
      default:
        return newGeneralAssignedSet();
      }
    }
  }

  private TreeSet<Value> handleFunctionCall(String name, ast.List<Expr> args) {
    if (name.equals("load")) {
      if (inExprStmt) {
        Set<String> set = LoadFunction.loadWhat(args);
        if (set == null) {
          destroyInfo();
        } else {
          destroyInfo(set);
        }
      }
      return newUndefSet();
    }
    if (name.equals("feval") &&
        args.getNumChild() > 0 &&
        args.getChild(0) instanceof StringLiteralExpr) {
      return handleFunctionCall(((StringLiteralExpr) args.getChild(0)).getValue());
    }
    return handleFunctionCall(name);
  }

  /**
   * Deals with the affects of function calls when the function name
   * is unknown. Works the same as handleFunctionCall but without the
   * possible lookup.
   */
  private TreeSet<Value> handleUnknownFunctionCall() {
    if (destructiveCalls) {
      destroyInfo();
    } else {
      destroyGlobalAndPersistentInfo();
    }
    return newGeneralAssignedSet();
  }

  /**
   * Destroys the information in the current set due to a function
   * call. This is done by adding {H,DWH} to all sets.
   */
  private void destroyInfo() {
    currentOutSet.entrySet().forEach(e -> destroyInfo(e.getKey(), e.getValue()));
  }

  /**
   * Destroy the information for a given identifier in the current set.
   */
  private void destroyInfo(String id) {
    destroyInfo(id, currentOutSet.get(id));
  }

  /**
   * Destroy the information for a given set of identifiers in the* current set.
   */
  private void destroyInfo(Set<String> set) {
    set.forEach(this::destroyInfo);
  }

  /**
   * Destroy the information for a given identifier with a given set
   * of values in the current set.
   */
  private void destroyInfo(String id, TreeSet<Value> values) {
    change = true;
    TreeSet<Value> newSet = newGeneralSet();
    if (values != null) {
      newSet.addAll(values);
    }
    currentOutSet.add(id, newSet);
  }

  /**
   * Adds undef to all identifier's info
   */
  private void undefAll() {
    for (Map.Entry<String, TreeSet<Value>> e : currentOutSet.entrySet()) {
      change = true;
      TreeSet<Value> values = new TreeSet<>();
      values.addAll(e.getValue());
      values.add(AbstractValue.newUndef());
      currentOutSet.add(e.getKey(), values);
    }
  }

  /**
   * Destroys the information in the current set for all globals.
   */
  private void destroyGlobalAndPersistentInfo() {
    for (String name : globalNames) {
      change = true;
      TreeSet<Value> newSet = newGeneralSet();
      Set<Value> set = currentOutSet.get(name);
      if (set != null) {
        newSet.addAll(set);
      }
      currentOutSet.add(name, newSet);
    }
    for (String name : persistentNames) {
      change = true;
      TreeSet<Value> newSet = newGeneralSet();
      Set<Value> set = currentOutSet.get(name);
      if (set != null) {
        newSet.addAll(set);
      }
      currentOutSet.add(name, newSet);
    }
  }
}