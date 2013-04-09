package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRNode;
import ast.Name;

import com.google.common.collect.Maps;

public class UDChain implements TamerPlusAnalysis
{
    private Map<TIRNode, Map<Name, Set<TIRNode>>> fUDMap;
    private UsedVariablesNameCollector fUsedVariablesNameCollector;
    private ReachingDefinitions fReachingDefinitionsAnalysis;
    
    public UDChain()
    {
        fUDMap = Maps.newHashMap();
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUsedVariablesNameCollector = engine.getUsedVariablesAnalysis();
        fReachingDefinitionsAnalysis = engine.getReachingDefinitionsAnalysis();
        this.constructUDChain();
    }
    
    public void constructUDChain()
    {
        for (TIRNode visitedStmt : fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList())
        {
           Set<Name> usedVariables = fUsedVariablesNameCollector.getUsedVariablesForNode(visitedStmt);
           if (usedVariables == null)
           {
               continue;
           }
           fUDMap.put(visitedStmt, getUsedVariablesToDefinitionsMapForNode(visitedStmt, usedVariables));
        }
    }
    
    public Map<Name, Set<TIRNode>> getUsedVariablesToDefinitionsMapForNode(TIRNode node, Set<Name> usedVariables)
    {
        Map<Name, Set<TIRNode>> usedVariablesToDefinitionsMap = Maps.newHashMap();
        Map<Name, Set<TIRNode>> variableToReachingDefinitionsMap = fReachingDefinitionsAnalysis.getReachingDefinitionsForNode(node);
        for (Name variableName : variableToReachingDefinitionsMap.keySet())
        {
            // If the variable is not declared in that stmt, add it to the map. We want to keep the used variables only.
            if (usedVariables.contains(variableName))
            {
                usedVariablesToDefinitionsMap.put(variableName, variableToReachingDefinitionsMap.get(variableName));
            }
        }
        return usedVariablesToDefinitionsMap;
    }
    
    public Map<TIRNode, Map<Name, Set<TIRNode>>> getChain() 
    {
        return fUDMap; 
    }
    
    public Map<Name, Set<TIRNode>> getDefinitionsMap(TIRNode node)
    {
        return fUDMap.get(node);
    }
        
    public void printUDChain()
    {
        StringBuffer sb = new StringBuffer();
        LinkedList<TIRNode> visitedStmts = fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList();
        for (TIRNode visitedStmt : visitedStmts)
        {
            sb.append("------- ").append(NodePrinter.printNode(visitedStmt)).append(" -------\n");
            Map<Name, Set<TIRNode>> definitionSiteMap = fUDMap.get(visitedStmt);
            if (definitionSiteMap == null)
            {
                continue;
            }
            printVariableToReachingDefinitionsMap(definitionSiteMap, sb);
        }
        System.out.println(sb.toString());
    }
    
    public void printVariableToReachingDefinitionsMap(Map<Name, Set<TIRNode>> definitionSiteMap, StringBuffer sb)
    {
        for (Map.Entry<Name, Set<TIRNode>> entry : definitionSiteMap.entrySet())
        {
            printVariableToReachingDefinitionsMapEntry(entry, sb);
        }
    }
    
    public void printVariableToReachingDefinitionsMapEntry(Map.Entry<Name, Set<TIRNode>> entry, StringBuffer sb)
    {
        Set<TIRNode> defStmts = entry.getValue();
        if (!defStmts.isEmpty())
        {
            sb.append("Var ").append(NodePrinter.printNode(entry.getKey())).append("\n");
            for (TIRNode defStmt : defStmts)
            {
                sb.append("\t").append(NodePrinter.printNode(defStmt)).append("\n");
            }
        }
    }
}
