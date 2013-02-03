package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRNode;
import natlab.toolkits.analysis.HashMapFlowMap;


public class UDWeb
{
    private ReachingDefinitionsAnalysis fReachingDefinitionsAnalysis;
    private Map<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> fUDMap;
    private VariableNameCollector fVariableNameCollector;
    private UsedVariablesNameCollector fUsedVariablesNameCollector;
    
    public UDWeb(ReachingDefinitionsAnalysis reachingDefintionsAnalysis)
    {
        fReachingDefinitionsAnalysis = reachingDefintionsAnalysis;
        fUDMap = new HashMap<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(fVariableNameCollector.getFunction());
        fUsedVariablesNameCollector.analyze();
    }
    
    public UDWeb(StaticFunction f)
    {
        fReachingDefinitionsAnalysis = new ReachingDefinitionsAnalysis(f);
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(f);
        fUDMap = new HashMap<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector.analyze();
    }
    
    public void constructUDWeb()
    {
        for (TIRNode visitedNode : fReachingDefinitionsAnalysis.getVisitedAssignStmtsLinkedList())
        {
           HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> varToDefUsedMap = fReachingDefinitionsAnalysis.getOutFlowSets().get(visitedNode);
           Set<String> usedVariables = fUsedVariablesNameCollector.getNames((TIRAbstractAssignStmt) visitedNode);
           HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>> varToUsedMap = new HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>();
           for (String key : varToDefUsedMap.keySet())
           {
               // If the variable is not declared in that stmt, add it to the map. We want to keep of the used variables only.
               if (usedVariables.contains(key))
               {
                   varToUsedMap.put(key, (HashSet<TIRAbstractAssignStmt>) varToDefUsedMap.get(key));
               }
           }
           fUDMap.put(visitedNode, varToUsedMap);
        }
    }
    
    public Map<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> getUDMap()
    {
        return fUDMap;
    }
    
    public ReachingDefinitionsAnalysis getReachingDefinitionsAnalysis()
    {
        return fReachingDefinitionsAnalysis;
    }
    
    public void printUDWeb()
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
