package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tir.TIRNode;
import ast.ASTNode;

import com.google.common.collect.Maps;

public class DUChain implements TamerPlusAnalysis
{
    public static boolean DEBUG = false;
    
    private Map<TIRNode, HashMap<String, HashSet<TIRNode>>> fDUMap;
    private UDChain fUDChains;
    
    public DUChain(ASTNode<?> tree)
    {
        fDUMap = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUDChains = engine.getUDChainAnalysis();
        this.constructDUChain();
        
        if (DEBUG) printDUChain();
    }
    
    private void constructDUChain()
    {
        Map<TIRNode, Map<String, Set<TIRNode>>> resultUDMap = fUDChains.getChain();
        for (TIRNode useStmt : resultUDMap.keySet())
        {
            chainVariablesDefToUseForStmt(useStmt, resultUDMap);
        }
    }
    
    private void chainVariablesDefToUseForStmt(TIRNode useStmt, Map<TIRNode, Map<String, Set<TIRNode>>> resultUDMap)
    {
        Set<String> usedVariablesNames = resultUDMap.get(useStmt).keySet();
        for (String usedVariableName : usedVariablesNames)
        {
            Set<TIRNode> definitionStmts = resultUDMap.get(useStmt).get(usedVariableName);
            chainDefsToUseForVariable(definitionStmts, usedVariableName, useStmt);
        }
    }
    
    private void chainDefsToUseForVariable(Set<TIRNode> definitionStmts, String usedVariableName, TIRNode useStmt)
    {
        for (TIRNode defStmt : definitionStmts) 
        {
            createNewMapForDefinitionStmt(defStmt);
            createUseStmtSetForDefStmtAndUsedVariable(defStmt, usedVariableName);
            fDUMap.get(defStmt).get(usedVariableName).add(useStmt);
        }
    }
    
    private void createNewMapForDefinitionStmt(TIRNode defStmt)
    {
        if (!fDUMap.containsKey(defStmt))
        {
            HashMap<String, HashSet<TIRNode>> useSiteMap = Maps.newHashMap();
            fDUMap.put(defStmt, useSiteMap);
        }
    }
    
    private void createUseStmtSetForDefStmtAndUsedVariable(TIRNode defStmt, String usedVariableName)
    {
        if (!fDUMap.get(defStmt).containsKey(usedVariableName))
        {
            HashSet<TIRNode> useAssignStmtsSet = new HashSet<TIRNode>();
            fDUMap.get(defStmt).put(usedVariableName, useAssignStmtsSet);
        }
    }
    
    /**
     * Returns the Use Definition chain
     * @return map - key: node, value: map - key: defined variable, value: set of uses of that variable
     */
    public Map<TIRNode, HashMap<String, HashSet<TIRNode>>> getChain() 
    {
        return fDUMap; 
    }
    
    /**
     * Returns the definitions map for a use statement
     * @param defStmt
     * @return map - key: defined variable, value: set of uses of that variable
     */
    public HashMap<String, HashSet<TIRNode>> getUsesMapForDefinitionStmt(TIRNode defStmt) 
    {
        return fDUMap.get(defStmt); 
    }
    
    private void printDUChain()
    {
        System.out.println("\nDefinition Use Chain analysis results:");
        StringBuilder sb = new StringBuilder();
        LinkedList<TIRNode> visitedStmts = fUDChains.getVisitedStmtsOrderedList();
        for (TIRNode visitedStmt : visitedStmts)
        {
            sb.append("------- " + NodePrinter.printNode(visitedStmt) + " -------\n");
            Map<String, HashSet<TIRNode>> useSiteMap = fDUMap.get(visitedStmt);
            if (useSiteMap == null)
            {
                continue;
            }
            printVariableToUsesMap(useSiteMap, sb);
        }
        System.out.println(sb.toString() + "\n");
    }
    
    private void printVariableToUsesMap(Map<String, HashSet<TIRNode>> useSiteMap, StringBuilder sb)
    {
        for (Map.Entry<String, HashSet<TIRNode>> entry : useSiteMap.entrySet())
        {
            printVariableToUsesMapEntry(entry, sb);
        }
    }
    
    private void printVariableToUsesMapEntry(Map.Entry<String, HashSet<TIRNode>> entry, StringBuilder sb)
    {
        Set<TIRNode> useStmts = entry.getValue();
        if (!useStmts.isEmpty())
        {
            sb.append("Var ").append(entry.getKey()).append("\n");
            for (TIRNode useStmt : useStmts)
            {
                sb.append("\t").append(NodePrinter.printNode(useStmt)).append("\n");
            }
        }
    }
    
}
