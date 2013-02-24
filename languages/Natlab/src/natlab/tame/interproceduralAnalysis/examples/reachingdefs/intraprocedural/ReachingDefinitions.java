package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import ast.ASTNode;

/**
 * Perform reaching definitions analysis on a given AST
 * @author Amine Sahibi
 *
 */
public class ReachingDefinitions extends TIRAbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, Set<TIRNode>>>
{
    // Member variables
    private final boolean DEBUG = false;
    private Merger<Set<TIRNode>> fMerger = Mergers.union();
    private DefinedVariablesNameCollector fVariableNameCollector;
    private HashMapFlowMap<String, Set<TIRNode>> fStartMap;
    private LinkedList<TIRNode> fVisitedStmts;
    
    // Constructor
    public ReachingDefinitions(StaticFunction f)
    {
        super(f.getAst());
        fStartMap = new HashMapFlowMap<String, Set<TIRNode>>(fMerger);
        fVariableNameCollector = new DefinedVariablesNameCollector(f);
        fVisitedStmts = new LinkedList<TIRNode>();
        fVariableNameCollector.analyze();
        for (String variable : fVariableNameCollector.getFullSet())
        {
            fStartMap.put( variable, new HashSet<TIRNode>());
        }
    }
    
    /**
     * Merge two in sets
     */
    public void merge
    (
        HashMapFlowMap<String, Set<TIRNode>> in1,
        HashMapFlowMap<String, Set<TIRNode>> in2,
        HashMapFlowMap<String, Set<TIRNode>> out
    )
    {
        Set<String> keys = new HashSet<String>(in1.keySet());
        keys.addAll(in2.keySet());
        for (String s : keys)
        {
            Set<TIRNode> result = new HashSet<TIRNode>();
            result.addAll(in1.get(s));
            result.addAll(in2.get(s));
            out.put(s, result);
        }
    }
    
    /**
     * Return copy of a flow set
     */
    public void copy(HashMapFlowMap<String, Set<TIRNode>> in, HashMapFlowMap<String, Set<TIRNode>> out)
    {
        if (in == out)  return;
        else
        {
            out.clear();
            for (String s : in.keySet())
            {
                out.put(s, new HashSet<TIRNode>(in.get(s)));
            }
        }
    }
    
    public HashMapFlowMap<String, Set<TIRNode>> copy(HashMapFlowMap<String, Set<TIRNode>> in)
    {
        HashMapFlowMap<String, Set<TIRNode>> out = new HashMapFlowMap<String, Set<TIRNode>>();
        copy(in, out);
        return out;
    }

    // Case methods
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesForNode(node);
        for (String s : definedVariablesNames)
        {
            Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(s, newDefinitionSite);
        }
        setInOutSet(node);
        currentInSet = copy(currentOutSet);
        fVisitedStmts.add(node);
        if (DEBUG) printMapForNode(node);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       currentOutSet = copy(currentInSet);
       Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesForNode(node);
       for (String variableName : definedVariablesNames)
       {
           Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
           newDefinitionSite.add(node);
           currentOutSet.put(variableName, newDefinitionSite);
       }
       setInOutSet(node);
       if (DEBUG) printMapForNode(node);
       fVisitedStmts.add(node);
    }   
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesForNode(node);
        for (String variableName : definedVariablesNames)
        {
            Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(variableName, newDefinitionSite);
        }
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        currentOutSet = copy(currentInSet);
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        currentOutSet = copy(currentInSet);
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
        caseWhileStmt(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        currentOutSet = copy(currentInSet);
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
        caseForStmt(node);
    }
    
    
    private void setInOutSet(ASTNode<?> node)
    {
        associateInSet(node, getCurrentInSet());
        associateOutSet(node, getCurrentOutSet());
    }
    
    public LinkedList<TIRNode> getVisitedStmtsLinkedList()
    {
        LinkedList<TIRNode> visitedNodesLinkedList = new LinkedList<TIRNode>();
        HashSet<TIRNode> visitedNodesSet = new HashSet<TIRNode>();
        for (TIRNode visitedStmt : fVisitedStmts)
        {
            if (!visitedNodesSet.contains(visitedStmt))
            {
                visitedNodesSet.add(visitedStmt);
                visitedNodesLinkedList.add(visitedStmt);
            }
        }
        return visitedNodesLinkedList;
    }
    
    @Override
    public HashMapFlowMap<String, Set<TIRNode>> newInitialFlow()
    {
        return copy(fStartMap);
    }
    
    
    public DefinedVariablesNameCollector getVarNameCollector() { return fVariableNameCollector; }
    
    public void printMapForNode(TIRNode node)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("------- " + printNode(node) + " ------------\n");
        HashMapFlowMap<String, Set<TIRNode>> definitionSiteMap = outFlowSets.get(node);
        Set<String> variableNames = definitionSiteMap.keySet();
        for (String variableName : variableNames)
        {
            Set<TIRNode> reachingDefs = definitionSiteMap.get(variableName);
            if (!reachingDefs.isEmpty())
            {
                sb.append("Var "+ variableName + "\n");
                for (TIRNode reachingDef : reachingDefs)
                {
                    sb.append("\t"+ printNode(reachingDef) + "\n");
                }
            }
        }
        System.out.println(sb.toString());
    }
    
    
    public void printVisitedStmts()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Visited Stmts\n");
        HashSet<TIRNode> visitedAssignStmtsSet = new HashSet<TIRNode>();
        for (TIRNode visitedStmt : fVisitedStmts)
        {
            if (!visitedAssignStmtsSet.contains(visitedStmt))
            {
                visitedAssignStmtsSet.add(visitedStmt);
                sb.append(printNode(visitedStmt) + "\n");
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