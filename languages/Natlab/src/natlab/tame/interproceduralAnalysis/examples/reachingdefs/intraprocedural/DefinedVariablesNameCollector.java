package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.Name;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DefinedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<Name>> implements TamerPlusAnalysis 
{
    private HashSet<Name> fCurrentVariablesSet;
    private Map<TIRNode, HashSet<Name>> fNodeToDefinedVariablesMap;
    
    public DefinedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fNodeToDefinedVariablesMap = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        super.analyze();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.addAll((Collection<? extends Name>) node.getInputParamList());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getLoopVarName());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentVariablesSet = newInitialFlow();
       // TODO, this might cause an error(asNameList)!
       fCurrentVariablesSet.addAll(node.getTargets().asNameList());
       fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(node.getTargets().asNameList());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getTargetName());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getArrayName());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getCellArrayName());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getDotName());
        fCurrentVariablesSet.add(node.getFieldName());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void merge(HashSet<Name> in1, HashSet<Name> in2, HashSet<Name> out)
    {
        out.addAll(in1);
        out.addAll(in2);
    }

    @Override
    public void copy(HashSet<Name> source, HashSet<Name> dest)
    {
        for (Name varName : source)
        {
            dest.add(varName.copy());
        }
    }
    
    public HashSet<Name> newInitialFlow()
    {
        return Sets.newHashSet();
    }

    public Set<Name> getDefinedVariablesForNode(TIRNode node)
    {
        Set<Name> set = fNodeToDefinedVariablesMap.get(node);
        if (set == null) 
        {
            return Sets.newHashSet();
        }
        return set;
    }
    
    public Set<Name> getDefinedVariablesFullSet()
    {
        Set<Name> definedVariablesFullSet = Sets.newHashSet(); 
        for (Entry<TIRNode, HashSet<Name>> entry : fNodeToDefinedVariablesMap.entrySet())
        {
            definedVariablesFullSet.addAll(entry.getValue());
        }
        return definedVariablesFullSet;
    }

}
