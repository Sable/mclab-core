package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;
import ast.ASTNode;
import ast.Function;
import ast.Name;
import ast.NameExpr;

/**
 * Intraprocedural analysis that collects the names of variables used in statements in a function
 * @author Amine Sahibi
 *
 */
public class UsedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> implements FunctionAnalysis<StaticFunction, HashSetFlowSet<String>>
{
    private HashSetFlowSet<String> fCurrentSet;
    public Map<TIRNode, HashSetFlowSet<String>> fFlowSets = new HashMap<TIRNode, HashSetFlowSet<String>>();
    
    public UsedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
    }
    
    public UsedVariablesNameCollector(StaticFunction f)
    {
        super(f.getAst());
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentSet = newInitialFlow();
       IRCommaSeparatedListToVaribaleNamesSet(node.getArguments(), fCurrentSet);
       fFlowSets.put(node, fCurrentSet);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        fCurrentSet = newInitialFlow();
        fCurrentSet.add(node.getConditionVarName().getID());
        fFlowSets.put(node, fCurrentSet);
        caseIfStmt(node);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        fCurrentSet = newInitialFlow();
        fCurrentSet.add(node.getCondition().getName().getNodeString());
        fFlowSets.put(node, fCurrentSet);
        caseWhileStmt(node);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        fCurrentSet = newInitialFlow();
        String lower = node.getLowerName().getID();
        String increment = node.hasIncr() ? node.getIncName().getID() : null;
        String upper = node.getUpperName().getID();
        fCurrentSet.add(lower);
        if (increment != null) fCurrentSet.add(increment);
        fCurrentSet.add(upper);
        fFlowSets.put(node, fCurrentSet);
        caseForStmt(node);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        fCurrentSet = newInitialFlow();
        TIRCommaSeparatedList usedVariablesList = null;
        if (node instanceof TIRAbstractAssignToVarStmt)
        {
            Name usedVarName = new Name(node.getRHS().getVarName());
            usedVariablesList = new TIRCommaSeparatedList(new NameExpr(usedVarName));
        }
        else if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            if (node instanceof TIRArraySetStmt)
            {
                TIRArraySetStmt arraySetStmt = (TIRArraySetStmt) node;
                usedVariablesList = new TIRCommaSeparatedList(new NameExpr(arraySetStmt.getValueName()));
                usedVariablesList.add(new NameExpr(arraySetStmt.getArrayName()));
                TIRCommaSeparatedList indices = arraySetStmt.getIndizes();
                addIndicesToUsedVariablesNames(indices, usedVariablesList);
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                TIRCellArraySetStmt cellArraySetStmt = (TIRCellArraySetStmt) node;
                usedVariablesList = new TIRCommaSeparatedList(new NameExpr(cellArraySetStmt.getValueName()));
                usedVariablesList.add(new NameExpr(cellArraySetStmt.getCellArrayName()));
                TIRCommaSeparatedList indices = cellArraySetStmt.getIndizes();
                addIndicesToUsedVariablesNames(indices, usedVariablesList);
            }
            else if (node instanceof TIRDotSetStmt)
            {
                TIRDotSetStmt dotSetStmt = (TIRDotSetStmt) node;
                usedVariablesList = new TIRCommaSeparatedList(new NameExpr(dotSetStmt.getValueName()));
                usedVariablesList.add(new NameExpr(dotSetStmt.getDotName()));
            }
            else 
            { 
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }

        }
        IRCommaSeparatedListToVaribaleNamesSet(usedVariablesList, fCurrentSet);
        fFlowSets.put(node, fCurrentSet);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        fCurrentSet = newInitialFlow();
        TIRCommaSeparatedList usedVariablesList = null;
        if (node instanceof TIRArrayGetStmt)
        {
            TIRArrayGetStmt arrayGetStmt = (TIRArrayGetStmt) node;
            Name arrayName = arrayGetStmt.getArrayName();
            usedVariablesList = new TIRCommaSeparatedList(new NameExpr((arrayName)));
            TIRCommaSeparatedList indices = arrayGetStmt.getIndizes();
            addIndicesToUsedVariablesNames(indices, usedVariablesList);
        }
        else if (node instanceof TIRDotGetStmt)
        {
            TIRDotGetStmt dotGetStmt = (TIRDotGetStmt) node;
            Name dotName = dotGetStmt.getDotName();
            Name fieldName = dotGetStmt.getFieldName();
            usedVariablesList = new TIRCommaSeparatedList(new NameExpr(dotName));
            usedVariablesList.add(new NameExpr(fieldName));
        }
        else if (node instanceof TIRCellArrayGetStmt)
        {
            TIRCellArrayGetStmt cellArrayGetStmt = (TIRCellArrayGetStmt) node;
            Name cellArrayName = cellArrayGetStmt.getCellArrayName();
            usedVariablesList = new TIRCommaSeparatedList(new NameExpr((cellArrayName)));
            TIRCommaSeparatedList indices = cellArrayGetStmt.getIndices();
            addIndicesToUsedVariablesNames(indices, usedVariablesList);
        }
        if (usedVariablesList == null)
        {
            throw new NullPointerException("Used variables list cannot be empty for node " + node);
        }
        IRCommaSeparatedListToVaribaleNamesSet(usedVariablesList, fCurrentSet);
        fFlowSets.put(node, fCurrentSet);
    }
        
    @Override
    public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out)
    {
        out.addAll(in1);
        out.addAll(in2);
    }

    @Override
    public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest)
    {
        source.copy(dest);
    }
    
    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }


    public void addIndicesToUsedVariablesNames(TIRCommaSeparatedList indices, TIRCommaSeparatedList usedVariablesNames)
    {
        for (int i = 0; i < indices.size(); i++)
        {
            NameExpr indexNameExpr = indices.getNameExpresion(i);
            if (indexNameExpr != null)
            {
                usedVariablesNames.add(indexNameExpr);
            }
        }
    }
    
    public void IRCommaSeparatedListToVaribaleNamesSet(TIRCommaSeparatedList csl, HashSetFlowSet<String> variableNames)
    {
        if (csl == null) return;
        for (Name name : csl.asNameList())
        {
            variableNames.add(name.getID());
        }
    }
    
    /**
     * Returns the set of names of variables used at point P in the program
     * @param node
     * @return set of used names of variables for the input node or null if entry does not exist in flow set
     */
    public Set<String> getUsedVariablesForNode(TIRNode node)
    {
        HashSetFlowSet<String> set = fFlowSets.get(node);
        if (set == null)    return null;
        return set.getSet();
    }
    
    @Override
    public HashSetFlowSet<String> getResult() { return null; }

    @Override
    public HashSetFlowSet<String> getDefaultResult() { return null; }
    
    @Override
    public Function getTree() { return (Function)super.getTree(); }
}
