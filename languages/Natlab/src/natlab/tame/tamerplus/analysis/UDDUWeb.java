package natlab.tame.tamerplus.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tamerplus.utils.NodePrinter;
import natlab.tame.tir.TIRNode;
import ast.ASTNode;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class UDDUWeb implements TamerPlusAnalysis
{
    public static boolean DEBUG = false;
    
    private UDChain fUDChain;
    private DUChain fDUChain;
    private Table<String, TIRNode, Integer> fUDWeb = HashBasedTable.create();
    private Table<String, TIRNode, Integer> fDUWeb = HashBasedTable.create();
    
    public UDDUWeb(ASTNode<?> tree) {}

    @Override
    public void analyze(AnalysisEngine engine)
    {
        fUDChain = engine.getUDChainAnalysis();
        fDUChain = engine.getDUChainAnalysis();
        
        if (DEBUG) System.out.println("\nUse Definition Definition Use Web analysis results");
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
        fDUWeb.put(variableName, visitedStmt, color);
        
        if(DEBUG) {
          System.out.format("Def of %s colored with\t%s in \t%s\n", variableName, color,
              NodePrinter.printNode(visitedStmt));
        }
        
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
        fUDWeb.put(variableName, visitedStmt, color);
        
        if(DEBUG) {
          System.out.format("Use of %s colored with\t%s in \t%s\n", variableName, color,
              NodePrinter.printNode(visitedStmt));
        }

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
    
    private boolean isMarkedInDUWeb(TIRNode visitedStmt, String variableName)
    {
       return fDUWeb.get(variableName, visitedStmt) != null;
    }
    
    private boolean isMarkedInUDWeb(TIRNode visitedStmt, String variableName)
    {
       return fUDWeb.get(variableName, visitedStmt) != null;
    }
    
    /**
     * Returns the coloring of a variable in all the statements where it's used
     * @param variableName
     * @return map - key: use statement of the variable, value: color
     */
    public Map<TIRNode, Integer> getNodeAndColorForUse(String variableName) 
    {
        return fUDWeb.row(variableName); 
    }
    
    /**
     * Returns the coloring of a variable in all the statements where it's defined
     * @param variableName
     * @return map - key: definition statement of the variable, value: color
     */
    public Map<TIRNode, Integer> getNodeAndColorForDefinition(String variableName) 
    {
      return fDUWeb.row(variableName);
    }
    
    public LinkedList<TIRNode> getVisitedStmtsLinkedList() 
    {
        return fUDChain.getVisitedStmtsOrderedList();
    }
}
