package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import ast.ASTNode;
import ast.Function;

public class ReachingDefinitionsAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, Set<TIRNode>>>
{
    // Member variables
    private final boolean DEBUG = false;
    private Merger<Set<TIRNode>> fMerger = Mergers.union();
    private VariableNameCollector fVariableNameCollector;
    private HashMapFlowMap<String, Set<TIRNode>> fStartMap;
    private LinkedList<TIRNode> fVisitedNodes;
    
    // Constructor
    public ReachingDefinitionsAnalysis(StaticFunction f)
    {
        super(f.getAst());
        fStartMap = new HashMapFlowMap<String, Set<TIRNode>>(fMerger);
        fVariableNameCollector = new VariableNameCollector(f);
        fVisitedNodes = new LinkedList<TIRNode>();
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
        //setCurrentOutSet(fStartMap);
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesUpToNode(node);
        for (String s : definedVariablesNames)
        {
            Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(s, newDefinitionSite);
        }
        setInOutSet(node);
        currentInSet = copy(currentOutSet);
        fVisitedNodes.add(node);
        if (DEBUG) printMapForNode(node);
        caseASTNode(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       currentOutSet = copy(currentInSet);
       Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesUpToNode(node);
       for (String s : definedVariablesNames)
       {
           Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
           newDefinitionSite.add(node);
           currentOutSet.put(s, newDefinitionSite);
       }
       setInOutSet(node);
       if (DEBUG) printMapForNode(node);
       fVisitedNodes.add(node);
    }   
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesUpToNode(node);
        for (String s : definedVariablesNames)
        {
            Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(s, newDefinitionSite);
        }
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedNodes.add(node);
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
        for (TIRNode node : fVisitedNodes)
        {
            if (!visitedNodesSet.contains(node))
            {
                visitedNodesSet.add(node);
                visitedNodesLinkedList.add(node);
            }
        }
        return visitedNodesLinkedList;
    }
    
    @Override
    public HashMapFlowMap<String, Set<TIRNode>> newInitialFlow()
    {
        return copy(fStartMap);
    }
    
    
    public VariableNameCollector getVarNameCollector() { return fVariableNameCollector; }
    
    public void printMapForNode(TIRNode node)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("------- " + printNode(node) + " ------------\n");
        HashMapFlowMap<String, Set<TIRNode>> definitionSiteMap = outFlowSets.get(node);
        Set<String> variableNames = definitionSiteMap.keySet();
        for (String s : variableNames)
        {
            Set<TIRNode> nodes = definitionSiteMap.get(s);
            if (!nodes.isEmpty())
            {
                sb.append("Var "+ s + "\n");
                for (TIRNode n : nodes)
                {
                    sb.append("\t"+ printNode(n) + "\n");
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
        for (TIRNode visitedNode : fVisitedNodes)
        {
            if (!visitedAssignStmtsSet.contains(visitedNode))
            {
                visitedAssignStmtsSet.add(visitedNode);
                sb.append(printNode(visitedNode) + "\n");
            }
        }
        System.out.println(sb.toString());
    }

    
    private String printNode(TIRNode node)
    {
        if (node instanceof TIRAbstractAssignStmt) return ((TIRAbstractAssignStmt) node).getStructureString();
        else if (node instanceof TIRFunction) return ((TIRFunction) node).getStructureString().split("\n")[0];
        else return null;
    }
}
