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
import ast.Name;

import com.google.common.collect.Sets;


public class DefiniteAssignmentAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<Name>> implements TamerPlusAnalysis
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
        fDefinedVariablesNameCollector.analyze(engine);
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
    
    public boolean isDefinitelyAssignedAtInputOf(TIRNode node, Name variableName)
    {
        if (!inFlowSets.containsKey((node)))
        {
            return false;
        }
        return inFlowSets.get(node).contains(variableName);
    }

    @Override
    public void merge(HashSet<Name> in1, HashSet<Name> in2, HashSet<Name> out)
    {
        out.clear();
        out.addAll(Sets.intersection(in1, in2));
    }

    @Override
    public void copy(HashSet<Name> source, HashSet<Name> dest)
    {
        for (Name varName : source)
        {
            dest.add(varName.copy());
        }
    }
    
    public HashSet<Name> copy(HashSet<Name> in)
    {
        HashSet<Name> out = Sets.newHashSet();
        copy(in, out);
        return out; 
    }
    
    private void setInOutSet(TIRNode node)
    {
        associateInSet((ASTNode<?>) node, getCurrentInSet());
        associateOutSet((ASTNode<?>) node, getCurrentOutSet());
    }

    @Override
    public HashSet<Name> newInitialFlow()
    {
        return Sets.newHashSet();
    }
}
