package natlab.tame.tamerplus.analysis;

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


public class DefiniteAssignment extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<String>> implements TamerPlusAnalysis
{

    private DefinedVariablesNameCollector fDefinedVariablesNameCollector;
    
    public DefiniteAssignment(ASTNode<?> tree)
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
    
    /**
     * Returns whether a variable is definitely assigned at input set of a given node
     * @param node
     * @param variableName
     * @return true if the variable is definitely assigned at input set of a given node, false otherwise
     */
    public boolean isDefinitelyAssignedAtInputOf(TIRNode node, String variableName)
    {
        if (!inFlowSets.containsKey((node)))
        {
            return false;
        }
        return inFlowSets.get(node).contains(variableName);
    }

    @Override
    public HashSet<String> merge(HashSet<String> in1, HashSet<String> in2)
    {
        return Sets.newHashSet(Sets.intersection(in1, in2));
    }

    @Override
    public HashSet<String> copy(HashSet<String> source)
    {
        return Sets.newHashSet(source);
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
