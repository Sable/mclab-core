package natlab.tame.valueanalysis;

import java.util.*;
import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.ClassRepository;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.interproceduralAnalysis.*;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.*;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.value.*;
import natlab.toolkits.path.FunctionReference;
import ast.*;

/**
 * This analysis attempts to find the class of every variable.
 * It also propagates some constant information. This analysis
 * stores a flow set for every statement, which is a map of
 * variablename->classname->value
 * 
 * This class operates on the static IR.
 * 
 * @author ant6n
 * 
 * extended by XU to support variables (also including arrays, 
 * but how about function call?) dependence analysis in 
 * loop statements.
 * 
 */
public class IntraproceduralValueAnalysis<V extends Value<V>>
extends TIRAbstractSimpleStructuralForwardAnalysis<ValueFlowMap<V>>
implements FunctionAnalysis<Args<V>, Res<V>>{
    StaticFunction function;
    ValueFactory<V> factory;
    ValuePropagator<V> valuePropagator;
    ValueFlowMap<V> argMap;
    Args<V> args;
    static boolean Debug = false;
    InterproceduralAnalysisNode<IntraproceduralValueAnalysis<V>, Args<V>, Res<V>> node;
    ClassRepository classRepository;
    TIRParentForwardingNodeCasehandler parentForwarder = new TIRParentForwardingNodeCasehandler(this);
    Set<String> dependentVars = new HashSet<String>(); // XU added it to store dependency analysis result.
    HashMap<String, LinkedList<V>> hookMap = new HashMap<String, LinkedList<V>>();
    
    public IntraproceduralValueAnalysis(InterproceduralAnalysisNode<IntraproceduralValueAnalysis<V>, Args<V>, Res<V>> node,
            StaticFunction function, ValueFactory<V> factory) {
        super(function.getAst());
        this.node = node;
        this.function = function;
        this.factory = factory;
        this.valuePropagator = factory.getValuePropagator();
        this.classRepository = node.getInterproceduralAnalysis().getFunctionCollection().getClassRepository();
    }
    
    /**
     * constructor that allows specifying values of the arguments
     */
    public IntraproceduralValueAnalysis(InterproceduralAnalysisNode<IntraproceduralValueAnalysis<V>, Args<V>, Res<V>> node,
            StaticFunction function, ValueFactory<V> factory, Args<V> args) {
    	this(node,function,factory);
        argMap = new ValueFlowMap<V>();
        this.args = args;
        //TODO check whether given args <= declared args
        for (int i = 0; i < args.size(); i++){
            argMap.put(
                    function.getAst().getInputParam(i).getID(),
                    ValueSet.newInstance(args.get(i)));
        }
        if (Debug) System.out.println("intraprocedural value analysis on "+function.getName()+" with args "+argMap);
    }
    
    
    /**
     * returns the result of the analysis. Runs the analysis if isAnalyzed is false
     */
    public Res<V> getResult(){
        if (! isAnalyzed()) this.analyze();
        Res<V> result = Res.newInstance();
        ValueFlowMap<V> flowResult = getOutFlowSets().get(function.getAst());
        //TODO - make sure there's the right number of outputs<AggrValue<AdvancedMatrixValue>>
        for (Name out : function.getAst().getOutputParamList()){
            result.add(flowResult.get(out.getID()));
        }
        return result;
    }
    
    public Args<V> getArgs(){
    	return args;
    }
    public ValueFlowMap<V> getArgMap(){
    	return argMap;
    }
    

    /*********** inherited stuff **************************************/
    @Override
    public void copy(ValueFlowMap<V> source, ValueFlowMap<V> dest) {
        dest.copyOtherIntoThis(source);
    }

    @Override
    public void merge(ValueFlowMap<V> in1, ValueFlowMap<V> in2, ValueFlowMap<V> out) {
        out.copyOtherIntoThis(in1.merge(in2));
    }

    @Override
    public ValueFlowMap<V> newInitialFlow() {
        return new ValueFlowMap<V>();
    }

    /*********** Function case - setting up args *************************/
    @Override
    public void caseFunction(Function node) {
        currentInSet = argMap.copy();
        caseASTNode(node);
        associateInAndOut(node);
    }

    /*********** statement cases *****************************************/
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
    	LoopDependencyAnalysis analyzeDependency = new LoopDependencyAnalysis(node);
    	dependentVars.addAll(analyzeDependency.getResult());
        parentForwarder.caseTIRWhileStmt(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
    	LoopDependencyAnalysis analyzeDependency = new LoopDependencyAnalysis(node);
    	dependentVars.addAll(analyzeDependency.getResult());
        parentForwarder.caseTIRForStmt(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("ircall: "+node.getPrettyPrinted());
        //set new callsite
        Callsite<IntraproceduralValueAnalysis<V>,Args<V>,Res<V>> callsite = this.node.createCallsiteObject(node);
        //find function
        String functionName = node.getFunctionName().getID();
        FunctionReference ref = function.getCalledFunctions().get(functionName);
        //find if function may be overloaded
        boolean checkOverloading;
        if (ref == null){ //the call couldn't be found - it has to be an overloaded function, otherwise an error
        	checkOverloading = true; 
        } else {
        	if (ref.path != null && ref.path.equals(this.function.getReference().getFile())){
        		checkOverloading = false; //if the found function is in the same file it cannot be overloaded
        	} else {
        		checkOverloading = true; //if it's not in the same function, we assume the function can be overloaded
        		//TODO - check this, which functions can be overloaded? - what about private functions etc?
        		//check via ref.referenceType
        	}
        }
        //do call
        setCurrentOutSet(assign(getCurrentInSet(),node.getTargets(),
                call(ref, getCurrentInSet(), node.getArguments(), node.getTargets(), callsite, null, functionName, checkOverloading)));
        //associate flowsets
        associateInAndOut(node);
    }
    
    public void caseIRCommentStmt(TIRCommentStmt node) {}
    
    @SuppressWarnings("unchecked")
    @Override
    public void caseLoopVar(AssignStmt assign) {
        if (checkNonViable(assign)) return;
        TIRForStmt node = (TIRForStmt)assign.getParent();
        ValueFlowMap<V> flow = getCurrentInSet();
        LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        //set the loop var
        //TODO - do we have to check whether colon is overloaded? - some initial checks say no, it cannot be overloaded
        //TODO - should we just call builtin colon propagator?
        if (node.hasIncr()){ //there's an inc value
            for (LinkedList<V> list : cross(flow,
                    node.getLowerName(),node.getIncName(),node.getUpperName())){
                results.add(Res.newInstance(
                		factory.forRange(list.getFirst(), list.getLast(), list.get(1))));
            }
        } else { //there's no inc value
            for (LinkedList<V> list : cross(flow,
                    node.getLowerName(),node.getUpperName())){
                results.add(Res.newInstance(
                		factory.forRange(list.getFirst(),list.getLast(),null)));
                if (Debug) System.out.println("inside intraprocedural value analysis loop case else, the results are "+results);
            }
        }
        //put results
        setCurrentOutSet(assign(flow,node.getLoopVarName().getID(), Res.newInstance(results)));
        associateInAndOut(node);
    }
    
    
    @Override
    public void caseIfCondition(Expr condExpr) {
        if (checkNonViable(condExpr)) return;
    	if(Debug) System.out.println("inside caseIfCondition!");
        ValueFlowMap<V> current = getCurrentInSet();
        ValueSet<V> values = current.get(((NameExpr)condExpr).getName().getID());
        for (V value : values){
        	//call 'any' on the condition value
            Constant any = Builtin.Any.getInstance().visit(
            		ConstantPropagator.<V>getInstance(),
            		Args.newInstance(value));
            if (any != null && any instanceof LogicalConstant){
                if (((LogicalConstant)any).equals(Constant.get(true))){
                    //result is true - false set is not viable
                    setTrueFalseOutSet(current, ValueFlowMap.<V>newInstance(false));
                } else {
                    //result is false - true set is not viable
                    setTrueFalseOutSet(ValueFlowMap.<V>newInstance(false), current);
                }
                return;
            }
        }
        setTrueFalseOutSet(current, current);
        if (Debug) System.out.println("end of caseIfCondition!");
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case array get: "+node.getPrettyPrinted());
        ValueFlowMap<V> flow = getCurrentInSet(); //note copied!
        Callsite<IntraproceduralValueAnalysis<V>,Args<V>,Res<V>> callsite = null; //used if there is a call in this stmt
        //if (Debug) System.out.println(flow);
        ValueSet<V> array = flow.get(node.getArrayName().getID());
        if (array == null) throw new UnsupportedOperationException("attempting to access unknown array "+node.getArrayName().getID()+" in\n"+node.getPrettyPrinted()+"\n with flow "+flow);
        LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        //go through all possible array values
        for (V arrayValue : array){
            if (arrayValue instanceof MatrixValue){
                if (node.getIndizes().size() == 0){
                    results.add(Res.newInstance(ValueSet.newInstance(arrayValue)));
                } else {
                    //go through all possible index setss
                    //TODO - deal with overloading of subsref etc.
                    //TODO - errors on assign - use is assign to var??
                    for (List<V> indizes : cross(flow,node.getIndizes())){
                        results.add(Res.newInstance(arrayValue.arraySubsref(Args.newInstance(indizes))));
                    }
                }
            } else if (arrayValue instanceof FunctionHandleValue){
                //go through all function handles this may represent and get the result
                //TODO - make this independent of the specific function handle value implementation
                for (FunctionHandleValue.FunctionHandle handle :((FunctionHandleValue<?>)((Object)arrayValue)).getFunctionHandles()){
                	if (callsite == null){ //create new callsite object if needed
                		callsite = this.node.createCallsiteObject(node);
                	}
                    results.add(call(
                          handle.getFunction(), flow, node.getIndizes(), node.getTargets(), callsite, (List<ValueSet<V>>)(List<?>)handle.getPartialValues(),handle.getFunction().getName(),false));
                    //TODO - we assume overloading is no possible for a function handle value - is that Matlab semantics?
                }
            } else {
                //TODO more possible values here
                throw new UnsupportedOperationException("array get received unknown value "+arrayValue);
            }
        }
        
        //put result assign/set flowsets
        setCurrentOutSet(assign(flow, node.getTargets(), Res.newInstance(results)));
        associateInAndOut(node);
    }
    
    //TODO make it deal with overloading properly?
    @Override
    public void caseTIRAbstractCreateFunctionHandleStmt(TIRAbstractCreateFunctionHandleStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case assign f_handle: "+node.getPrettyPrinted());
        //find var and remove
        String targetName = node.getTargetName().getID();
        ValueFlowMap<V> flow = getCurrentInSet();
        
        //find the function handle
        FunctionReference f = 
            function.getCalledFunctions().get(node.getFunctionName().getID());
        
        //get enclosed workspace - the set of values already assigned
        List<Name> enclosedNames = new ArrayList<Name>();
        if (node instanceof TIRCreateLambdaStmt){
        	enclosedNames = ((TIRCreateLambdaStmt)node).getEnclosedVars();
        }
        LinkedList<ValueSet<V>> enclosedValues = new LinkedList<ValueSet<V>>();
        for (Name var : enclosedNames){
            enclosedValues.add(flow.get(var.getID()));
        }
        
        //assign result
        setCurrentOutSet(assign(flow, targetName, 
                Res.newInstance(factory.newFunctionHandlevalue(f, enclosedValues))));
        
        //set in/out set
        associateInAndOut(node);
    }
    
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case array set: "+node.getPrettyPrinted());
        //find vars
        ValueFlowMap<V> flow = getCurrentInSet();
        String targetName = node.getArrayName().getID();
        ValueSet<V> targets = flow.get(targetName);
        ValueSet<V> values = flow.get(node.getValueName().getID());
        List<LinkedList<V>> indizesList = cross(flow,node.getIndizes());
        
        //if target is undefined yet, define it
        if (targets == null){
            //for now just use the value... FIXME
            targets = values;
        }
        if (values == null){
            throw new UnsupportedOperationException("bad array set "+node.getPrettyPrinted()+"\n"+targetName+","+targets+","+values+"\n"+flow+"\n"+function);
        }
        
        //assign all combinations
        LinkedList<V> results = new LinkedList<V>();
        for (V value : values){
            for (LinkedList<V> indizes : indizesList){
                Args<V> is = Args.newInstance(indizes);
                for (V target : targets){
                    results.add(target.arraySubsasgn(is, value));
                }
            }
        }
        
        //assign result
        setCurrentOutSet(assign(flow, targetName, Res.newInstance(ValueSet.newInstance(results))));
        
        //set in/out set
        associateInAndOut(node);
    }
    
    @Override
    public void caseTIRAssignLiteralStmt(TIRAssignLiteralStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case assign literal: "+node.getPrettyPrinted());
        //get literal and make constant
        Constant constant = Constant.get(node.getRHS());

        //put in flow map
        ValueFlowMap<V> flow = getCurrentInSet();
        String targetName = node.getTargetName().getID();
        
        /*
         * this comment and code below modified added by XU @ 6:36pm March 9th 2013
         * this targetName can be used as a symbolic value of this target variable, 
         * so I passed this symbolic info to newValueSet method below.
         */

        //assign result
        setCurrentOutSet(assign(flow,targetName, 
                Res.newInstance(factory.newValueSet(targetName, constant))));

        //set in/out set
        associateInAndOut(node);
    }
    
    @Override
    public void caseTIRCopyStmt(TIRCopyStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case copy: "+node.getPrettyPrinted());

        ValueFlowMap<V> flow = getCurrentInSet();
        String targetName = node.getTargetName().getID();
        ValueSet<V> valueSet = flow.get(node.getSourceName().getID());
        
        //assign result
        setCurrentOutSet(assign(flow, targetName, Res.newInstance(valueSet)));
        
        //set in/out set
        associateInAndOut(node);
        if (Debug) System.out.println("after copy: "+flow);
    }
    
    
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case dot get: "+node.getPrettyPrinted());

        //get variable, field, flow
        ValueFlowMap<V> flow = getCurrentInSet();
        String objName = node.getDotName().getID();
        String field = node.getFieldName().getID();
        
        LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        
        //TODO check if var exists
        //loop through all possible values
        for (Value<V> v : flow.get(objName)){
            results.add(Res.newInstance(v.dotSubsref(field)));
        }
        //assign result
        setCurrentOutSet(assign(flow, node.getTargets(),Res.newInstance(results)));

        //set in/out set
        associateInAndOut(node);
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case dot set: "+node.getPrettyPrinted());

        //get variable, field, flow
        ValueFlowMap<V> flow = getCurrentInSet();
        String objName = node.getDotName().getID();
        String field = node.getFieldName().getID();
        String rhs = node.getValueName().getID();
        
        LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        //loop through all possible rhs
        for (V v : flow.get(rhs)){
            //check if var exists - TODO this might change
            if (isVarAssigned(flow, objName)){
                //go through all possible objects
                for (V obj : flow.get(objName)){
                    results.add(Res.newInstance(obj.dotSubsasgn(field, v)));                    
                }
            } else {
                results.add(Res.newInstance(factory.newStruct().dotSubsasgn(field, v)));
            }
        }

        //assign result
        setCurrentOutSet(assign(flow, objName, Res.newInstance(results)));

        //set in/out set
        associateInAndOut(node);
    }
    
    
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node) {
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case cell array get: "+node.getPrettyPrinted());
        ValueFlowMap<V> flow = getCurrentInSet();
        //if (Debug) System.out.println(flow);
        ValueSet<V> array = flow.get(node.getCellArrayName().getID());
        if (array == null) throw new UnsupportedOperationException("attempting to access unknown cell array "+node.getCellArrayName().getID()+" in\n"+node.getPrettyPrinted()+"\n with flow "+flow);
        LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        //go through all possible array values
        for (V arrayValue : array){
            //go through all possible index setss
            for (List<V> indizes : cross(flow,node.getIndices())){
                //TODO - deal with overloading etc.
                results.add(arrayValue.cellSubsref(Args.newInstance(indizes)));
            }
        }
        
        //put result assign/set flowsets
        setCurrentOutSet(assign(flow, node.getTargets(), Res.newInstance(results)));
        associateInAndOut(node);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node) {     
        if (checkNonViable(node)) return;
        if (Debug) System.out.println("case cell array set: "+node.getPrettyPrinted());
        //find vars
        ValueFlowMap<V> flow = getCurrentInSet();
        String targetName = node.getCellArrayName().getID();
        ValueSet<V> targets = flow.get(targetName);
        ValueSet<V> values = flow.get(node.getValueName().getID());
        List<LinkedList<V>> indizesList = cross(flow,node.getIndizes());
        
        //if target is undefined yet, define it
        if (targets == null){
            //TODO - call builtin cell instead
            targets = ValueSet.newInstance(factory.newCell());
        }
        if (values == null){
            //TODO return error value
            throw new UnsupportedOperationException("bad array set "+node.getPrettyPrinted()+"\n"+targetName+","+targets+","+values+"\n"+flow);
        }
        
        //assign all combinations
        LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        for (V value : values){
            for (LinkedList<V> indizes : indizesList){
                Args<V> is = Args.newInstance(indizes);
                for (V target : targets){
                    results.add(Res.newInstance(target.cellSubsasgn(is,Args.newInstance(value))));
                }
            }
        }
        
        //assign result
        setCurrentOutSet(assign(flow, node.getCellArrayName().getID(), Res.newInstance(results)));
        associateInAndOut(node);
    }
    
    @Override
    public void caseTIRCommentStmt(TIRCommentStmt node) {
    	//TODO - do we need something here?
    }
    
    @Override
    public void caseStmt(Stmt node) {
        if (checkNonViable(node)) return;
        //if (Debug)
        if (Debug) System.out.println("IntraproceduralValueAnalysis: Stmt "+node+"-"+node.getPrettyPrinted());
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
    private List<LinkedList<V>> cross(ValueFlowMap<V> flow,TIRCommaSeparatedList args){
        return cross(flow,args,null);
    }
    
    /**
     * given an IRCommaSeparated list and a flow set, returns all possible combinations
     * of values as a list of lists
     * partial values will be prepended
     */
    private List<LinkedList<V>> cross(ValueFlowMap<V> flow,TIRCommaSeparatedList args,List<ValueSet<V>> partialValues){
        //if (Debug)System.out.println("cross - flow: "+flow+" args: "+args);
        //get list of value sets from the names        
        ArrayList<ValueSet<V>> values;
        if (partialValues == null){
            values = new ArrayList<ValueSet<V>>(args.size());
        } else {
            values = new ArrayList<ValueSet<V>>(args.size()+partialValues.size());
            values.addAll(partialValues);
        }
        for (Expr expr : args){
            if (expr instanceof NameExpr){
                String name = ((NameExpr)expr).getName().getID();
                ValueSet<V> vs = flow.get(name);
                if (vs == null){
                    if (Debug) System.out.println(function.toString());
                    throw new UnsupportedOperationException("name "+name+" not found in "+flow);
                }
                values.add(vs);
            } else if (expr instanceof ColonExpr){
                values.add(ValueSet.newInstance(factory.newColonValue()));
            } else {
                throw new UnsupportedOperationException("received bad arg set "+args);
            }
        }
        return ValueSet.cross(values);
    }
    
    /**
     * returns ValueSet.cross but using valueSets from the given flowmap, using Names
     */
    private List<LinkedList<V>> cross(ValueFlowMap<V> flow, Name... args){
        LinkedList<ValueSet<V>> list = new LinkedList<ValueSet<V>>();
        for (Name arg : args){
            list.add(flow.get(arg.getID()));
        }
        return ValueSet.cross(list);
    }
    
    
    /**
     * implements the flow equations for calling functions
     * Returns a Result, doesn't modify anything
     * 
     * the function reference is used for overloading, the name is the
     * name of the function that gets called (for overloading and error reporting purposes)
     */
    private Res<V> call(
            FunctionReference functionReference,ValueFlowMap<V> flow,
            TIRCommaSeparatedList args,TIRCommaSeparatedList targets,
            Callsite<IntraproceduralValueAnalysis<V>,Args<V>,Res<V>> callsite,
            List<ValueSet<V>> partialArgs, String functionName, boolean checkOverloading){
    	
    	//get number of requested results
    	int numOfOutputVariables = 1;
    	if (callsite.getASTNode() instanceof TIRAbstractAssignToListStmt){                	
    		numOfOutputVariables = ((TIRAbstractAssignToListStmt)callsite.getASTNode()).getNumTargets();
    		//XU added here, to pass number of output variables to the equation propagator/analysis
    	}
    	
    	LinkedList<Res<V>> results = new LinkedList<Res<V>>();
        if (Debug) System.out.println("calling function "+function+" with\n"+cross(flow,args,partialArgs));
        for (LinkedList<V> argumentList : cross(flow,args,partialArgs)){
        	FunctionReference function = functionReference;
        	
        	//check overloading
        	if (checkOverloading && argumentList.size() > 0){
        		//find the dominant argument
        		ClassReference dominant = argumentList.getFirst().getMatlabClass();
        		for (V arg : argumentList){
        			if (classRepository.getClass(arg.getMatlabClass()).isSuperior(classRepository.getClass(dominant))){
        				dominant = arg.getMatlabClass();
        			}
        		}
        		//overload if there's an overloaded version
        		FunctionReference ref = classRepository.getClass(dominant).getMethods().get(functionName);
        		if (ref != null){
        			function = ref;
        		}        	
        	}
        	
        	//give an error if function call cannot be resolved
        	if (function == null){
        		//TODO - make proper errors
        		throw new UnsupportedOperationException(
        				"call to function "+functionName+" in function call "+this.node.getCall()+" cannot be resolved. trace: \n"
        				+this.node.getCallString());
        	}
        	
        	//actually call
            //System.out.println("doFunctionCall result "+res);
        	if (function.isBuiltin()) {
            	if (functionName.equals("horzcat") || functionName.equals("vertcat")) {
            		LinkedList<V> hookList = new LinkedList<V>();
                	for (Name arg : args.asNameList()) {
                		hookList.add(flow.get(arg.getID()).getSingleton());
                	}
            		hookMap.put(targets.getName(0).getID(), hookList);
            	}
            	Args<V> argsObj;
            	if ((functionName.equals("ones") || functionName.equals("zeros") || functionName.equals("rand"))
            			&& args.size()==1 && hookMap.containsKey(args.asNameList().get(0).getID())) {
            		argsObj = Args.newInstance(dependentVars, numOfOutputVariables
            				, hookMap.get(args.asNameList().get(0).getID()));
            	}
            	else {
            		argsObj = Args.newInstance(dependentVars,numOfOutputVariables,argumentList);
            	}
                //spcial cases for some known functions
            	callsite.addBuiltinCall(new Call<Args<V>>(function, argsObj));
                if (function.getName().equals("nargin") && argsObj.size() == 0){
                	// changed by XU @ 6:41pm March 9th 2013, TODO unchecked!
                    results.add(Res.newInstance(factory.newMatrixValue(null, argMap.size())));
                } else {
                	if (Debug) System.out.println("calling propagatpr with argument "+argsObj);
                	results.add(valuePropagator.call(function.getName(), argsObj));
                }
            }else{
                //simplify args
                ArrayList<V> newArgumentList = new ArrayList<V>(argumentList);
                for (int i = 0; i < newArgumentList.size(); i++){
                    newArgumentList.set(i, newArgumentList.get(i).toFunctionArgument(false));
                }
                Args<V> argsObj = Args.newInstance(newArgumentList);
                //simplify again if its recursive
                //TODO - rethink this - if a call is recursive, force it to be even if the arg ist different?
                if (node.getCallString().contains(function, argsObj)){
                    newArgumentList = new ArrayList<V>(argumentList);
                    for (int i = 0; i < newArgumentList.size(); i++){
                        newArgumentList.set(i, newArgumentList.get(i).toFunctionArgument(true));
                    }
                    argsObj = Args.newInstance(newArgumentList);
                }
                //perform analysis for call
                results.add(node.analyze(function, argsObj, callsite));
            }
        }        
        if (Debug) System.out.println("called "+function+", received "+Res.newInstance(results));
        if (cross(flow,args,partialArgs).size() > 1){
        	if (Debug) System.out.println("exiting");
        	if (Debug) System.out.println(results);
        	//System.exit(0);
        }
        //FIXME
        return Res.newInstance(results);
    }
    

    /**
     * assigns the given collection of values, to the targets represented
     * by the comma separated list. Returns a new flowmap which is a copy
     * of old one, except for the newly assigned valeus.
     * 
     * all assignments should go through assign calls
     * TODO - should the third argument be a ValueSet?
     */
    private ValueFlowMap<V> assign(ValueFlowMap<V> flow, 
            String target, Res<V> values){
        return assign(flow,new TIRCommaSeparatedList(new NameExpr(new Name(target))),values);
    }
    
    /**
     * assigns the given collection of values, to the targets represented
     * by the comma separated list. Returns a new flowmap which is a copy
     * of old one, except for the newly assigned valeus.
     * 
     * all assignments should go through assign calls
     * 
     * TODO - should this be part of ValueFlowMap? Or maybe a helper
     * TODO - should we just assign Res<V>?
     */
    private ValueFlowMap<V> assign(ValueFlowMap<V> flow, 
            TIRCommaSeparatedList targets, Res<V> values){
       if (Debug) System.out.println("assign: "+targets+" = "+values);
       ValueFlowMap<V> result = flow.copy();
       if (!values.isViable()){
           return ValueFlowMap.newInstance(false);
       }
       if (targets.isAllNameExpressions()){
           Iterator<ValueSet<V>> iValues = values.iterator();
           Iterator<Name> iNames = targets.asNameList().iterator();
           while (iNames.hasNext()){
        	   if (!iValues.hasNext()){
        		   throw new UnsupportedOperationException(
        				   "Not enough values produced for assignment in function call "
        						   +this.node.getCall()+" cannot be resolved. trace: \n"
        						   +this.node.getCallString());
        	   }
               result.put(iNames.next().getID(), iValues.next());
           }
       } else {
           throw new UnsupportedOperationException("no support for non-primitive assings");
       }
	   if (Debug) System.out.println(result);
       return result;
    }
    
    
    @Override
    public Res<V> getDefaultResult() {
        return Res.newInstance(false);
    }
    
    
    @Override
    public TIRFunction getTree() {
        return (TIRFunction)this.function.getAst();
    }
    
    
    /**
     * returns true if the given variable name is assigned in the given flow set
     */
    private boolean isVarAssigned(ValueFlowMap<V> flow,String varName){
        return (flow.containsKey(varName) && (flow.get(varName).size > 0));
    }    
    
    
    /**
     * checks whether the current flow set is not viable. If it isn't, associate
     * nonviable in/out sets, and return true. Thus, every statement case should
     * start with
     *  if (checkNonViable(node)) return;
     */
    private boolean checkNonViable(ASTNode<?> node){
        if (Debug){
            System.out.println("==== analyzing "+node+": "+node.getPrettyPrinted());
            System.out.println("flowset "+getCurrentInSet());
        }
        if (getCurrentInSet().isViable()) return false;
        setCurrentOutSet(getCurrentInSet().copy());
        associateInAndOut(node);
        return true;
    }
}





