package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRNode;
import natlab.toolkits.analysis.HashMapFlowMap;

public class DUWeb
{
    private Map<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> fDUMap;
    private UDWeb fUDChains;
    
    public DUWeb(UDWeb udChains)
    {
        fUDChains = udChains;
        fUDChains.constructUDWeb();
        fDUMap = new HashMap<TIRNode, HashMapFlowMap<String,HashSet<TIRAbstractAssignStmt>>>();
    }
    
    public void constructDUWeb()
    {
        Map<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> resultUDMap = fUDChains.getUDMap();
        Set<TIRNode> useAssignmentStmts = resultUDMap.keySet();
        for (TIRNode useAssignmentStmt : useAssignmentStmts)
        {
            Set<String> varNames = resultUDMap.get(useAssignmentStmt).keySet();
            for (String varName : varNames)
            {
                Set<TIRAbstractAssignStmt> defAssignmentStmts = resultUDMap.get(useAssignmentStmt).get(varName);
                for (TIRAbstractAssignStmt defAssignStmt : defAssignmentStmts) 
                {
                    if (fDUMap.get(defAssignStmt) == null)
                    {
                        HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>> useSiteMap = new HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>();
                        fDUMap.put(defAssignStmt, useSiteMap);
                    }
                    if (fDUMap.get(defAssignStmt).get(varName) == null)
                    {
                        HashSet<TIRAbstractAssignStmt> useAssignStmtsSet = new HashSet<TIRAbstractAssignStmt>();
                        fDUMap.get(defAssignStmt).put(varName, useAssignStmtsSet);
                    }
                    fDUMap.get(defAssignStmt).get(varName).add((TIRAbstractAssignStmt) useAssignmentStmt);
                }
            }
        }
    }
    
    
    public void printDUWeb()
    {
        StringBuffer sb = new StringBuffer();
        for (TIRNode node : fUDChains.getReachingDefinitionsAnalysis().getVisitedAssignStmtsLinkedList())
        {
            sb.append("------- " + ((TIRAbstractAssignStmt) node).getStructureString() + " ------------\n");
            HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>> useSiteMap = fDUMap.get(node);
            if (useSiteMap == null) continue;
            Set<String> variableNames = useSiteMap.keySet();
            for (String s : variableNames)
            {
                Set<TIRAbstractAssignStmt> assignStmts = useSiteMap.get(s);
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
    public Map<TIRNode, HashMapFlowMap<String, HashSet<TIRAbstractAssignStmt>>> getDUMap()
    {
        return fDUMap;
    }
}
