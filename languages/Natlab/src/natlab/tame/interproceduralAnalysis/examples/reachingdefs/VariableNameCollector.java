package natlab.tame.interproceduralAnalysis.examples.reachingdefs;

import java.util.*;
import java.util.List;

import ast.*;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.interproceduralAnalysis.examples.live.LiveValue;
import natlab.tame.tir.*;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;

public class VariableNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> implements FunctionAnalysis<StaticFunction, HashSetFlowSet<String>>
{
    protected boolean inLHS = false;
    protected HashSetFlowSet<String> fullSet = new HashSetFlowSet<String>();
    protected HashSetFlowSet<String> currentSet;
    protected Map<TIRNode, HashSetFlowSet<String>> flowSets = new HashMap<TIRNode, HashSetFlowSet<String>>();
    
    public VariableNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fullSet = new HashSetFlowSet<String>();
    }
    
    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }
    
    @Override
    public HashSetFlowSet<String> getResult()
    {
        analyze();
        return fullSet;
    }
    
    /**
     * return the result if wasn't computed yet - for recursive calls
     */
    @Override
    public HashSetFlowSet<String> getDefaultResult() 
    {
        throw new UnsupportedOperationException("live variable analysis example doesn't support recursive programs");
    }
    
    @Override
    public Function getTree() 
    {
        return (Function)super.getTree();
    }
    
    //******* case methods ***************************************************
   
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       currentSet = newInitialFlow();
       IRCommaSeparatedListToVaribaleNamesSet(((TIRAbstractAssignToListStmt)node).getTargets(), currentSet);
       flowSets.put(node, currentSet);
       fullSet.addAll(currentSet);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
        currentSet = newInitialFlow();
        TIRCommaSeparatedList declaredVariablesList = null;
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            Name target;
            if (node instanceof TIRArraySetStmt)
            {
                target = ((TIRArraySetStmt)node).getArrayName();
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                target = ((TIRCellArraySetStmt)node).getCellArrayName();
            }
            else if (node instanceof TIRDotSetStmt)
            {
                target = ((TIRDotSetStmt)node).getDotName();
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
            declaredVariablesList = new TIRCommaSeparatedList(new NameExpr(target));
        }
        else if (node instanceof TIRAbstractAssignToListStmt)
        {
            declaredVariablesList = ((TIRAbstractAssignToListStmt)node).getTargets();
        } 
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            declaredVariablesList = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement "+node);
        }
        IRCommaSeparatedListToVaribaleNamesSet(declaredVariablesList, currentSet);
        flowSets.put(node, currentSet);
        fullSet.addAll(currentSet);
    }
    
    public void IRCommaSeparatedListToVaribaleNamesSet(TIRCommaSeparatedList csl, HashSetFlowSet<String> variableNames)
    {
        for(Name name : csl.asNameList())
        {
            variableNames.add(name.getID());
        }
    }

    @Override
    public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2,
            HashSetFlowSet<String> out)
    {
        out.addAll(in1);
        out.addAll(in2);
    }

    @Override
    public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest)
    {
        dest = source.copy();
    }
}
