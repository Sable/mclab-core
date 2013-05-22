package natlab.tame.interproceduralAnalysis.examples.live;

import java.util.List;
import java.util.*;

import ast.*;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.*;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralBackwardAnalysis;


/**
 * the intraprocedural analysis upon which the interprocedural value analysis is built
 */
public class IntraproceduralLiveVariableAnalysis 
   extends TIRAbstractSimpleStructuralBackwardAnalysis<Map<String,LiveValue>> 
   implements FunctionAnalysis<LiveInput, List<LiveValue>>{
	LiveInput inputs;
	List<LiveValue> result;
	Map<String,LiveValue> inputMap;
	StaticFunction function;
	InterproceduralAnalysisNode<IntraproceduralLiveVariableAnalysis, LiveInput, List<LiveValue>> interProcNode;

	//******** constructor - gets called by the factory in LiveFariableAnalysis *****************************
	protected IntraproceduralLiveVariableAnalysis(
		    InterproceduralAnalysisNode<IntraproceduralLiveVariableAnalysis, LiveInput, List<LiveValue>> node,
		    LiveInput inputs) {
		super(node.getFunction().getAst());
		this.inputs = inputs;
		this.function = node.getFunction();
		this.interProcNode = node;
		//build input map
		inputMap = new HashMap<String,LiveValue>();
		for (int i = 0; i < inputs.size(); i++){
			//TODO - check whether too many outputs are required?
			inputMap.put(
					function.getAst().getOutputParamList().getChild(i).getID(),
					LiveValue.getLive());
		}
	}

	
	//******** inherited Functionanalysis methods ******************************
	@Override
	public List<LiveValue> getResult() {
		if (!isAnalyzed()) analyze();
		//build result list (those are the live input parameters)
		if (result == null){
			result = new ArrayList<LiveValue>();
			Map<String,LiveValue> resultMap = getOutFlowSets().get(function.getAst());
			for (Name argName : function.getAst().getInputParamList()){
				String arg = argName.getID();
				if (resultMap.containsKey(arg)){
					result.add(resultMap.get(arg));
				} else {
					result.add(LiveValue.getDead());
				}
			}
		}
		return result;
	}

	/**
	 * return the result if wasn't computed yet - for recursive calls
	 */
	@Override
	public List<LiveValue> getDefaultResult() {
		throw new UnsupportedOperationException("live variable analysis example doesn't support recursive programs");
	}
	
	@Override
	public Function getTree() {
		return (Function)super.getTree();
	}
	
	//*** inherited mcsaf functions **************************************************
	@Override
	public void copy(Map<String, LiveValue> source, Map<String, LiveValue> dest) {
		for (Map.Entry<String,LiveValue> entry : source.entrySet()){
			dest.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Map<String, LiveValue> newInitialFlow() {
		return new HashMap<String, LiveValue>();
	}

	@Override
	public void merge(Map<String, LiveValue> in1, Map<String, LiveValue> in2,
			Map<String, LiveValue> out) {
		//fill in everything from in1
		for (String name : in1.keySet()){
			if (in2.containsKey(name)){
				out.put(name,in1.get(name).merge(in2.get(name)));
			} else {
				out.put(name,in1.get(name).merge(LiveValue.getDead()));
			}
		}
		//fill in the missing elements from in2
		for (String name : in2.keySet()){
			if (!in1.containsKey(name)){
				out.put(name, in2.get(name).merge(LiveValue.getDead()));
			}
		}
	}
	
	//******* case methods ***************************************************
	@Override
	public void caseFunction(Function node) {
        setCurrentOutSet(inputMap);
        caseASTNode(node);
        setInOutSet(node);
	}
	
	/*
	@Override
	public void caseTIRCopyStmt(TIRCopyStmt node) {
		Map<String,LiveValue> map = copyInSet();
		map.remove(node.getTargetName().getID());
		map.put(node.getSourceName().getID(),LiveValue.getLive());
        setCurrentInSet(map);
		setInOutSet(node);
	}*/

	@Override
	public void caseTIRCallStmt(TIRCallStmt node) {
		Map<String,LiveValue> map = copyInSet();
		map = call(inputs.getNode().getCallsite(node),
		        node.getArguments(),node.getTargets(), map); // MADE A CHANGE HERE
		System.out.println("result map "+map);
        setCurrentInSet(map);
		setInOutSet(node);
	}
	
	@Override
	public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
		Map<String,LiveValue> map = copyInSet();
		//check whether there is a call in this statement
		if (inputs.getNode().getCallsite(node) != null){
			//TODO implement this
			throw new UnsupportedOperationException("live variable analysis doesn't support calls outside of call stmts");
		}
		//get list of written variables/read variables
		TIRCommaSeparatedList read = null, write = null;
		if (node instanceof TIRAbstractAssignFromVarStmt){
			Name target;
			if (node instanceof TIRArraySetStmt){
				target = ((TIRArraySetStmt)node).getArrayName();
				read = ((TIRArraySetStmt)node).getIndizes();
			} else if (node instanceof TIRCellArraySetStmt){
				target = ((TIRCellArraySetStmt)node).getCellArrayName();
				read = ((TIRCellArraySetStmt)node).getIndizes();
			} else if (node instanceof TIRDotSetStmt){
				target = ((TIRDotSetStmt)node).getDotName();
				read = new TIRCommaSeparatedList();
			} else { throw new UnsupportedOperationException("unknown assign from var stmt"+node); }
			read.add(new NameExpr(((TIRAbstractAssignFromVarStmt)node).getValueName())); //add the rhs variable
			write = new TIRCommaSeparatedList(new NameExpr(target));
		} else if (node instanceof TIRAbstractAssignToListStmt){
			write = ((TIRAbstractAssignToListStmt)node).getTargets();
			if (node instanceof TIRArrayGetStmt){
				read = ((TIRArrayGetStmt)node).getIndizes();
				read.add(new NameExpr(((TIRArrayGetStmt)node).getArrayName()));
			} else if (node instanceof TIRCellArrayGetStmt){
				read = ((TIRCellArrayGetStmt)node).getIndices();
				read.add(new NameExpr(((TIRCellArrayGetStmt)node).getCellArrayName()));				
			} else if (node instanceof TIRDotGetStmt){
				read = new TIRCommaSeparatedList(new NameExpr(((TIRDotGetStmt)node).getDotName()));
			}
		} else if (node instanceof TIRAbstractAssignToVarStmt){
			write = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
			if (node instanceof TIRCopyStmt){
				read = new TIRCommaSeparatedList(new NameExpr(((TIRCopyStmt)node).getSourceName()));
			} else if (node instanceof TIRCreateLambdaStmt){
				read = TIRCommaSeparatedList.createFromNames(((TIRCreateLambdaStmt)node).getEnclosedVars());
			}			
		} else {
			throw new UnsupportedOperationException("unknown assignment statement "+node);
		}
		//do the write operation - remove variables from list, they become dead
		if (write != null){
			for (Name name : write.asNameList()){
				map.remove(name.getID());
			}
		}
		//perform read operation
		if (read != null){
			//build array of 'live' live values
			List<LiveValue> values = new ArrayList<LiveValue>(read.size());
			for (int i = 0; i < read.size(); i++){
				values.add(LiveValue.getLive());
			}
			map = read(map, values, read);
		}
		setCurrentInSet(map);
		setInOutSet(node);
	}
	
	
	//******** helper methods ************************************************
	//associated the in set and out set with the given stmt
	private void setInOutSet(ASTNode<?> node){
	        associateInSet(node, getCurrentInSet());
	        associateOutSet(node, getCurrentOutSet());
	}
	
	private Map<String,LiveValue> copyInSet(){
		return new HashMap<String,LiveValue>(getCurrentInSet());
	}
	
	/**
	 * will analyse a call, and return a flow map that is the result of the call.
	 * inputs are the original callsite, the argument and target list, and the 
	 * map that will get modified to return the new flow map.
	 */
	private Map<String,LiveValue> call(
			Callsite<?,?,?> previousCallsite,
			TIRCommaSeparatedList args, TIRCommaSeparatedList targets,
			Map<String,LiveValue> map){
		Callsite<IntraproceduralLiveVariableAnalysis, LiveInput, List<LiveValue>> callsite =
				interProcNode.createCallsiteObject(previousCallsite.getASTNode());
		System.out.println(previousCallsite.getASTNode().getPrettyPrinted()+" "+previousCallsite.getCalls());
		//build input set
		if (!targets.isAllNameExpressions() || !args.isAllNameExpressions()) 
			throw new UnsupportedOperationException("live variable analysis only supports names on lhs/rhs");
		List<LiveValue> inputValues = new ArrayList<LiveValue>(targets.size());
		for (Name name : targets.asNameList()){
			if (map.containsKey(name.getID())){
				inputValues.add(map.get(name.getID()));
			} else {	
				inputValues.add(LiveValue.getDead()); //the value is dead			
			}
		}
		//remove the values from the input set
		for (Name name : targets.asNameList()){
			map.remove(name.getID());
		}
		//run the calls
		List<LiveValue> result = null;		
		//first the builtin calls - if there is any call, just assume all arguments are used - they become live
		System.out.println("call: "+previousCallsite);
		if (previousCallsite.getBuiltinCalls().size() > 0){
			//note that  builtin calls don't get recorded back in the callsite
			result = new ArrayList<LiveValue>(args.size());
			for (int i = 0; i < args.size(); i++) result.add(LiveValue.getLive());
		}
		System.out.println(previousCallsite);
		for (Call<?> previousCall : previousCallsite.getCalls().keySet()){
			//build input set
			LiveInput input = new LiveInput(
					previousCallsite.getCalls().get(previousCall),inputValues);
			//build call, and and analyse the call
			List<LiveValue> aResult = interProcNode.analyze(
					new Call<LiveInput>(previousCall.getFuncionReference(),input), callsite);
			//merge the result
			if (result == null){
				result = aResult;
			} else {
				merge(result,aResult);
			}
		}
		System.out.println("result "+result);
		//assign the result
		return read(map,result,args);
	}
	
	/**
	 * merges the two given result lists into the first list
	 */
	private void merge(List<LiveValue> dest, List<LiveValue> src){
		//go through all dest elements, merging
		for (int i = 0; i < dest.size(); i++){
			if (i < src.size()){ //merge if in both
				dest.set(i, dest.get(i).merge(src.get(i)));
			} else { //ran out of source elements
				dest.set(i, dest.get(i).merge(LiveValue.getDead()));
			}
		}
		//if source is longer, add those missing elements
		if (src.size() > dest.size()){
			for (int i = dest.size(); i < src.size(); i++){
				dest.add(src.get(i).merge(LiveValue.getDead()));
			}
		}
	}
	
	/**
	 * assigns the live values in the value list to the variables int he comma separated list,
	 * returning an updated flow map.
	 * TODO - at some point there may be some special merging when a read is happening, right
	 * now all values just get overriden except if they are dead
	 */
	private Map<String,LiveValue> read(Map<String,LiveValue> map, List<LiveValue> values, TIRCommaSeparatedList variables){
		List<Name> params = variables.asNameList();
		for (int i = 0; i < values.size(); i++){
			String name = params.get(i).getID();
			if (map.containsKey(name)){ //if the variable is in the map, we need to update (reread) the value
				map.put(name, map.get(name).reRead(values.get(i)));
			} else { //...otherwise we just put it in as-is
				map.put(name, values.get(i));
			}
		}
		return map;
	}
	
	//***** public functions *****************************************************
	public InterproceduralAnalysisNode<?,?,?> getPreviousAnalysisNode(){
		return inputs.getNode();
	}
}



