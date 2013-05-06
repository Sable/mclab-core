package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRNode;
import ast.ASTNode;
import com.google.common.collect.Maps;

public class UDChain implements TamerPlusAnalysis
{
    private Map<TIRNode, Map<String, Set<TIRNode>>> fUDMap;
    private UsedVariablesNameCollector fUsedVariablesNameCollector;
    private ReachingDefinitions fReachingDefinitionsAnalysis;
    
    public UDChain(ASTNode<?> tree)
    {
        fUDMap = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUsedVariablesNameCollector = engine.getUsedVariablesAnalysis();
        fReachingDefinitionsAnalysis = engine.getReachingDefinitionsAnalysis();
        constructUDChain();
    }
    
    private void constructUDChain()
    {
        for (TIRNode visitedStmt : fReachingDefinitionsAnalysis.getVisitedStmtsOrderedList())
        {
           Set<String> usedVariables = fUsedVariablesNameCollector.getUsedVariablesForNode(visitedStmt);
           if (usedVariables.isEmpty())
           {
               continue;
           }
           fUDMap.put(visitedStmt, getUsedVariablesToDefinitionsMapForNode(visitedStmt, usedVariables));
        }
    }
    
    public Map<String, Set<TIRNode>> getUsedVariablesToDefinitionsMapForNode(TIRNode node, Set<String> usedVariables)
    {
        Map<String, Set<TIRNode>> usedVariablesToDefinitionsMap = Maps.newHashMap();
        Map<String, Set<TIRNode>> variableToReachingDefinitionsMap = fReachingDefinitionsAnalysis.getReachingDefinitionsForNode(node);
        for (String variableName : variableToReachingDefinitionsMap.keySet())
        {
            // If the variable is not declared in that stmt, add it to the map. We want to keep the used variables only.
            if (usedVariables.contains(variableName))
            {
                usedVariablesToDefinitionsMap.put(variableName, variableToReachingDefinitionsMap.get(variableName));
            }
        }
        return usedVariablesToDefinitionsMap;
    }
    
    public Map<TIRNode, Map<String, Set<TIRNode>>> getChain() 
    {
        return fUDMap; 
    }
    
    public Map<String, Set<TIRNode>> getDefinitionsMapFoUseStmt(TIRNode useStmt)
    {
        return fUDMap.get(useStmt);
    }
        
    public void printUDChain()
    {
        StringBuilder sb = new StringBuilder();
        LinkedList<TIRNode> visitedStmts = fReachingDefinitionsAnalysis.getVisitedStmtsOrderedList();
        for (TIRNode visitedStmt : visitedStmts)
        {
            sb.append("------- ").append(NodePrinter.printNode(visitedStmt)).append(" -------\n");
            Map<String, Set<TIRNode>> definitionSiteMap = fUDMap.get(visitedStmt);
            if (definitionSiteMap == null)
            {
                continue;
            }
            printVariableToReachingDefinitionsMap(definitionSiteMap, sb);
        }
        System.out.println(sb.toString());
    }
    
    private void printVariableToReachingDefinitionsMap(Map<String, Set<TIRNode>> definitionSiteMap, StringBuilder sb)
    {
        for (Map.Entry<String, Set<TIRNode>> entry : definitionSiteMap.entrySet())
        {
            printVariableToReachingDefinitionsMapEntry(entry, sb);
        }
    }
    
    private void printVariableToReachingDefinitionsMapEntry(Map.Entry<String, Set<TIRNode>> entry, StringBuilder sb)
    {
        Set<TIRNode> defStmts = entry.getValue();
        if (!defStmts.isEmpty())
        {
            sb.append("Var ").append(entry.getKey()).append("\n");
            for (TIRNode defStmt : defStmts)
            {
                sb.append("\t").append(NodePrinter.printNode(defStmt)).append("\n");
            }
        }
    }
    
    public LinkedList<TIRNode> getVisitedStmtsOrderedList()
    {
        return fReachingDefinitionsAnalysis.getVisitedStmtsOrderedList();
    }
}
