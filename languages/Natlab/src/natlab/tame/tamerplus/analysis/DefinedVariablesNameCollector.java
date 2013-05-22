package natlab.tame.tamerplus.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tamerplus.utils.TamerPlusUtils;
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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DefinedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSet<String>> implements TamerPlusAnalysis 
{
    public static boolean DEBUG = false;
    
    private HashSet<String> fCurrentVariablesSet;
    private Map<TIRNode, HashSet<String>> fNodeToDefinedVariablesMap;
    
    public DefinedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
        fNodeToDefinedVariablesMap = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        super.analyze();
        
        if (DEBUG) printNodeToDefinedVariablesMapContent();
    }
    
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        fCurrentVariablesSet = Sets.newHashSet(TamerPlusUtils.getNameListAsStringSet(node.getInputParams()));
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getLoopVarName().getID());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentVariablesSet = newInitialFlow();
       // TODO, this might cause an error(asNameList)!
       fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getTargets().asNameList()));
       fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        // TODO, this might cause an error(asNameList)!
        fCurrentVariablesSet.addAll(TamerPlusUtils.getNameListAsStringSet(node.getTargets().asNameList()));
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRAbstractAssignToVarStmt(TIRAbstractAssignToVarStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getTargetName().getID());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRArraySetStmt(TIRArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getArrayName().getID());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRCellArraySetStmt(TIRCellArraySetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getCellArrayName().getID());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public void caseTIRDotSetStmt(TIRDotSetStmt node)
    {
        fCurrentVariablesSet = newInitialFlow();
        fCurrentVariablesSet.add(node.getDotName().getID());
        fCurrentVariablesSet.add(node.getFieldName().getID());
        fNodeToDefinedVariablesMap.put(node, fCurrentVariablesSet);
    }
    
    @Override
    public HashSet<String> merge(HashSet<String> in1, HashSet<String> in2)
    {
        HashSet<String> out = Sets.newHashSet();
        out.addAll(in1);
        out.addAll(in2);
        return out;
    }

    @Override
    public HashSet<String> copy(HashSet<String> source)
    {
        return Sets.newHashSet(source);
    }
    
    public HashSet<String> newInitialFlow()
    {
        return Sets.newHashSet();
    }

    public Set<String> getDefinedVariablesForNode(TIRNode node)
    {
        Set<String> set = fNodeToDefinedVariablesMap.get(node);
        if (set == null) 
        {
            return Sets.newHashSet();
        }
        return set;
    }
    
    public Set<String> getDefinedVariablesFullSet()
    {
        Set<String> definedVariablesFullSet = Sets.newHashSet(); 
        for (Entry<TIRNode, HashSet<String>> entry : fNodeToDefinedVariablesMap.entrySet())
        {
            definedVariablesFullSet.addAll(entry.getValue());
        }
        return definedVariablesFullSet;
    }
    
    private void printNodeToDefinedVariablesMapContent()
    {
        System.err.println("Defined variables names analysis results:");
        for (Map.Entry<TIRNode, HashSet<String>> entry : fNodeToDefinedVariablesMap.entrySet())
        {
           System.out.print(NodePrinter.printNode(entry.getKey()) + "\t");
           for(String definedVariableName : entry.getValue())
           {
               System.out.print(definedVariableName + " ");
           }
           System.out.println();
        }
        System.out.println("\n");
    }

}
