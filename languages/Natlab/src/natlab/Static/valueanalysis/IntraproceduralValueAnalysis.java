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
 * store a flow set for every statement, which is a map of
 * variablename->classname->abstract value
 * 
 * This class operates on the static IR.
 * @author ant6n
 */
public class IntraproceduralValueAnalysis<D extends MatrixValue<D>> extends  IRAbstractSimpleStructuralForwardAnalysis<ValueFlowMap<D>>{
    StaticFunction function;
    MatrixValueFactory<D> factory;
    
    public IntraproceduralValueAnalysis(StaticFunction function, MatrixValueFactory<D> factory) {
        super(function.getAst());
        this.function = function;
        DEBUG = true;
        this.factory = factory;
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
        ValueFlowMap<D> map = in1.merge(in2);
        out.clear(); //TODO - use correct valueflow map method union (in1,dest)
        out.putAll(map);
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
        //find all combinations of arguments
        
        
        //LinkedList<LinkedList<Value<D>>> list;
        
        //for every combination, find which function actually gets called
        //then analyze it etc.
        
        
        //combine all the results
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void caseLoopVar(AssignStmt assign) {
        IRForStmt node = (IRForStmt)assign.getParent();
        ValueFlowMap<D> flow = getCurrentInSet().copy();
        ValueSet<D> result = ValueSet.newInstance();
        //set the loop var
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
        ValueFlowMap<D> flow = getCurrentInSet().copy();
        ValueSet<D> array = flow.get(node.getArrayName().getID());
        ValueSet<D> result = ValueSet.newInstance();
        //go through all possible array values
        for (Value<D> arrayValue : array){
            if (arrayValue instanceof MatrixValue<?>){
                //go through all possible index sets
                for (List<Value<D>> indizes : cross(flow,node.getIndizes())){
                    result = result.add(arrayValue.subsref(indizes));
                }
            } else if (arrayValue instanceof FunctionHandleValue<?>){
                //TODO call function
                throw new UnsupportedOperationException("indexing function handle");
                
            } else {
                //TODO more possible values here
                throw new UnsupportedOperationException("array get received unknown value "+arrayValue);
            }
        }
        
        //put result assign/set flowsets
        flow.put(node.getTarget().getID(), result);
        setCurrentOutSet(flow);
        associateInAndOut(node);
    }
    
    //TODO make it deal with overloading properly
    @Override
    public void caseIRAssignFunctionHandleStmt(IRAssignFunctionHandleStmt node) {
        //find var and remove
        String targetName = node.getTarget().getID();
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
        String targetName = node.getTarget().getID();
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
}





