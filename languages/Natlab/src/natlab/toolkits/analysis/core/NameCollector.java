package natlab.toolkits.analysis.core;

import java.util.Set;

import natlab.toolkits.analysis.HashSetFlowSet;
import analysis.AbstractDepthFirstAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.Function;
import ast.GlobalStmt;
import ast.Name;
import ast.ParameterizedExpr;

/**
 * @author Jesse Doherty
 */
public class NameCollector extends AbstractDepthFirstAnalysis<HashSetFlowSet<Name>>
{
    protected HashSetFlowSet<Name> fullSet;
    protected boolean inLHS = false;

    public NameCollector(ASTNode<?> tree)
    {
        super(tree);
        fullSet = new HashSetFlowSet<Name>();
    }

    public HashSetFlowSet<Name> newInitialFlow()
    {
        return new HashSetFlowSet<Name>();
    }

    public Set<Name> getAllNames()
    {
        return fullSet.getSet();
    }
    public Set<Name> getNames( AssignStmt node )
    {
        HashSetFlowSet<Name> set = flowSets.get(node);
        if( set == null )
            return null;
        else
            return set.getSet();
    }

    public void caseName( Name node )
    {
        if( inLHS )
            currentSet.add(node);
    }

    public void caseAssignStmt( AssignStmt node )
    {
        inLHS = true;
        currentSet = newInitialFlow();
        analyze( node.getLHS() );
        flowSets.put(node,currentSet);
        fullSet.addAll( currentSet );
        inLHS = false;
    }

    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        analyze(node.getTarget());
    }

    public void caseCellIndexExpr(CellIndexExpr node) {
      analyze(node.getTarget());
    }

    public void caseDotExpr(DotExpr node) {
      analyze(node.getTarget());
    }
    
    public void caseFunction(Function f) {
      for (Name name : f.getInputParams()) {
        currentSet.add(name);
      }
      fullSet.addAll(currentSet);
      analyze(f.getStmts());
      analyze(f.getNestedFunctions());
    }
    
    public void caseGlobalStmt(GlobalStmt node) {
      for (Name name : node.getNames()) {
        currentSet.add(name);
      }
      fullSet.addAll(currentSet);
    }
}
