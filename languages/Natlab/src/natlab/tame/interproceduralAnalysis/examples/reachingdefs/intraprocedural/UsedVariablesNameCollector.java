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
import natlab.tame.tir.TIRNode;
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
    // Member variables
    private HashSetFlowSet<String> fCurrentSet;
    public Map<TIRNode, HashSetFlowSet<String>> fFlowSets = new HashMap<TIRNode, HashSetFlowSet<String>>();
    
    // Constructors
    public UsedVariablesNameCollector(ASTNode<?> tree)
    {
        super(tree);
    }
    
    public UsedVariablesNameCollector(StaticFunction f)
    {
        super(f.getAst());
    }
    
    // Case Methods
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
       fCurrentSet = newInitialFlow();
       IRCommaSeparatedListToVaribaleNamesSet(node.getArguments(), fCurrentSet);
       fFlowSets.put(node, fCurrentSet);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node) {
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
    public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out)
    {
        out.addAll(in1);
        out.addAll(in2);
    }

    @Override
    public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest)
    {
        dest = source.copy();
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
