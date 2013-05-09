package natlab.toolkits.analysis.example;

import natlab.toolkits.analysis.HashSetFlowSet;
import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.Name;
import ast.Script;
import ast.Stmt;

public class DefiniteAssignment extends AbstractSimpleStructuralForwardAnalysis< HashSetFlowSet<String> >
{

    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }

    public DefiniteAssignment( ASTNode tree )
    {
        super(tree);
        currentOutSet = newInitialFlow();
    }

    public void caseFunction( Function node ){
        currentOutSet = newInitialFlow();
        for( Name n: node.getInputParams() )
            currentOutSet.add( n.getID() );
        caseASTNode( node );
        outFlowSets.put(node, currentOutSet);
    }

    public void caseScript( Script node ){
        currentOutSet = newInitialFlow();
        caseASTNode( node );   
        outFlowSets.put(node, currentOutSet);
    }
    
    public void caseAssignStmt( AssignStmt node )
    {
        inFlowSets.put(node, currentInSet.copy() );
        //This line was not in the slides, it is here to make sure that the assignStmt 
        //actually owns the outset it is modifying.
        currentOutSet = copy(currentInSet);
        for( String s: node.getLValues()){
            currentOutSet.add( s );
        }
        outFlowSets.put(node, currentOutSet.copy() );
    }

    //This method was not in the slides. It is there to add info for each statement.
    public void caseStmt( Stmt node )
    {
        inFlowSets.put(node,currentInSet );
        currentOutSet = currentInSet;
        caseASTNode( node );
        outFlowSets.put( node, currentInSet );
    }
    
    public HashSetFlowSet<String> copy( HashSetFlowSet<String> source)
    {
      return source.copy();
    }
    
    public HashSetFlowSet<String> merge( HashSetFlowSet<String> in1, HashSetFlowSet<String> in2)
    {
        HashSetFlowSet<String> out = new HashSetFlowSet<String>();
        in1.intersection( in2, out );
        return out;
    }
}