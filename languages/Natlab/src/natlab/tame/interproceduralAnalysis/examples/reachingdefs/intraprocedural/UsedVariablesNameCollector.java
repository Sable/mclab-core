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
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractSimpleStructuralForwardAnalysis;
import natlab.toolkits.analysis.HashSetFlowSet;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.Name;
import ast.NameExpr;
import ast.RangeExpr;

/**
 * Intraprocedural analysis that collects the names of variables used in statements in a function
 * @author Amine Sahibi
 *
 */
@SuppressWarnings("rawtypes")
public class UsedVariablesNameCollector extends TIRAbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> implements FunctionAnalysis<StaticFunction, HashSetFlowSet<String>>
{
    // Member variables
    private HashSetFlowSet<String> fCurrentSet;
    public Map<ASTNode, HashSetFlowSet<String>> fFlowSets = new HashMap<ASTNode, HashSetFlowSet<String>>();
    
    // Constructors
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
                TIRCommaSeparatedList indeces = arraySetStmt.getIndizes();
                for (int i = 0; i < indeces.size(); i++)
                {
                    NameExpr indexNameExpr = indeces.getNameExpresion(i);
                    if (indexNameExpr != null)
                    {
                        usedVariablesList.add(indexNameExpr);
                    }
                }
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                TIRCellArraySetStmt cellArraySetStmt = (TIRCellArraySetStmt) node;
                usedVariablesList = new TIRCommaSeparatedList(new NameExpr(cellArraySetStmt.getValueName()));
                usedVariablesList.add(new NameExpr(cellArraySetStmt.getCellArrayName()));
                TIRCommaSeparatedList indeces = cellArraySetStmt.getIndizes();
                for (int i = 0; i < indeces.size(); i++)
                {
                    NameExpr indexNameExpr = indeces.getNameExpresion(i);
                    if (indexNameExpr != null)
                    {
                        usedVariablesList.add(indexNameExpr);
                    }
                }
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
            TIRCommaSeparatedList indeces = arrayGetStmt.getIndizes();
            for (int i = 0; i < indeces.size(); i++)
            {
                NameExpr indexNameExpr = indeces.getNameExpresion(i);
                if (indexNameExpr != null)
                {
                    usedVariablesList.add(indexNameExpr);
                }
            }
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
            TIRCommaSeparatedList indeces = cellArrayGetStmt.getIndices();
            for (int i = 0; i < indeces.size(); i++)
            {
                NameExpr indexNameExpr = indeces.getNameExpresion(i);
                if (indexNameExpr != null)
                {
                    usedVariablesList.add(indexNameExpr);
                }
            }
        }
        if (usedVariablesList == null) System.out.println("PROBLEM");
        IRCommaSeparatedListToVaribaleNamesSet(usedVariablesList, fCurrentSet);
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
    public void caseLoopVar(AssignStmt node)
    {
        fCurrentSet = newInitialFlow();
        RangeExpr range = (RangeExpr) node.getRHS();
        String lower = range.getLower().getNodeString();
        String increment = range.hasIncr() ? range.getIncr().getNodeString() : null;
        String upper = range.getUpper().getNodeString();
        fCurrentSet.add(lower);
        if (increment != null) fCurrentSet.add(increment);
        fCurrentSet.add(upper);
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

    /**
     * Turns a TIRCommaSeparatedList of Names into a set of Strings that represent variables names
     * @param csl
     * @param variableNames
     */
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
    public Set<String> getUsedVariablesForNode(ASTNode node)
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
