package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;
import ast.ASTNode;
import ast.Function;

/**
 * Intraprocedural analysis that determines whether a variable has been assigned before it was used.
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class DefiniteAssignmentAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>>
{

    private StaticFunction fFunction;
    private DefinedVariablesNameCollector fDefinedVariablesNameCollector;
    
    public DefiniteAssignmentAnalysis(ASTNode<?> tree)
    {
        super(tree);
    }
    
    public DefiniteAssignmentAnalysis(StaticFunction f, DefinedVariablesNameCollector definedVariablesNameCollector)
    {
        super(f.getAst());
        fFunction = f;
        fDefinedVariablesNameCollector = definedVariablesNameCollector;
    }
    
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        currentInSet = newInitialFlow();
        currentOutSet = copy(currentInSet);
        addDefinedVariablesToCurrentOutSet(node);
        setInOutSet(node);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       currentOutSet = copy(currentInSet);
       addDefinedVariablesToCurrentOutSet(node);
       setInOutSet(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        currentOutSet = copy(currentInSet);
        addDefinedVariablesToCurrentOutSet(node);
        setInOutSet(node);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        currentOutSet = copy(currentInSet);
        addDefinedVariablesToCurrentOutSet(node);
        setInOutSet(node);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        currentOutSet = copy(currentInSet);
        addDefinedVariablesToCurrentOutSet(node);
        setInOutSet(node);
    }
    
    public void addDefinedVariablesToCurrentOutSet(TIRNode node)
    {
        currentOutSet.addAll(fDefinedVariablesNameCollector.getDefinedVariablesNamesForNode(node));
    }
    
    public boolean isDefinitelyAssignedAtInputOf(ASTNode node, String variableName)
    {
        if (!inFlowSets.containsKey((node)))
        {
            return false;
        }
        return inFlowSets.get(node).contains(variableName);
    }
    
    @Override
    public Function getTree()
    {
        return (Function) super.getTree();
    }

    @Override
    public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out)
    {
        if (!out.isEmpty())
        {
            out.clear();
        }
        for (String variableName : in1)
        {
            if (in2.contains(variableName))
            {
                out.add(variableName);
            }
        }
    }

    @Override
    public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest)
    {
        source.copy(dest);
    }
    
    public HashSetFlowSet<String> copy(HashSetFlowSet<String> in)
    {
        HashSetFlowSet<String> out = new HashSetFlowSet<String>();
        copy(in, out);
        return out;
    }
    
    private void setInOutSet(TIRNode node)
    {
        associateInSet((ASTNode) node, getCurrentInSet());
        associateOutSet((ASTNode) node, getCurrentOutSet());
    }

    @Override
    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }
    
    public StaticFunction getFunction() { return fFunction; }

}
