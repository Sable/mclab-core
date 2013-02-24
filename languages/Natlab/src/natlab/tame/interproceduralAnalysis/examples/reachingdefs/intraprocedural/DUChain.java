package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.HashMapFlowMap;

/**
 * Constructs a DU chain for a given AST
 * @author Amine Sahibi
 *
 */
public class DUChain
{
    private Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> fDUMap;
    private UDChain fUDChains;
    
    public DUChain(UDChain udChains)
    {
        fUDChains = udChains;
        fUDChains.constructUDChain();
        fDUMap = new HashMap<TIRNode, HashMapFlowMap<String,HashSet<TIRNode>>>();
    }
    
    public void constructDUChain()
    {
        Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> resultUDMap = fUDChains.getChain();
        Set<TIRNode> useAssignmentStmts = resultUDMap.keySet();
        for (TIRNode useAssignmentStmt : useAssignmentStmts)
        {
            Set<String> varNames = resultUDMap.get(useAssignmentStmt).keySet();
            for (String varName : varNames)
            {
                Set<TIRNode> defAssignmentStmts = resultUDMap.get(useAssignmentStmt).get(varName);
                for (TIRNode defAssignStmt : defAssignmentStmts) 
                {
                    if (fDUMap.get(defAssignStmt) == null)
                    {
                        HashMapFlowMap<String, HashSet<TIRNode>> useSiteMap = new HashMapFlowMap<String, HashSet<TIRNode>>();
                        fDUMap.put(defAssignStmt, useSiteMap);
                    }
                    if (fDUMap.get(defAssignStmt).get(varName) == null)
                    {
                        HashSet<TIRNode> useAssignStmtsSet = new HashSet<TIRNode>();
                        fDUMap.get(defAssignStmt).put(varName, useAssignStmtsSet);
                    }
                    fDUMap.get(defAssignStmt).get(varName).add((TIRNode) useAssignmentStmt);
                }
            }
        }
    }
    
    public Map<TIRNode, HashMapFlowMap<String, HashSet<TIRNode>>> getChain() { return fDUMap; }
    
    public HashMapFlowMap<String, HashSet<TIRNode>> getUsesMap(TIRNode node) { return fDUMap.get(node); }
    
    public void printDUChain()
    {
        StringBuffer sb = new StringBuffer();
        LinkedList<TIRNode> visitedStmts = fUDChains.getVisitedStmtsLinkedList();
        for (TIRNode visitedStmt : visitedStmts)
        {
            sb.append("------- " + printNode(visitedStmt) + " -------\n");
            HashMapFlowMap<String, HashSet<TIRNode>> useSiteMap = fDUMap.get(visitedStmt);
            if (useSiteMap == null) continue;
            Set<String> variableNames = useSiteMap.keySet();
            for (String s : variableNames)
            {
                Set<TIRNode> useStmts = useSiteMap.get(s);
                if (!useStmts.isEmpty())
                {
                    sb.append("Var "+ s + "\n");
                    for (TIRNode useStmt : useStmts)
                    {
                        sb.append("\t"+ printNode(useStmt) + "\n");
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
