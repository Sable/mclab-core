package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.toolkits.analysis.HashMapFlowMap;

/**
 * Construct a UDDU web for a given AST using the AST's UD and DU chains
 * @author Amine Sahibi
 *
 */
public class UDDUWeb
{
    public static boolean DEBUG = false;
    private UDChain fUDChain;
    private DUChain fDUChain;
    private HashMap<String, HashMap<TIRNode, Integer>> fUDWeb;
    private HashMap<String, HashMap<TIRNode, Integer>> fDUWeb;
    
    public UDDUWeb(UDChain udChain, DUChain duChain)
    {
        fUDChain = udChain;
        fDUChain = duChain;
    }
    
    public void constructUDDUWeb()
    {
        initializeUDDUChains();
        
        initializeUDAndDUWeb();
        
        int color = 0;
        
        for (TIRNode visitedStmt : fUDChain.getVisitedStmtsLinkedList())
        {
            HashMapFlowMap<String, HashSet<TIRNode>> duMap = fDUChain.getUsesMap(visitedStmt);
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
    
    public void initializeUDDUChains()
    {
        fUDChain.constructUDChain();
        fDUChain.constructDUChain();
    }
    
    public void initializeUDAndDUWeb()
    {
        fUDWeb = new HashMap<String, HashMap<TIRNode,Integer>>();
        fDUWeb = new HashMap<String, HashMap<TIRNode,Integer>>();
    }
    
    private void markDefinition(TIRNode visitedStmt, String variableName, Integer color)
    {
        if (fDUWeb.get(variableName) == null)
        {
            fDUWeb.put(variableName, new HashMap<TIRNode, Integer>());
        }
        
        fDUWeb.get(variableName).put(visitedStmt, color);
        
        if (DEBUG) System.out.println("Def of " + variableName + " colored with\t" + color + " in \t" + printNode(visitedStmt));
        
        HashSet<TIRNode> useSet = fDUChain.getUsesMap(visitedStmt).get(variableName);
        
        if (useSet == null)
        {
            return;
        }
        
        for (TIRNode use : useSet)
        {
            if (!isMarkedInUDWeb(use, variableName))
            {
                markUse(use, variableName, color);
            }
        }
    }
    
    private void markUse(TIRNode visitedStmt, String variableName, Integer color)
    {
        if (fUDWeb.get(variableName) == null)
        {
            fUDWeb.put(variableName, new HashMap<TIRNode, Integer>());
        }
        
        fUDWeb.get(variableName).put(visitedStmt, color);
        
        if(DEBUG) System.out.println("Use of " + variableName + " colored with\t" + color + " in \t" + printNode(visitedStmt));
        
        HashSet<TIRNode> definitionSet = fUDChain.getDefinitionsMap(visitedStmt).get(variableName);
        
        if (definitionSet == null)
        {
            return;
        }
        
        for (TIRNode definition : definitionSet)
        {
            if (!isMarkedInDUWeb(definition, variableName))
            {
                markDefinition(definition, variableName, color);
            }
        }
    }
    
    private boolean isMarkedInDUWeb(TIRNode visitedStmt, String variableName)
    {
        if (fDUWeb.containsKey(variableName))
        {
            HashMap<TIRNode, Integer> webEntry = fDUWeb.get(variableName);
            if (webEntry != null && webEntry.containsKey(visitedStmt) && webEntry.get(visitedStmt) != null)
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean isMarkedInUDWeb(TIRNode visitedStmt, String variableName)
    {
        if (fUDWeb.containsKey(variableName))
        {
            HashMap<TIRNode, Integer> webEntry = fUDWeb.get(variableName);
            if (webEntry != null && webEntry.containsKey(visitedStmt) && webEntry.get(visitedStmt) != null)
            {
                return true; 
            }
        }
        return false;
    }
    
    public HashMap<TIRNode, Integer> getNodeAndColorForUse(String variableName) { return fUDWeb.get(variableName); }
    
    public HashMap<TIRNode, Integer> getNodeAndColorForDefinition(String variableName) { return fDUWeb.get(variableName); }
    
    public LinkedList<TIRNode> getVisitedStmtsLinkedList() { return fUDChain.getVisitedStmtsLinkedList(); }
    
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
