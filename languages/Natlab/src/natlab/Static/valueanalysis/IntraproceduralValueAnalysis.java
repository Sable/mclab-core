package natlab.Static.valueanalysis;

import java.util.*;
import java.util.List;

import ast.*;
import natlab.Static.callgraph.StaticFunction;
import natlab.Static.classes.reference.PrimitiveClassReference;
import natlab.Static.ir.*;
import natlab.Static.ir.analysis.*;
import natlab.Static.valueanalysis.constant.*;
import natlab.Static.valueanalysis.value.*;
import natlab.toolkits.analysis.FlowMap;
import natlab.toolkits.path.FunctionReference;

/**
 * This analysis attempts to find the class of every variable.
 * It also propagates some constant information. This analysis
 * stores a flow set for every statement, which is a map of
 * variablename->classname->abstract value
 * 
 * This class operates on the static IR.
 * @author ant6n
 */
public class IntraproceduralValueAnalysis<D extends MatrixValue<D>> extends  IRAbstractSimpleStructuralForwardAnalysis<ValueFlowMap<D>>{
    StaticFunction function;
    MatrixValueFactory<D> factory;
    ValuePropagator<D> valuePropagator;
    
    public IntraproceduralValueAnalysis(StaticFunction function, MatrixValueFactory<D> factory) {
        super(function.getAst());
        this.function = function;
        DEBUG = true;
        this.factory = factory;
        this.valuePropagator = factory.getValuePropagator();
    }
    
    //TODO - a second constructor which also takes an Args<D> object

    /*********** inherited stuff **************************************/
    @Override
    public void copy(ValueFlowMap<D> source, ValueFlowMap<D> dest) {
        dest.clear();
        dest.putAll(source);
    }

    @Override
    public void merge(ValueFlowMap<D> in1, ValueFlowMap<D> in2, ValueFlowMap<D> out) {
        in1.union(in2, out);
    }

    @Override
    public ValueFlowMap<D> newInitialFlow() {
        return new ValueFlowMap<D>();
    }

    /*********** Function case - setting up args *************************/
    @Override
    public void caseFunction(Function node) {
        // TODO Auto-generated method stub
        super.caseFunction(node);
    }

