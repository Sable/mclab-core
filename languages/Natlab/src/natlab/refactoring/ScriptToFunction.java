package natlab.refactoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import natlab.refactoring.Exceptions.IDConflictException;
import natlab.refactoring.Exceptions.RefactorException;
import natlab.toolkits.ParsedCompilationUnitsContextStack;
import natlab.toolkits.analysis.core.Def;
import natlab.toolkits.analysis.core.LivenessAnalysis;
import natlab.toolkits.analysis.core.ReachingDefs;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFFlowInsensitiveAnalysis;
import natlab.toolkits.filehandling.GenericFile;
import natlab.utils.NodeFinder;
import ast.CompilationUnits;
import ast.ExprStmt;
import ast.Function;
import ast.FunctionList;
import ast.Name;
import ast.NameExpr;
import ast.Script;

public class ScriptToFunction {
	ParsedCompilationUnitsContextStack context;
	Set<Script> scripts;
	public Map<Script, Integer> inputCount = new HashMap<Script, Integer>();
	public Map<Script, Integer> outputCount = new HashMap<Script, Integer>();
	public ScriptToFunction(CompilationUnits cu){
		context = new ParsedCompilationUnitsContextStack(new LinkedList<GenericFile>(), cu.getRootFolder(), cu);
		scripts = NodeFinder.find(Script.class, cu).collect(Collectors.toSet());
	}
	
	public Map<Script, List<RefactorException>> replaceAll(){
		final Map<Script, List<RefactorException>> map =  new HashMap<Script, List<RefactorException>>();
		for (Script s: scripts){
			Function f = new Function();
			map.put(s, replace(s, f));
			inputCount.put(s, f.getNumInputParam());
			outputCount.put(s, f.getNumOutputParam());
		}
		return map;
	}
	
