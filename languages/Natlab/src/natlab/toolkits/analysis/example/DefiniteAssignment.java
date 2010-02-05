package natlab.toolkits.analysis.example;

import ast.*;
import java.util.*;
import natlab.toolkits.analysis.*;

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

        HashSetFlowSet<String> mySet = newInitialFlow();

        for( Name n: node.getInputParams() )
            mySet.add( n.getID() );
        currentOutSet = newInitialFlow();
        copy(mySet, currentOutSet);
        caseASTNode( node );
        
        
    }

    public void caseAssignStmt( AssignStmt node )
    {
        inFlowSets.put(node, currentInSet.clone() );
        //This line was not in the slides, it is here to make sure that the assignStmt 
        //actually owns the outset it is modifying.
        currentOutSet = newInitialFlow();
        copy( currentInSet, currentOutSet);
        for( String s: node.getLValues()){
            currentOutSet.add( s );
        }
        outFlowSets.put(node, currentOutSet.clone() );
    }

    //This method was not in the slides. It is there to add info for each statement.
    public void caseStmt( Stmt node )
    {
        inFlowSets.put(node,currentInSet );
        currentOutSet = currentInSet;
        caseASTNode( node );
        outFlowSets.put( node, currentInSet );
    }
    public void copy( HashSetFlowSet<String> source, HashSetFlowSet<String> dest )
    {
        source.copy(dest);
    }
    public void merge( HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out )
    {
        in1.intersection( in2, out );
    }
}