    /*********** statement cases *****************************************/
    @Override
    public void caseIRCallStmt(IRCallStmt node) {
        //find function
        FunctionReference ref = function.getCalledFunctions().get(node.getFunctionName().getID());
        //do call
        setCurrentOutSet(
                doFunctionCall(ref, getCurrentInSet(), node.getArguments(), node.getTargets()));
        //associate flowsets
        associateInAndOut(node);
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void caseLoopVar(AssignStmt assign) {
        IRForStmt node = (IRForStmt)assign.getParent();
        ValueFlowMap<D> flow = getCurrentInSet().copy();
        ValueSet<D> result = ValueSet.newInstance();
        //set the loop var
        //TODO - do we have to check whether colon is overloaded?
        if (node.hasIncr()){ //there's an inc value
            for (LinkedList<Value<D>> list : cross(flow,
                    node.getLowerName(),node.getIncName(),node.getUpperName())){
                result = result.add(((D)list.getFirst()).forRange((D)list.getLast(),(D)list.get(1)));
            }
        } else { //there's no inc value
            for (LinkedList<Value<D>> list : cross(flow,
                    node.getLowerName(),node.getUpperName())){
                result = result.add(((D)list.getFirst()).forRange((D)list.getLast(),null));
            }
        }
        //put results
        flow.put(node.getLoopVarName().getID(), result);
        setCurrentOutSet(flow);
        associateInAndOut(node);
    }
    
    @Override
    public void caseIRArrayGetStmt(IRArrayGetStmt node) {
        ValueFlowMap<D> flow = getCurrentInSet(); //note copied!
        ValueSet<D> array = flow.get(node.getArrayName().getID());
        ValueFlowMap<D> result = null;
        //go through all possible array values
        for (Value<D> arrayValue : array){
            if (arrayValue instanceof MatrixValue<?>){
                //go through all possible index sets
                //TODO - deal with overloading etc.
                //TODO - errors on assign - use is assign to var??
                for (List<Value<D>> indizes : cross(flow,node.getIndizes())){
                    result = unionOrSet(result,doAssign(flow,node.getTargets(),
                            Collections.singleton(ValueSet.newInstance(arrayValue.subsref(indizes)))));
                    
                }
            } else if (arrayValue instanceof FunctionHandleValue<?>){
                //go through all function handles this may represent and get the result
                for (FunctionReference ref :((FunctionHandleValue<D>)arrayValue).getFunctions()){
                  result = unionOrSet(result,doFunctionCall(
                          ref, flow, node.getIndizes(), node.getTargets()));
                }
            } else {
                //TODO more possible values here
                throw new UnsupportedOperationException("array get received unknown value "+arrayValue);
            }
        }
        
        //put result assign/set flowsets
        setCurrentOutSet(result);
        associateInAndOut(node);
    }
    
    //TODO make it deal with overloading properly
    @Override
    public void caseIRAssignFunctionHandleStmt(IRAssignFunctionHandleStmt node) {
        //find var and remove
        String targetName = node.getTargetName().getID();
        ValueFlowMap<D> flow = getCurrentInSet().copy();
        flow.remove(targetName);
        
        //find the function handle
        System.err.println(function.getCalledFunctions());
        FunctionReference f = 
            function.getCalledFunctions().get(node.getFunction().getID());
        
        //assign
        flow.put(targetName, ValueSet.newInstance(new FunctionHandleValue<D>(f)));
        
        //assign result
        setCurrentOutSet(flow);
        
        //set in/out set
        associateInAndOut(node);
    }
    
    
    @Override
    public void caseIRAssignLiteralStmt(IRAssignLiteralStmt node) {
        //get literal and make constant
        Constant constant = Constant.get(node.getRHS());

        //put in flow map
        ValueFlowMap<D> flow = getCurrentInSet().copy();
        String targetName = node.getTargetName().getID();
        flow.remove(targetName);
        flow.put(targetName, factory.newValueSet(constant));
        
        //assign result
        setCurrentOutSet(flow);

        //set in/out set
        associateInAndOut(node);
    }
    
    
    @Override
    public void caseStmt(Stmt node) {
        System.out.println(node+" "+currentInSet);
        //set in/out set
        associateInAndOut(node);
    }
    
    /**
     * associates the current in and out set with the given node
     */
    private void associateInAndOut(ASTNode<?> node){
        associateInSet(node, getCurrentInSet());
        associateOutSet(node, getCurrentOutSet());
    }
    
    
    /**
     * given an IRCommaSeparated list and a flow set, returns all possible combinations
     * of values as a list of lists
     */
    private List<LinkedList<Value<D>>> cross(ValueFlowMap<D> flow,IRCommaSeparatedList args){
         if (!args.isAllNameExpressions()){
             //TODO deal with this
             throw new UnsupportedOperationException("received bad input values on comma separated list");
         } else {
             //get list of value sets from the names
             ArrayList<ValueSet<D>> values = new ArrayList<ValueSet<D>>(args.size());
             for (Name name : args.asNameList()){
                 values.add(flow.get(name.getID()));
             }
             return ValueSet.cross(values);
         }
    }
    
    /**
     * returns ValueSet.cross but using valueSets from the given flowmap, using Names
     */
    private List<LinkedList<Value<D>>> cross(ValueFlowMap<D> flow, Name... args){
        LinkedList<ValueSet<D>> list = new LinkedList<ValueSet<D>>();
        for (Name arg : args){
            list.add(flow.get(arg.getID()));
        }
        return ValueSet.cross(list);
    }
    
    
    /**
     * implements the flow equations for calling functions
     * Returns a new value flow map which represents the outset of a call
     */
    private ValueFlowMap<D> doFunctionCall(
            FunctionReference function,ValueFlowMap<D> flow,
            IRCommaSeparatedList args,IRCommaSeparatedList targets){
        ValueFlowMap<D> result = null;
        //TODO - do overloading, deal with dominant args etc.
        if (function.isBuiltin()){
            for (LinkedList<Value<D>> argumentList : cross(flow,args)){
                Args<D> argsObj = Args.newInstance(argumentList);
                Res<D> res = valuePropagator.call(function.getname(), argsObj);
                result = unionOrSet(result,doAssign(flow, targets, res));
                System.out.println(result);
            }
        } else {
            //TODO - deal with this
            return null;
        }
        return result;
    }
    
    
    /**
     * assigns the given collection of values, to the targets represented
     * by the comma separated list. Returns a new flowmap which is a copy
     * of old one, except for the newly assigned valeus.
     * 
     * TODO - should this be part of ValueFlowMap? Or maybe a helper
     */
    private ValueFlowMap<D> doAssign(ValueFlowMap<D> flow, 
            IRCommaSeparatedList targets, Collection<ValueSet<D>> values){
       ValueFlowMap<D> result = flow.copy();
       if (targets.isAllNameExpressions()){
           Iterator<ValueSet<D>> iValues = values.iterator();
           Iterator<Name> iNames = targets.asNameList().iterator();
           while (iNames.hasNext()){
               result.put(iNames.next().getID(), iValues.next());
           }
       } else {
           throw new UnsupportedOperationException("no support for non-primitive assings");
       }
       return result;
    }
    
    
    
    /**
     * merges flow map  b into flow map a and returns a, unless a or b is null 
     * - in which case the other argument gets returned.
     */
    private ValueFlowMap<D> unionOrSet(ValueFlowMap<D> a, ValueFlowMap<D> b){
        if (a == null) return b;
        if (b == null) return a;
        a.union(b);
        return a;
    }
}





