package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
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
