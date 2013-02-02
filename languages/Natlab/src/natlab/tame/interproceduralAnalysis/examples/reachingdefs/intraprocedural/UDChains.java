package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRNode;
import natlab.toolkits.analysis.HashMapFlowMap;


public class UDChains
{
    private ReachingDefinitionsAnalysis fReachingDefinitionsAnalysis;
    private Map<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> fUDMap;
    private VariableNameCollector fVariableNameCollector;
    
    public UDChains(ReachingDefinitionsAnalysis reachingDefintionsAnalysis)
    {
        fReachingDefinitionsAnalysis = reachingDefintionsAnalysis;
        fUDMap = new HashMap<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
    }
    
    public void constructUDChain()
    {
        for (TIRNode visitedNode : fReachingDefinitionsAnalysis.getVisitedAssignStmtsLinkedList())
        {
           HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> varToDefUsedMap = fReachingDefinitionsAnalysis.getOutFlowSets().get(visitedNode);
           Set<String> declaredVariablesNames = fVariableNameCollector.getNames((TIRAbstractAssignStmt) visitedNode);
           HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>> varToUsedMap = new HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>();
           for (String key : varToDefUsedMap.keySet())
           {
               // If the variable is not declared in that stmt, add it to the map. We want to keep of the used variables only.
               if (!declaredVariablesNames.contains(key))
               {
                   varToUsedMap.put(key, (HashSet<TIRAbstractAssignStmt>) varToDefUsedMap.get(key));
               }
           }
           fUDMap.put(visitedNode, varToUsedMap);
        }
    }
    
    public void printUDChain()
    {
        StringBuffer sb = new StringBuffer();
        for (TIRNode node : fReachingDefinitionsAnalysis.getVisitedAssignStmtsLinkedList())
        {
            sb.append("------- " + ((TIRAbstractAssignStmt) node).getStructureString() + " ------------\n");
            HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>> definitionSiteMap = fUDMap.get(node);
            Set<String> variableNames = definitionSiteMap.keySet();
            for (String s : variableNames)
            {
                Set<TIRAbstractAssignStmt> assignStmts = definitionSiteMap.get(s);
                if (!assignStmts.isEmpty())
                {
                    sb.append("Var "+ s + "\n");
                    for (TIRAbstractAssignStmt assignStmt : assignStmts)
                    {
                        sb.append("\t"+ assignStmt.getStructureString() + "\n");
                    }
                }
            }
        }
        System.out.println(sb.toString());
    }
}
