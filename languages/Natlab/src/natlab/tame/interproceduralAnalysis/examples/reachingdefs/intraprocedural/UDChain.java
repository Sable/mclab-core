package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.HashMapFlowMap;

/**
 * Construct a UD chain for a given AST
 * @author Amine Sahibi
 *
 */
public class UDChain
{
    private ReachingDefinitions fReachingDefinitionsAnalysis;
    private Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> fUDMap;
    private DefinedVariablesNameCollector fVariableNameCollector;
    private UsedVariablesNameCollector fUsedVariablesNameCollector;
    
    public UDChain(ReachingDefinitions reachingDefintionsAnalysis)
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
        fReachingDefinitionsAnalysis = new ReachingDefinitions(f);
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(f);
        fUDMap = new HashMap<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector.analyze();
    }
    
    public void constructUDChain()
    {
        LinkedList<TIRNode> visitedStmts = fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList();
        for (TIRNode visitedStmt : visitedStmts)
        {
           HashMapFlowMap<String, Set<TIRNode>> varToDefUsedMap = fReachingDefinitionsAnalysis.getOutFlowSets().get(visitedStmt);
           Set<String> usedVariables = fUsedVariablesNameCollector.getUsedVariablesForNode(visitedStmt);
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
           fUDMap.put(visitedStmt, varToUsedMap);
        }
    }
    
    public Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> getChain() { return fUDMap; }
    
    public HashMapFlowMap<String, HashSet<TIRNode>> getDefinitionsMap(TIRNode node) { return fUDMap.get(node); }
        
    public LinkedList<TIRNode> getVisitedStmtsLinkedList() { return fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList(); } 
    
    public void printUDChain()
    {
        StringBuffer sb = new StringBuffer();
        LinkedList<TIRNode> visitedStmts = fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList();
        for (TIRNode visitedStmt : visitedStmts)
        {
            sb.append("------- " + printNode(visitedStmt) + " -------\n");
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
        else if (node instanceof TIRIfStmt) return ((TIRIfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRWhileStmt) return ((TIRWhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRForStmt) return ((TIRForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
}