	public List<RefactorException> replace(final Script script, Function f){
		System.out.println("Script Name: " + script.getName());
		List<ExprStmt> calls = NodeFinder.find(ExprStmt.class, context.cu)
		    .filter(node -> node.getExpr() instanceof NameExpr)
		    .filter(node -> ((NameExpr) node.getExpr()).getName().getID().equals(script.getName()))
		    .collect(Collectors.toList());
		f.setStmtList(script.getStmtList().copy());
		FunctionList fl = new FunctionList();
		fl.addFunction(f);
		fl.setFile(script.getFile());
		f.setParent(fl.getFunctionList());
		LivenessAnalysis live = new LivenessAnalysis(script);
		VFFlowInsensitiveAnalysis scriptKind = new VFFlowInsensitiveAnalysis(script);
		ReachingDefs scriptReaching = new ReachingDefs(script);
		live.analyze(); 
		scriptKind.analyze();
		scriptReaching.analyze();
		
		Set<String> scriptLiveVars = live.getOutFlowSets().get(script);
		Set<String> assignedVars = new HashSet<String>();
		for (Map.Entry<String, Set<Def>> entry: scriptReaching.getOutFlowSets().get(script).entrySet()){
			if (entry.getValue().contains(ReachingDefs.UNDEF) || entry.getValue().isEmpty())
				continue;
			assignedVars.add(entry.getKey());
		}
//		Set<String> shouldNotBeAssigned = new HashSet<String>();
		List<RefactorException> errors = new LinkedList<RefactorException>();
		TreeSet<String> inputArgs = null;
		TreeSet<String> outputArgs = null;
        boolean inlined = false;

		System.out.println(script.getPrettyPrinted() + "\nAssigned:" + assignedVars + "\nLive:" +scriptLiveVars + " Calls: " + calls); 
        
		for (ExprStmt call: calls) {
			Name name = ((NameExpr)call.getExpr()).getName();
			Function callFunc = NodeFinder.findParent(Function.class, call);
			if (callFunc == null){ //called inside script
				errors.add(new Exceptions.ScriptCalledFromScript(name));
				return errors;
			}
			//int offset = NodeFinder.find(callFunc, ExprStmt.class).indexOf(call);
            //            callFunc = callFunc.fullCopy();
            //		call = NodeFinder.find(callFunc, ExprStmt.class).get(offset);
            
			//MScriptInliner scriptInliner = new MScriptInliner(context.cu);
			//for (ExprStmt toInline : NodeFinder.find(callFunc, ExprStmt.class)){
            //		if (toInline != call){
            //       if (toInline.getChild(0) instanceof NameExpr)
                      
                        // scriptInliner.inlineStmt(callFunc, toInline, new LinkedList<Exception>());
            //         inlined = true;
            //                }
            //}
            if (inlined) {
                System.out.println(name+ "\n"+ callFunc.getPrettyPrinted());
            }
			TreeSet<String> inputArgsCurrent = new TreeSet<String>();
			TreeSet<String> outputArgsCurrent = new TreeSet<String>();
			ReachingDefs mayReach = new ReachingDefs(callFunc);
			LivenessAnalysis callLive = new LivenessAnalysis(callFunc);
			mayReach.analyze();
			callLive.analyze();
			Set<String> callLiveVars = callLive.getOutFlowSets().get(call);
			Map<String, Set<Def>> reaching = mayReach.getInFlowSets().get(call);
            System.out.println("\ncallReaching:" + reaching + "\ncLive:" +callLiveVars); 
            if (inlined) {
                System.out.println();
            }

			for (Map.Entry<String, Set<Def>> entry: reaching.entrySet()){
				if (entry.getValue() != null){
                    Set<Def> reached = entry.getValue();
					 if ((reached.size()>1 || (!reached.contains(ReachingDefs.UNDEF))) && scriptLiveVars.contains(entry.getKey()))
						 inputArgsCurrent.add(entry.getKey());
				}
				if (callLiveVars.contains(entry.getKey()) && assignedVars.contains(entry.getKey())){
					outputArgsCurrent.add(entry.getKey());
				}
					
			}
			if (inputArgs == null){
				inputArgs = inputArgsCurrent;
			}
			else if (!inputArgs.equals(inputArgsCurrent)){
				inputArgs.removeAll(inputArgsCurrent);
					errors.add(new Exceptions.ScriptInputsNotMatching(new Name(inputArgs.toString())));
			}
			
			if (outputArgs == null){
				outputArgs = outputArgsCurrent;
			}
			else if (!outputArgs.equals(outputArgsCurrent)){
				outputArgs.addAll(outputArgsCurrent);
				errors.add(new Exceptions.ScriptOutputsNotMatching(new Name(outputArgs.toString())));
			}
		}
        System.out.println("" + inputArgs + " " + outputArgs);
		if (inputArgs!=null)
			for (String inputArg: inputArgs){
				f.addInputParam(new Name(inputArg));
			}
		
		if (outputArgs!=null)
			for (String outputArg: outputArgs){
				f.addOutputParam(new Name(outputArg));
			}

        if (inputArgs == null){
            errors.add(new Exceptions.NoCallsToScript(new Name(script.getName())));
        }
		VFFlowInsensitiveAnalysis funcKind = new VFFlowInsensitiveAnalysis(f);
        if (inlined){
            System.out.println(f.getPrettyPrinted());
            //            System.exit(0);
        }
		funcKind.analyze();
		Map<String, VFDatum> funcRes = funcKind.getFlowSets().get(f);
		for (Map.Entry<String, VFDatum> entry: scriptKind.getFlowSets().get(script).entrySet()){
		    VFDatum kind = funcRes.get(entry.getKey());
		    if (kind == null) kind = VFDatum.UNDEF;
			if (entry.getValue()!=null && entry.getValue().isID() && kind.isID())
				errors.add(new IDConflictException(new Name(entry.getKey())));
            if (entry.getValue()!=null && entry.getValue().isID() && kind.isFunction())
				errors.add(new Exceptions.WarnIDToFunException(new Name(entry.getKey())));
            if (kind==VFDatum.TOP)
                errors.add(new Exceptions.KindConflict(new Name(entry.getKey())));
		}
		return errors;
	}
}
