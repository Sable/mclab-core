package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.HashMapFlowMap;
import ast.ASTNode;
import ast.AssignStmt;

/**
 * Construct a UDDU web for a given AST using the AST's UD and DU chains
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class UDDUWeb
{
    private final boolean DEBUG = false;
    private UDChain fUDChain;
    private DUChain fDUChain;
    private HashMap<String, HashMap<ASTNode, Integer>> fUDWeb;
    private HashMap<String, HashMap<ASTNode, Integer>> fDUWeb;
    
    public UDDUWeb(UDChain udChain, DUChain duChain)
    {
        fUDChain = udChain;
        fDUChain = duChain;
    }
    
    public void constructUDDUWeb()
    {
        // Construct UD and DU chains
        fUDChain.constructUDChain();
        fDUChain.constructDUChain();
        
        // Initialize UDDU web
        fUDWeb = new HashMap<String, HashMap<ASTNode,Integer>>();
        fDUWeb = new HashMap<String, HashMap<ASTNode,Integer>>();
        
        // Initialize the statement color 
        int color = 0;
        
        for (ASTNode visitedStmt : fUDChain.getVisitedStmtsLinkedList())
        {
            HashMapFlowMap<String, HashSet<ASTNode>> duMap = fDUChain.getUsesMap(visitedStmt);
            if (duMap != null && !duMap.isEmpty())
            {
                for (String variableName : duMap.keySet())
                {
                    if (!isMarkedInDUWeb(visitedStmt, variableName))
                    {
                        markDefinition(visitedStmt, variableName, color);
                        color++;
                    }
                }
            }
        }
    }
    
    private void markDefinition(ASTNode visitedStmt, String variableName, Integer color)
    {
        if (fDUWeb.get(variableName) == null)
        {
            fDUWeb.put(variableName, new HashMap<ASTNode, Integer>());
        }
        fDUWeb.get(variableName).put(visitedStmt, color);
        if (DEBUG) System.out.println("Def of " + variableName + " colored with\t" + color + " in \t" + printNode(visitedStmt));
        HashSet<ASTNode> useSet = fDUChain.getUsesMap(visitedStmt).get(variableName);
        if (useSet == null) return;
        for (ASTNode use : useSet)
        {
            if (!isMarkedInUDWeb(use, variableName))
            {
                markUse(use, variableName, color);
            }
        }
    }
    
    private void markUse(ASTNode visitedStmt, String variableName, Integer color)
    {
        if (fUDWeb.get(variableName) == null)
        {
            fUDWeb.put(variableName, new HashMap<ASTNode, Integer>());
        }
        fUDWeb.get(variableName).put(visitedStmt, color);
        if(DEBUG) System.out.println("Use of " + variableName + " colored with\t" + color + " in \t" + printNode(visitedStmt));  
        HashSet<ASTNode> definitionSet = fUDChain.getDefinitionsMap(visitedStmt).get(variableName);
        if (definitionSet == null) return;
        for (ASTNode definition : definitionSet)
        {
            if (!isMarkedInDUWeb(definition, variableName))
            {
                markDefinition(definition, variableName, color);
            }
        }
    }
    
    private boolean isMarkedInDUWeb(ASTNode visitedStmt, String variableName)
    {
        if (fDUWeb.containsKey(variableName))
        {
            HashMap<ASTNode, Integer> webEntry = fDUWeb.get(variableName);
            if (webEntry != null && webEntry.containsKey(visitedStmt) && webEntry.get(visitedStmt) != null) { return true; }
        }
        return false;
    }
    
    private boolean isMarkedInUDWeb(ASTNode visitedStmt, String variableName)
    {
        if (fUDWeb.containsKey(variableName))
        {
            HashMap<ASTNode, Integer> webEntry = fUDWeb.get(variableName);
            if (webEntry != null && webEntry.containsKey(visitedStmt) && webEntry.get(visitedStmt) != null) { return true; }
        }
        return false;
    }
    
    public HashMap<ASTNode, Integer> getNodeAndColorForUse(String variableName) { return fUDWeb.get(variableName); }
    
    public HashMap<ASTNode, Integer> getNodeAndColorForDefinition(String variableName) { return fDUWeb.get(variableName); }
    
    public LinkedList<ASTNode> getVisitedStmtsLinkedList() { return fUDChain.getVisitedStmtsLinkedList(); }
    
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
