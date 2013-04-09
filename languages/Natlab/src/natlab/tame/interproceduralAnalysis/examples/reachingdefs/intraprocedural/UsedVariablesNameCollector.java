package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.Name;
import ast.NameExpr;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

 public class UsedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<Name>> implements TamerPlusAnalysis 
{
    private HashSet<Name> fCurrentVariablesSet;
    public Map<TIRNode, HashSet<Name>> fNodeToUsedVariablesMap;
    
    public UsedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fNodeToUsedVariablesMap = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        super.analyze();
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentVariablesSet = newInitialFlow();
       // TODO, this might cause an error(asNameList)!
       fCurrentVariablesSet.addAll(node.getArguments().asNameList());
       fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getConditionVarName());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getCondition().getName());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
        caseWhileStmt(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        if (node.hasIncr() ) fCurrentVariablesSet.add(node.getIncName());
        fCurrentVariablesSet.add(node.getUpperName());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(((NameExpr) node.getRHS()).getName());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getValueName());
        fCurrentVariablesSet.add(node.getDotName());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getValueName());
        fCurrentVariablesSet.add(node.getCellArrayName());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(node.getIndizes().asNameList());   
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getValueName());
        fCurrentVariablesSet.add(node.getArrayName());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(node.getIndizes().asNameList());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRArrayGetStmt(TIRArrayGetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getArrayName());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(node.getIndizes().asNameList());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRCellArrayGetStmt(TIRCellArrayGetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getCellArrayName());
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(node.getIndices().asNameList());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRDotGetStmt(TIRDotGetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getDotName());
        fCurrentVariablesSet.add(node.getFieldName());
        fNodeToUsedVariablesMap.put(node, fCurrentVariablesSet);
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

    public Set<Name> getUsedVariablesForNode(TIRNode node)
    {
        Set<Name> set = fNodeToUsedVariablesMap.get(node);
        if (set == null)
        {
            return Sets.newHashSet();
        }
        return set;
    }
}
