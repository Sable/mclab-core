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
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.HashMapFlowMap;
import ast.ASTNode;
import ast.AssignStmt;

/**
 * Construct a UD chain for a given AST
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class UDChain
{
    private ReachingDefinitions fReachingDefinitionsAnalysis;
    private Map<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> fUDMap;
    private DefinedVariablesNameCollector fVariableNameCollector;
    private UsedVariablesNameCollector fUsedVariablesNameCollector;
    
    public UDChain(ReachingDefinitions reachingDefintionsAnalysis)
    {
        fReachingDefinitionsAnalysis = reachingDefintionsAnalysis;
        fUDMap = new HashMap<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(fVariableNameCollector.getFunction());
        fUsedVariablesNameCollector.analyze();
    }
    
    public UDChain(StaticFunction f)
    {
        fReachingDefinitionsAnalysis = new ReachingDefinitions(f);
        fUsedVariablesNameCollector = new UsedVariablesNameCollector(f);
        fUDMap = new HashMap<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> ();
        fReachingDefinitionsAnalysis.analyze();
        fVariableNameCollector = fReachingDefinitionsAnalysis.getVarNameCollector();
        fUsedVariablesNameCollector.analyze();
    }
    
    public void constructUDChain()
    {
        LinkedList<ASTNode> visitedStmts = fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList();
        for (ASTNode visitedStmt : visitedStmts)
        {
           HashMapFlowMap<String, Set<ASTNode>> varToDefUsedMap = fReachingDefinitionsAnalysis.getOutFlowSets().get(visitedStmt);
           Set<String> usedVariables = fUsedVariablesNameCollector.getUsedVariablesForNode(visitedStmt);
           if (usedVariables == null) continue;
           HashMapFlowMap<String, HashSet<ASTNode>> varToUsedMap = new HashMapFlowMap<String, HashSet<ASTNode>>();
           for (String key : varToDefUsedMap.keySet())
           {
               // If the variable is not declared in that stmt, add it to the map. We want to keep the used variables only.
               if (usedVariables.contains(key))
               {
                   varToUsedMap.put(key, (HashSet<ASTNode>) varToDefUsedMap.get(key));
               }
           }
           fUDMap.put(visitedStmt, varToUsedMap);
        }
    }
    
    public Map<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> getChain() { return fUDMap; }
    
    public HashMapFlowMap<String, HashSet<ASTNode>> getDefinitionsMap(ASTNode node) { return fUDMap.get(node); }
        
    public LinkedList<ASTNode> getVisitedStmtsLinkedList() { return fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList(); }
    
    public void printUDChain()
    {
        StringBuffer sb = new StringBuffer();
        LinkedList<ASTNode> visitedStmts = fReachingDefinitionsAnalysis.getVisitedStmtsLinkedList();
        for (ASTNode visitedStmt : visitedStmts)
        {
            sb.append("------- " + printNode(visitedStmt) + " -------\n");
            HashMapFlowMap<String, HashSet<ASTNode>> definitionSiteMap = fUDMap.get(visitedStmt);
            if (definitionSiteMap == null) continue;
            Set<String> variableNames = definitionSiteMap.keySet();
            for (String s : variableNames)
            {
                Set<ASTNode> defStmts = definitionSiteMap.get(s);
                if (!defStmts.isEmpty())
                {
                    sb.append("Var "+ s + "\n");
                    for (ASTNode defStmt : defStmts)
                    {
                        sb.append("\t"+ printNode(defStmt) + "\n");
                    }
                }
            }
        }
        System.out.println(sb.toString());
    }
    
    private String printNode(ASTNode node)
    {
        if (node instanceof TIRAbstractAssignStmt) return ((TIRAbstractAssignStmt) node).getStructureString();
        else if (node instanceof TIRFunction) return ((TIRFunction) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRIfStmt) return ((TIRIfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRWhileStmt) return ((TIRWhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRForStmt) return ((TIRForStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof AssignStmt) return node.getStructureString();
        else return null;
    }
}
