package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRNode;
import natlab.toolkits.analysis.HashMapFlowMap;
import ast.Name;

import com.google.common.collect.Maps;

/**
 * Constructs a DU chain for a given AST
 * @author Amine Sahibi
 *
 */
public class DUChain implements TamerPlusAnalysis
{
    private Map<TIRNode, HashMap<Name, HashSet<TIRNode>>> fDUMap;
    private UDChain fUDChains;
    
    public DUChain(UDChain udChains)
    {
        fUDChains = udChains;
        fUDChains.constructUDChain();
        fDUMap = Maps.newHashMap();
    }
    
    public void constructDUChain()
    {
        Map<TIRNode, HashMap<Name, HashSet<TIRNode>>> resultUDMap = fUDChains.getChain();
        Set<TIRNode> useAssignmentStmts = resultUDMap.keySet();
        for (TIRNode useAssignmentStmt : useAssignmentStmts)
        {
            Set<Name> varNames = resultUDMap.get(useAssignmentStmt).keySet();
            for (Name varName : varNames)
            {
                Set<TIRNode> defAssignmentStmts = resultUDMap.get(useAssignmentStmt).get(varName);
                for (TIRNode defAssignStmt : defAssignmentStmts) 
                {
                    if (fDUMap.get(defAssignStmt) == null)
                    {
                        HashMap<Name, HashSet<TIRNode>> useSiteMap = Maps.newHashMap();
                        fDUMap.put(defAssignStmt, useSiteMap);
                    }
                    
                    if (fDUMap.get(defAssignStmt).get(varName) == null)
                    {
                        HashSet<TIRNode> useAssignStmtsSet = new HashSet<TIRNode>();
                        fDUMap.get(defAssignStmt).put(varName, useAssignStmtsSet);
                    }
                    
                    fDUMap.get(defAssignStmt).get(varName).add(useAssignmentStmt);
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
            sb.append("------- " + NodePrinter.printNode(visitedStmt) + " -------\n");
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
                        sb.append("\t"+ NodePrinter.printNode(useStmt) + "\n");
                    }
                }
            }
        }
        System.out.println(sb.toString());
    }
}
