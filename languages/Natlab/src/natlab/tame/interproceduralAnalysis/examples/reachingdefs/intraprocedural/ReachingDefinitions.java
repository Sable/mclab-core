package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;

/**
 * Perform reaching definitions analysis on a given AST
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class ReachingDefinitions extends TIRAbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, Set<ASTNode>>>
{
    // Member variables
    private final boolean DEBUG = false;
    private Merger<Set<ASTNode>> fMerger = Mergers.union();
    private DefinedVariablesNameCollector fVariableNameCollector;
    private DefiniteAssignmentAnalysis fDefiniteAssignmentAnalysis;
    private HashMapFlowMap<String, Set<ASTNode>> fStartMap;
    private LinkedList<ASTNode> fVisitedStmts;
    
    // Constructor
    public ReachingDefinitions(StaticFunction f)
    {
        super(f.getAst());
        fStartMap = new HashMapFlowMap<String, Set<ASTNode>>(fMerger);
        fVisitedStmts = new LinkedList<ASTNode>();
        
        fVariableNameCollector = new DefinedVariablesNameCollector(f);
        fVariableNameCollector.analyze();
        
        fDefiniteAssignmentAnalysis = new DefiniteAssignmentAnalysis(f, fVariableNameCollector);
        fDefiniteAssignmentAnalysis.analyze();
        
        for (String variable : fVariableNameCollector.getFullSet())
        {
            fStartMap.put( variable, new HashSet<ASTNode>());
        }
    }
    
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesForNode(node);
        for (String s : definedVariablesNames)
        {
            Set<ASTNode> newDefinitionSite = new HashSet<ASTNode>();
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
           Set<ASTNode> newDefinitionSite = new HashSet<ASTNode>();
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
            if (node instanceof TIRAbstractAssignFromVarStmt && fDefiniteAssignmentAnalysis.isDefinitelyAssignedAtInputOf(node, variableName))
            {
                continue;
            }
            Set<ASTNode> newDefinitionSite = new HashSet<ASTNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(variableName, newDefinitionSite);
        }
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesForNode(node);
        for (String variableName : definedVariablesNames)
        {
            Set<ASTNode> newDefinitionSite = new HashSet<ASTNode>();
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
    public void caseLoopVar(AssignStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getDefinedVariablesNamesForNode(node);
        for (String variableName : definedVariablesNames)
        {
            Set<ASTNode> newDefinitionSite = new HashSet<ASTNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(variableName, newDefinitionSite);
        }
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
    }
    
    @Override
    public void caseForStmt(ForStmt node)
    {
        currentOutSet = copy(currentInSet);
        setInOutSet(node);
        if (DEBUG) printMapForNode(node);
        fVisitedStmts.add(node);
        super.caseForStmt(node);
    }
    
    public void merge
    (
        HashMapFlowMap<String, Set<ASTNode>> in1,
        HashMapFlowMap<String, Set<ASTNode>> in2,
        HashMapFlowMap<String, Set<ASTNode>> out
    )
    {
        Set<String> keys = new HashSet<String>(in1.keySet());
        keys.addAll(in2.keySet());
        for (String s : keys)
        {
            Set<ASTNode> result = new HashSet<ASTNode>();
            result.addAll(in1.get(s));
            result.addAll(in2.get(s));
            out.put(s, result);
        }
    }

    public void copy(HashMapFlowMap<String, Set<ASTNode>> in, HashMapFlowMap<String, Set<ASTNode>> out)
    {
        if (in == out)  return;
        else
        {
            out.clear();
            for (String s : in.keySet())
            {
                out.put(s, new HashSet<ASTNode>(in.get(s)));
            }
        }
    }
    
    public HashMapFlowMap<String, Set<ASTNode>> copy(HashMapFlowMap<String, Set<ASTNode>> in)
    {
        HashMapFlowMap<String, Set<ASTNode>> out = new HashMapFlowMap<String, Set<ASTNode>>();
        copy(in, out);
        return out;
    }
    
    private void setInOutSet(ASTNode<?> node)
    {
        associateInSet(node, getCurrentInSet());
        associateOutSet(node, getCurrentOutSet());
    }
    
    public LinkedList<ASTNode> getVisitedStmtsLinkedList()
    {
        LinkedList<ASTNode> visitedNodesLinkedList = new LinkedList<ASTNode>();
        HashSet<ASTNode> visitedNodesSet = new HashSet<ASTNode>();
        for (ASTNode visitedStmt : fVisitedStmts)
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
    public HashMapFlowMap<String, Set<ASTNode>> newInitialFlow()
    {
        return copy(fStartMap);
    }
    
    
    public DefinedVariablesNameCollector getVarNameCollector() { return fVariableNameCollector; }
    
    public void printMapForNode(ASTNode node)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("------- " + printNode(node) + " ------------\n");
        HashMapFlowMap<String, Set<ASTNode>> definitionSiteMap = outFlowSets.get(node);
        Set<String> variableNames = definitionSiteMap.keySet();
        for (String variableName : variableNames)
        {
            Set<ASTNode> reachingDefs = definitionSiteMap.get(variableName);
            if (!reachingDefs.isEmpty())
            {
                sb.append("Var "+ variableName + "\n");
                for (ASTNode reachingDef : reachingDefs)
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
        HashSet<ASTNode> visitedAssignStmtsSet = new HashSet<ASTNode>();
        for (ASTNode visitedStmt : fVisitedStmts)
        {
            if (!visitedAssignStmtsSet.contains(visitedStmt))
            {
                visitedAssignStmtsSet.add(visitedStmt);
                sb.append(printNode(visitedStmt) + "\n");
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