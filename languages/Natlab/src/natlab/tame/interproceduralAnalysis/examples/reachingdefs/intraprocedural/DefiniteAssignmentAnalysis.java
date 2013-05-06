package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;

import com.google.common.collect.Sets;


public class DefiniteAssignmentAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<String>> implements TamerPlusAnalysis
{

    private DefinedVariablesNameCollector fDefinedVariablesNameCollector;
    
    public DefiniteAssignmentAnalysis(ASTNode<?> tree)
    {
        super(tree);
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fDefinedVariablesNameCollector = engine.getDefinedVariablesAnalysis();
        super.analyze();
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
        currentOutSet.addAll(fDefinedVariablesNameCollector.getDefinedVariablesForNode(node));
    }
    
    public boolean isDefinitelyAssignedAtInputOf(TIRNode node, String variableName)
    {
        if (!inFlowSets.containsKey((node)))
        {
            return false;
        }
        return inFlowSets.get(node).contains(variableName);
    }

    @Override
    public void merge(HashSet<String> in1, HashSet<String> in2, HashSet<String> out)
    {
        out.clear();
        out.addAll(Sets.intersection(in1, in2));
    }

    @Override
    public void copy(HashSet<String> source, HashSet<String> dest)
    {
        for (String varName : source)
        {
            dest.add(varName);
        }
    }
    
    public HashSet<String> copy(HashSet<String> in)
    {
        HashSet<String> out = Sets.newHashSet();
        copy(in, out);
        return out; 
    }
    
    private void setInOutSet(TIRNode node)
    {
        associateInSet((ASTNode<?>) node, getCurrentInSet());
        associateOutSet((ASTNode<?>) node, getCurrentOutSet());
    }

    @Override
    public HashSet<String> newInitialFlow()
    {
        return Sets.newHashSet();
    }
}
