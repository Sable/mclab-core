package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.toolkits.analysis.HashMapFlowMap;


public class UDChain
{
    private ReachingDefinitionsAnalysis fReachingDefinitionsAnalysis;
    private Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> fUDMap;
    private VariableNameCollector fVariableNameCollector;
    private UsedVariablesNameCollector fUsedVariablesNameCollector;
    
    public UDChain(ReachingDefinitionsAnalysis reachingDefintionsAnalysis)
    {
        fReachingDefinitionsAnalysis = reachingDefintionsAnalysis;
        fUDMap = new HashMap<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(fVariableNameCollector.getFunction());
        fUsedVariablesNameCollector.analyze();
    }
    
    public UDChain(StaticFunction f)
    {
        fReachingDefinitionsAnalysis = new ReachingDefinitionsAnalysis(f);
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(f);
        fUDMap = new HashMap<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector.analyze();
    }
    
    public void constructUDChain()
    {
        for (TIRNode visitedNode : fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList())
        {
           HashMapFlowMap<String, Set<TIRNode>> varToDefUsedMap = fReachingDefinitionsAnalysis.getOutFlowSets().get(visitedNode);
           Set<String> usedVariables = fUsedVariablesNameCollector.getUsedVariablesForNode(visitedNode);
           if (usedVariables == null) continue;
           HashMapFlowMap<String, HashSet<TIRNode>> varToUsedMap = new HashMapFlowMap<String, HashSet<TIRNode>>();
           for (String key : varToDefUsedMap.keySet())
           {
               // If the variable is not declared in that stmt, add it to the map. We want to keep the used variables only.
               if (usedVariables.contains(key))
               {
                   varToUsedMap.put(key, (HashSet<TIRNode>) varToDefUsedMap.get(key));
               }
           }
           fUDMap.put(visitedNode, varToUsedMap);
        }
    }
    
    public Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> getUDMap() { return fUDMap; }
    
    public ReachingDefinitionsAnalysis getReachingDefinitionsAnalysis() { return fReachingDefinitionsAnalysis; }
    
    public void printUDChain()
    {
        StringBuffer sb = new StringBuffer();
        for (TIRNode visitedStmt : fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList())
        {
            sb.append("------- " + printNode(visitedStmt) + " ------------\n");
            HashMapFlowMap<String, HashSet<TIRNode>> definitionSiteMap = fUDMap.get(visitedStmt);
            if (definitionSiteMap == null) continue;
            Set<String> variableNames = definitionSiteMap.keySet();
            for (String s : variableNames)
            {
                Set<TIRNode> defStmts = definitionSiteMap.get(s);
                if (!defStmts.isEmpty())
                {
                    sb.append("Var "+ s + "\n");
                    for (TIRNode defStmt : defStmts)
                    {
                        sb.append("\t"+ printNode(defStmt) + "\n");
                    }
                }
            }
        }
        System.out.println(sb.toString());
    }
    
    private String printNode(TIRNode node)
    {
        if (node instanceof TIRAbstractAssignStmt) return ((TIRAbstractAssignStmt) node).getStructureString();
        else if (node instanceof TIRFunction) return ((TIRFunction) node).getStructureString().split("\n")[0];
        else return null;
    }
}
