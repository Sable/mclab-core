package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import ast.ASTNode;
import ast.AssignStmt;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.HashMapFlowMap;

/**
 * Constructs a DU chain for a given AST
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class DUChain
{
    private Map<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> fDUMap;
    private UDChain fUDChains;
    
    public DUChain(UDChain udChains)
    {
        fUDChains = udChains;
        fUDChains.constructUDChain();
        fDUMap = new HashMap<ASTNode, HashMapFlowMap<String,HashSet<ASTNode>>>();
    }
    
    public void constructDUChain()
    {
        Map<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> resultUDMap = fUDChains.getChain();
        Set<ASTNode> useAssignmentStmts = resultUDMap.keySet();
        for (ASTNode useAssignmentStmt : useAssignmentStmts)
        {
            Set<String> varNames = resultUDMap.get(useAssignmentStmt).keySet();
            for (String varName : varNames)
            {
                Set<ASTNode> defAssignmentStmts = resultUDMap.get(useAssignmentStmt).get(varName);
                for (ASTNode defAssignStmt : defAssignmentStmts) 
                {
                    if (fDUMap.get(defAssignStmt) == null)
                    {
                        HashMapFlowMap<String, HashSet<ASTNode>> useSiteMap = new HashMapFlowMap<String, HashSet<ASTNode>>();
                        fDUMap.put(defAssignStmt, useSiteMap);
                    }
                    if (fDUMap.get(defAssignStmt).get(varName) == null)
                    {
                        HashSet<ASTNode> useAssignStmtsSet = new HashSet<ASTNode>();
                        fDUMap.get(defAssignStmt).put(varName, useAssignStmtsSet);
                    }
                    fDUMap.get(defAssignStmt).get(varName).add((ASTNode) useAssignmentStmt);
                }
            }
        }
    }
    
    public Map<ASTNode, HashMapFlowMap<String, HashSet<ASTNode>>> getChain() { return fDUMap; }
    
    public HashMapFlowMap<String, HashSet<ASTNode>> getUsesMap(ASTNode node) { return fDUMap.get(node); }
    
    public void printDUChain()
    {
        StringBuffer sb = new StringBuffer();
        LinkedList<ASTNode> visitedStmts = fUDChains.getVisitedStmtsLinkedList();
        for (ASTNode visitedStmt : visitedStmts)
        {
            sb.append("------- " + printNode(visitedStmt) + " -------\n");
            HashMapFlowMap<String, HashSet<ASTNode>> useSiteMap = fDUMap.get(visitedStmt);
            if (useSiteMap == null) continue;
            Set<String> variableNames = useSiteMap.keySet();
            for (String s : variableNames)
            {
                Set<ASTNode> useStmts = useSiteMap.get(s);
                if (!useStmts.isEmpty())
                {
                    sb.append("Var "+ s + "\n");
                    for (ASTNode useStmt : useStmts)
                    {
                        sb.append("\t"+ printNode(useStmt) + "\n");
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
