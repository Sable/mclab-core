package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;
import ast.ASTNode;
import ast.Function;
import ast.Name;
import ast.NameExpr;

/**
 * Intraprocedural analysis that collects the names of variables defined in a function
 * @author Amine Sahibi
 *
 */
public class DefinedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> implements FunctionAnalysis<StaticFunction, HashSetFlowSet<String>>
{
    private HashSetFlowSet<String> fFullSet = new HashSetFlowSet<String>();
    private HashSetFlowSet<String> fCurrentSet;
    private Map<TIRNode, HashSetFlowSet<String>> fFlowSets = new HashMap<TIRNode, HashSetFlowSet<String>>();
    private StaticFunction fFunction;
    
    public DefinedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fFunction = null;
    }
    
    public DefinedVariablesNameCollector(StaticFunction f)
    {
        super(f.getAst());
        fFunction = f;
    }
    
    @Override
    public HashSetFlowSet<String> getResult()
    {
        analyze();
        return fFullSet;
    }
    
    @Override
    public HashSetFlowSet<String> getDefaultResult() 
    {
        throw new UnsupportedOperationException("Variable name collector doesn't support recursive programs");
    }
    
    @Override
    public Function getTree() 
    {
        return (Function) super.getTree();
    }
   
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        fCurrentSet = newInitialFlow();
        fCurrentSet.addAll(node.getInParamSet());
        fFlowSets.put(node, fCurrentSet);
        fFullSet.addAll(fCurrentSet);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        fCurrentSet = newInitialFlow();
        fCurrentSet.add(node.getLoopVarName().getID());
        fFlowSets.put(node, fCurrentSet);
        fFullSet.addAll(fCurrentSet);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentSet = newInitialFlow();
       IRCommaSeparatedListToVaribaleNamesSet(((TIRAbstractAssignToListStmt)node).getTargets(), fCurrentSet);
       fFlowSets.put(node, fCurrentSet);
       fFullSet.addAll(fCurrentSet);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        fCurrentSet = newInitialFlow();
        TIRCommaSeparatedList definedVariablesNames = null;
        if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            Name target;
            if (node instanceof TIRArraySetStmt)
            {
                target = ((TIRArraySetStmt)node).getArrayName();
                definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(target));
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                target = ((TIRCellArraySetStmt)node).getCellArrayName();
                definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(target));
            }
            else if (node instanceof TIRDotSetStmt)
            {
                target = ((TIRDotSetStmt)node).getDotName();
                Name targetField = ((TIRDotSetStmt)node).getFieldName();
                definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(target));
                definedVariablesNames.add(new NameExpr(targetField));
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }

        }
        else if (node instanceof TIRAbstractAssignToVarStmt)
        {
            definedVariablesNames = new TIRCommaSeparatedList(new NameExpr(((TIRAbstractAssignToVarStmt)node).getTargetName()));
        }
        else 
        {
            throw new UnsupportedOperationException("unknown assignment statement " + node);
        }
        IRCommaSeparatedListToVaribaleNamesSet(definedVariablesNames, fCurrentSet);
        fFlowSets.put(node, fCurrentSet);
        fFullSet.addAll(fCurrentSet);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        fCurrentSet = newInitialFlow();
        TIRCommaSeparatedList definedVariablesNames = node.getTargets();
        IRCommaSeparatedListToVaribaleNamesSet(definedVariablesNames, fCurrentSet);
        fFlowSets.put(node, fCurrentSet);
        fFullSet.addAll(fCurrentSet);
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
    
    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }
    
    private void IRCommaSeparatedListToVaribaleNamesSet(TIRCommaSeparatedList csl, HashSetFlowSet<String> variableNames)
    {
        if (csl == null) 
        {
            return;
        }
        for (Name name : csl.asNameList())
        {
            variableNames.add(name.getID());
        }
    }

    public Set<String> getDefinedVariablesNamesForNode(TIRNode node)
    {
        HashSetFlowSet<String> set = fFlowSets.get(node);
        if (set == null) 
        {
            return new HashSet<String>();
        }
        return set.getSet();
    }
    
    public StaticFunction getFunction() 
    {
        return fFunction; 
    }
    
    public HashSetFlowSet<String> getFullSet()
    {
        return fFullSet; 
    }
}
