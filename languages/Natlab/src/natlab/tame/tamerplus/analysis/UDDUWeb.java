package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tir.TIRNode;
import ast.ASTNode;

import com.google.common.collect.Maps;

public class UDDUWeb implements TamerPlusAnalysis
{
    public static boolean DEBUG = false;
    
    private UDChain fUDChain;
    private DUChain fDUChain;
    private HashMap<String, HashMap<TIRNode, Integer>> fUDWeb;
    private HashMap<String, HashMap<TIRNode, Integer>> fDUWeb;
    
    public UDDUWeb(ASTNode<?> tree)
    {
        fUDWeb = Maps.newHashMap();
        fDUWeb = Maps.newHashMap();
    }

    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUDChain = engine.getUDChainAnalysis();
        fDUChain = engine.getDUChainAnalysis();
        
        if (DEBUG) System.err.println("Use Definition Definition Use Web analysis results");
        constructUDDUWeb();
    }
    
    private void constructUDDUWeb()
    {
        int color = 0;
        
        for (TIRNode visitedStmt : fUDChain.getVisitedStmtsOrderedList())
        {
            HashMap<String, HashSet<TIRNode>> duMap = fDUChain.getUsesMapForDefinitionStmt(visitedStmt);
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
    
    private void markDefinition(TIRNode visitedStmt, String variableName, Integer color)
    {
        createNewEntryInDUWebForVariable(variableName);
        
        fDUWeb.get(variableName).put(visitedStmt, color);
        
        if(DEBUG) System.out.println((new StringBuilder("Def of ")).append(variableName)
                .append(" colored with\t").append(color).append(" in \t").append(NodePrinter.printNode(visitedStmt))); 
        
        HashSet<TIRNode> useSet = fDUChain.getUsesMapForDefinitionStmt(visitedStmt).get(variableName);
        
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
        createNewEntryInUDWebForVariable(variableName);
        
        fUDWeb.get(variableName).put(visitedStmt, color);
        
        if(DEBUG) System.out.println((new StringBuilder("Use of ")).append(variableName)
                .append(" colored with\t").append(color).append(" in \t").append(NodePrinter.printNode(visitedStmt)));
        
        Set<TIRNode> definitionSet = fUDChain.getDefinitionsMapFoUseStmt(visitedStmt).get(variableName);
        
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
    
    private void createNewEntryInUDWebForVariable(String variableName)
    {
        if (fUDWeb.get(variableName) == null)
        {
            fUDWeb.put(variableName, new HashMap<TIRNode, Integer>());
        }
    }
    
    private void createNewEntryInDUWebForVariable(String variableName)
    {
        if (fDUWeb.get(variableName) == null)
        {
            fDUWeb.put(variableName, new HashMap<TIRNode, Integer>());
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
    
    public HashMap<TIRNode, Integer> getNodeAndColorForUse(String variableName) 
    {
        return fUDWeb.get(variableName); 
    }
    
    public HashMap<TIRNode, Integer> getNodeAndColorForDefinition(String variableName) 
    {
        return fDUWeb.get(variableName); 
    }
    
    public LinkedList<TIRNode> getVisitedStmtsLinkedList() 
    {
        return fUDChain.getVisitedStmtsOrderedList();
    }
    
}
