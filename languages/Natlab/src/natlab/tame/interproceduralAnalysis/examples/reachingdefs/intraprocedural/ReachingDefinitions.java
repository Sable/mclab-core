package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import ast.ASTNode;
import ast.Name;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ReachingDefinitions extends TIRAbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<Name, Set<TIRNode>>> implements TamerPlusAnalysis
{
    public static boolean DEBUG = false;

    private DefinedVariablesNameCollector fVariableNameCollector;
    private DefiniteAssignmentAnalysis fDefiniteAssignmentAnalysis;
    private HashMapFlowMap<Name, Set<TIRNode>> fStartMap;
    private LinkedList<TIRNode> fVisitedStmts;
    
    public ReachingDefinitions(ASTNode<?> tree)
    {
        super(tree);
        fVisitedStmts = new LinkedList<TIRNode>();
        initializeStartMap();
    }
    
    public void initializeStartMap()
    {
        Merger<Set<TIRNode>> merger = Mergers.union();
        fStartMap = new HashMapFlowMap<Name, Set<TIRNode>>(merger);
        for (Name variableName : fVariableNameCollector.getDefinedVariablesFullSet())
        {
            fStartMap.put(variableName, new HashSet<TIRNode>());
        }
    }
    
    @Override
    public void analyze(AnalysisEngine engine)
    {
        fVariableNameCollector = engine.getDefinedVariablesAnalysis();
        fDefiniteAssignmentAnalysis = engine.getDefiniteAssignmentAnalysis();
        super.analyze();
    }
    
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        currentOutSet = copy(currentInSet);
        Set<Name> definedVariablesNames = fVariableNameCollector.getDefinedVariablesForNode(node);
        populateOutSetWithDefinitionSitesForNode(definedVariablesNames, currentOutSet, node);
        setInOutSet(node);
        currentInSet = copy(currentOutSet);
        fVisitedStmts.add(node);
        caseASTNode(node);
        if (DEBUG) printMapForNode(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       currentOutSet = copy(currentInSet);
       Set<Name> definedVariablesNames = fVariableNameCollector.getDefinedVariablesForNode(node);
       populateOutSetWithDefinitionSitesForNode(definedVariablesNames, currentOutSet, node);
       setInOutSet(node);
       fVisitedStmts.add(node);
       if (DEBUG) printMapForNode(node);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        currentOutSet = copy(currentInSet);
        setInOutSet(node);
        fVisitedStmts.add(node);
        caseIfStmt(node);
        if (DEBUG) printMapForNode(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        currentOutSet = copy(currentInSet);
        setInOutSet(node);
        fVisitedStmts.add(node);
        caseWhileStmt(node);
        if (DEBUG) printMapForNode(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<Name> definedVariablesNames = fVariableNameCollector.getDefinedVariablesForNode(node);
        populateOutSetWithDefinitionSitesForNode(definedVariablesNames, currentOutSet, node);
        setInOutSet(node);
        fVisitedStmts.add(node);
        // We need to make the for assign statement visible to the statements within the for loop
        currentInSet.put(definedVariablesNames.iterator().next(), Sets.<TIRNode>newHashSet(node));
        caseForStmt(node);
        if (DEBUG) printMapForNode(node);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<Name> definedVariablesNames = fVariableNameCollector.getDefinedVariablesForNode(node);
        for (Name variableName : definedVariablesNames)
        {
            // Arrays maybe implicitly defined
            if (node instanceof TIRAbstractAssignFromVarStmt && fDefiniteAssignmentAnalysis.isDefinitelyAssignedAtInputOf(node, variableName))
            {
                continue;
            }
            Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(variableName, newDefinitionSite);
        }
        setInOutSet(node);
        fVisitedStmts.add(node);
        if (DEBUG) printMapForNode(node);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<Name> definedVariablesNames = fVariableNameCollector.getDefinedVariablesForNode(node);
        populateOutSetWithDefinitionSitesForNode(definedVariablesNames, currentOutSet, node);
        setInOutSet(node);
        fVisitedStmts.add(node);
        if (DEBUG) printMapForNode(node);
    }
    
    public void populateOutSetWithDefinitionSitesForNode(Set<Name> definedVariablesNames, HashMapFlowMap<Name, Set<TIRNode>> outSet, TIRNode node)
    {
        for (Name variableName : definedVariablesNames)
        {
            Set<TIRNode> newDefinitionSite = new HashSet<TIRNode>();
            newDefinitionSite.add(node);
            currentOutSet.put(variableName, newDefinitionSite);
        }
    }
    
    @Override
    public void merge (HashMapFlowMap<Name, Set<TIRNode>> in1, HashMapFlowMap<Name, Set<TIRNode>> in2, HashMapFlowMap<Name, Set<TIRNode>> out)
    {
        Set<Name> keys = Sets.newHashSet(in1.keySet());
        keys.addAll(in2.keySet());
        for (Name s : keys)
        {
            Set<TIRNode> result = Sets.newHashSet();
            result.addAll(in1.get(s));
            result.addAll(in2.get(s));
            out.put(s, result);
        }
    }

    @Override
    public void copy(HashMapFlowMap<Name, Set<TIRNode>> in, HashMapFlowMap<Name, Set<TIRNode>> out)
    {
        if (in == out)
        {
            return;
        }
        else
        {
            out.clear();
            for (Name s : in.keySet())
            {
                out.put(s, Sets.newHashSet(in.get(s)));
            }
        }
    }
    
    private HashMapFlowMap<Name, Set<TIRNode>> copy(HashMapFlowMap<Name, Set<TIRNode>> in)
    {
        HashMapFlowMap<Name, Set<TIRNode>> out = new HashMapFlowMap<Name, Set<TIRNode>>();
        copy(in, out);
        return out;
    }
    
    @Override
    public HashMapFlowMap<Name, Set<TIRNode>> newInitialFlow()
    {
        return copy(fStartMap);
    }
    
    private void setInOutSet(TIRNode node)
    {
        associateInSet((ASTNode<?>) node, getCurrentInSet());
        associateOutSet((ASTNode<?>) node, getCurrentOutSet());
    }
    
    public Map<Name, Set<TIRNode>> getReachingDefinitionsForNode(TIRNode node)
    {
        return this.getOutFlowSets().get(node).toMap();
    }
    
    public LinkedList<TIRNode> getVisitedStmtsLinkedList()
    {
        LinkedList<TIRNode> visitedStmtsLinkedList = Lists.newLinkedList();
        Set<TIRNode> visitedStmtsSet = Sets.newHashSet();
        for (TIRNode visitedStmt : fVisitedStmts)
        {
            if (visitedStmtsSet.add(visitedStmt))
            {
                visitedStmtsLinkedList.add(visitedStmt);
            }
        }
        return visitedStmtsLinkedList;
    }
    
    private void printMapForNode(TIRNode node)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("------------ ").append(NodePrinter.printNode(node)).append(" ------------\n");
        printMapEntries(node, sb);
        System.out.println(sb.toString());
    }
    
    private void printMapEntries(TIRNode node, StringBuffer sb)
    {
        HashMapFlowMap<Name, Set<TIRNode>> NameTodefinitionSiteMap = outFlowSets.get(node);
        for (Name variableName : NameTodefinitionSiteMap.keySet())
        {
            printMapEntry(variableName, NameTodefinitionSiteMap, sb);
        }
    }
    
    private void printMapEntry(Name variableName, HashMapFlowMap<Name, Set<TIRNode>> NameTodefinitionSiteMap, StringBuffer sb)
    {
        Set<TIRNode> reachingDefs = NameTodefinitionSiteMap.get(variableName);
        if (!reachingDefs.isEmpty())
        {
            sb.append("Var ").append(NodePrinter.printNode(variableName)).append("\n");
            for (TIRNode reachingDef : reachingDefs)
            {
                sb.append("\t").append(NodePrinter.printNode(reachingDef)).append("\n");
            }
        }
    }
    
    public void printVisitedStmts()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Visited Stmts:\n");
        for (TIRNode visitedStmt : this.getVisitedStmtsLinkedList())
        {
            sb.append(NodePrinter.printNode(visitedStmt)).append("\n");
        }
        System.out.println(sb.toString());
    }
}