package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ast.ASTNode;
import ast.Function;
import ast.Name;
import ast.NameExpr;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;

public class UsedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> implements FunctionAnalysis<StaticFunction, HashSetFlowSet<String>>
{
    private HashSetFlowSet<String> fCurrentSet;
    public Map<TIRNode, HashSetFlowSet<String>> fFlowSets = new HashMap<TIRNode, HashSetFlowSet<String>>();
    private StaticFunction fFunction;
    
    public UsedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fFunction = null;
    }
    
    public UsedVariablesNameCollector(StaticFunction f)
    {
        super(f.getAst());
        fFunction = f;
    }
    
    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentSet = newInitialFlow();
       IRCommaSeparatedListToVaribaleNamesSet(node.getArguments(), fCurrentSet);
       fFlowSets.put(node, fCurrentSet);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
        fCurrentSet = newInitialFlow();
        TIRCommaSeparatedList usedVariablesList = null;
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            // Name target;
            if (node instanceof TIRArraySetStmt)
            {
               // target = ((TIRArraySetStmt)node).getArrayName();
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
               // target = ((TIRCellArraySetStmt)node).getCellArrayName();
            }
            else if (node instanceof TIRDotSetStmt)
            {
               // ((TIRDotSetStmt)node).getDotName();
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
           // usedVariablesList = new TIRCommaSeparatedList(new NameExpr(target));
        }
        else if (node instanceof TIRAbstractAssignToListStmt)
        {
          //  usedVariablesList = ((TIRAbstractAssignToListStmt)node).getTargets();
        } 
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            Name usedVarName = new Name(node.getRHS().getVarName());
            usedVariablesList = new TIRCommaSeparatedList(new NameExpr(usedVarName));
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement "+node);
        }
        IRCommaSeparatedListToVaribaleNamesSet(usedVariablesList, fCurrentSet);
        fFlowSets.put(node, fCurrentSet);
    }
    
    public void IRCommaSeparatedListToVaribaleNamesSet(TIRCommaSeparatedList csl, HashSetFlowSet<String> variableNames)
    {
        if (csl == null) return;
        for (Name name : csl.asNameList())
        {
            variableNames.add(name.getID());
        }
    }

    @Override
    public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out)
    {
        out.addAll(in1);
        out.addAll(in2);
    }

    @Override
    public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest)
    {
        dest = source.copy();
    }
    
    // TODO Figure out how we handle the input and output variables!
    public StaticFunction getFunction()
    {
        return fFunction;
    }
    
    public Set<String> getNames(TIRAbstractAssignStmt node)
    {
        HashSetFlowSet<String> set = fFlowSets.get(node);
        if (set == null)    return null;
        else
        {
            return set.getSet();
        }
    }

    @Override
    public HashSetFlowSet<String> getResult()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HashSetFlowSet<String> getDefaultResult()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Function getTree() 
    {
        return (Function)super.getTree();
    }
}
