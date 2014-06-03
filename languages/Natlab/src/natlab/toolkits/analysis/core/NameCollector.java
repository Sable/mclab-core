package natlab.toolkits.analysis.core;

import java.util.HashSet;
import java.util.Set;

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
public class NameCollector extends AbstractDepthFirstAnalysis<Set<Name>>
{
    protected Set<Name> fullSet;
    protected boolean inLHS = false;

    public NameCollector(ASTNode<?> tree)
    {
        super(tree);
        fullSet = newInitialFlow();
    }

    @Override
    public Set<Name> newInitialFlow()
    {
        return new HashSet<>();
    }

    public Set<Name> getAllNames()
    {
        return fullSet;
    }
    public Set<Name> getNames( AssignStmt node )
    {
      return flowSets.get(node);
    }

    @Override
    public void caseName( Name node )
    {
        if( inLHS )
            currentSet.add(node);
    }

    @Override
    public void caseAssignStmt( AssignStmt node )
    {
        inLHS = true;
        currentSet = newInitialFlow();
        analyze( node.getLHS() );
        flowSets.put(node,currentSet);
        fullSet.addAll( currentSet );
        inLHS = false;
    }

    @Override
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        analyze(node.getTarget());
    }

    @Override
    public void caseCellIndexExpr(CellIndexExpr node) {
      analyze(node.getTarget());
    }

    @Override
    public void caseDotExpr(DotExpr node) {
      analyze(node.getTarget());
    }
    
    @Override
    public void caseFunction(Function f) {
      for (Name name : f.getInputParams()) {
        currentSet.add(name);
      }
      fullSet.addAll(currentSet);
      analyze(f.getStmts());
      analyze(f.getNestedFunctions());
    }
    
    @Override
    public void caseGlobalStmt(GlobalStmt node) {
      for (Name name : node.getNames()) {
        currentSet.add(name);
      }
      fullSet.addAll(currentSet);
    }
}
