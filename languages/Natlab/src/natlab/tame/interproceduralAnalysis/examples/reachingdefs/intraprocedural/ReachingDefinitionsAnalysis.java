package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashMapFlowMap;
import natlab.toolkits.analysis.Merger;
import natlab.toolkits.analysis.Mergers;
import ast.ASTNode;
import ast.Function;

public class ReachingDefinitionsAnalysis extends TIRAbstractSimpleStructuralForwardAnalysis<HashMapFlowMap<String, Set<TIRAbstractAssignStmt>>>
{
    private Merger<Set<TIRAbstractAssignStmt>> fMerger = Mergers.union();
    private VariableNameCollector fVariableNameCollector;
    private HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> fStartMap;
    private LinkedList<TIRAbstractAssignStmt> fVisitedAssignStmts;
    
    public ReachingDefinitionsAnalysis(StaticFunction f)
    {
        super(f.getAst());
        fStartMap = new HashMapFlowMap<String, Set<TIRAbstractAssignStmt>>(fMerger);
        fVariableNameCollector = new VariableNameCollector(f);
        fVisitedAssignStmts = new LinkedList<TIRAbstractAssignStmt>();
        fVariableNameCollector.analyze();
        for (String variable : fVariableNameCollector.getFullSet())
        {
            fStartMap.put( variable, new HashSet<TIRAbstractAssignStmt>());
        }
    }
    
    public void merge
    (
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> in1,
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> in2,
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> out
    )
    {
        Set<String> keys = new HashSet<String>(in1.keySet());
        keys.addAll(in2.keySet());
        for (String s : keys)
        {
            Set<TIRAbstractAssignStmt> result = new HashSet<TIRAbstractAssignStmt>();
            result.addAll(in1.get(s));
            result.addAll(in2.get(s));
            out.put(s, result);
        }
    }
    
    public void copy
    (
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> in,
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> out
    )
    {
        if (in == out)  return;
        else
        {
            out.clear();
            for (String s : in.keySet())
            {
                out.put(s, new HashSet<TIRAbstractAssignStmt>(in.get(s)));
            }
        }
    }
    
    public HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> copy(HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> in)
    {
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> out = new HashMapFlowMap<String, Set<TIRAbstractAssignStmt>>();
        copy(in, out);
        return out;
    }

    @Override
    public void caseFunction(Function node)
    {
        setCurrentOutSet(fStartMap);
        caseASTNode(node);
        setInOutSet(node);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       currentOutSet = copy(currentInSet);
       Set<String> definedVariablesNames = fVariableNameCollector.getNames(node);
       for (String s : definedVariablesNames)
       {
           Set<TIRAbstractAssignStmt> newDefinitionSite = new HashSet<TIRAbstractAssignStmt>();
           newDefinitionSite.add(node);
           currentOutSet.put(s, newDefinitionSite);
       }
       setInOutSet(node);
       printMapForNode(node);
       fVisitedAssignStmts.add(node);
    }   
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        currentOutSet = copy(currentInSet);
        Set<String> definedVariablesNames = fVariableNameCollector.getNames(node);
        for (String s : definedVariablesNames)
        {
            Set<TIRAbstractAssignStmt> newDefinitionSite = new HashSet<TIRAbstractAssignStmt>();
            newDefinitionSite.add(node);
            currentOutSet.put(s, newDefinitionSite);
        }
        setInOutSet(node);
        printMapForNode(node);
        fVisitedAssignStmts.add(node);
    }
    
    private void setInOutSet(ASTNode<?> node)
    {
        associateInSet(node, getCurrentInSet());
        associateOutSet(node, getCurrentOutSet());
    }
    
    public LinkedList<TIRAbstractAssignStmt> getVisitedAssignStmtsLinkedList()
    {
        LinkedList<TIRAbstractAssignStmt> visitedAssignStmtsLinkedList = new LinkedList<TIRAbstractAssignStmt>();
        HashSet<TIRAbstractAssignStmt> visitedAssignStmtsSet = new HashSet<TIRAbstractAssignStmt>();
        for (TIRAbstractAssignStmt stmt : fVisitedAssignStmts)
        {
            if (!visitedAssignStmtsSet.contains(stmt))
            {
                visitedAssignStmtsSet.add(stmt);
                visitedAssignStmtsLinkedList.add(stmt);
            }
        }
        return visitedAssignStmtsLinkedList;
    }
    
    public VariableNameCollector getVarNameCollector()
    {
        return fVariableNameCollector;
    }
    
    @Override
    public HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> newInitialFlow()
    {
        return copy(fStartMap);
    }
    
    
    public void printMapForNode(TIRAbstractAssignStmt node)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("------- " + node.getStructureString() + " ------------\n");
        HashMapFlowMap<String, Set<TIRAbstractAssignStmt>> definitionSiteMap = outFlowSets.get(node);
        Set<String> variableNames = definitionSiteMap.keySet();
        for (String s : variableNames)
        {
            Set<TIRAbstractAssignStmt> assignStmts = definitionSiteMap.get(s);
            if (!assignStmts.isEmpty())
            {
                sb.append("Var "+ s + "\n");
                for (TIRAbstractAssignStmt assignStmt : assignStmts)
                {
                    sb.append("\t"+ assignStmt.getStructureString() + "\n");
                }
            }
        }
        System.out.println(sb.toString());
    }
    
    
    public void printVisitedAssignmentStms()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Visited Assignment Stmts\n");
        HashSet<TIRAbstractAssignStmt> visitedAssignStmtsSet = new HashSet<TIRAbstractAssignStmt>();
        for (TIRAbstractAssignStmt stmt : fVisitedAssignStmts)
        {
            if (!visitedAssignStmtsSet.contains(stmt))
            {
                visitedAssignStmtsSet.add(stmt);
                sb.append(stmt.getStructureString() + "\n");
            }
        }
        System.out.println(sb.toString());
    }
    
    public void printNodeVarsDeclared()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Visited Assignment Stmts\n");
        HashSet<TIRAbstractAssignStmt> visitedAssignStmtsSet = new HashSet<TIRAbstractAssignStmt>();
        for (TIRAbstractAssignStmt stmt : fVisitedAssignStmts)
        {
            if (!visitedAssignStmtsSet.contains(stmt))
            {
                visitedAssignStmtsSet.add(stmt);
                sb.append(stmt.getStructureString() + " -> ");
                for (String var : fVariableNameCollector.getNames(stmt))
                {
                    sb.append(var + " ");
                }
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
    }
    
    
}
