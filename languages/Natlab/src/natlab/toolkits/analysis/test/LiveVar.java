package natlab.toolkits.analysis.test;

import ast.*;
import java.util.*;
import analysis.*;
import natlab.toolkits.analysis.*;

public class LiveVar extends AbstractSimpleStructuralBackwardAnalysis< HashSetFlowSet< String > >
{

    public static boolean DEBUG = true;

    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }

    public LiveVar( ASTNode tree )
    {
        super(tree);
        currentOutSet = newInitialFlow();
    }

    public void caseAssignStmt( AssignStmt node )
    {
        inFlowSets.put(node, currentInSet.copy() );
        currentOutSet = newInitialFlow();

        copy(currentInSet, currentOutSet);

        HashSetFlowSet<String> gen = new HashSetFlowSet<String>();
        HashSetFlowSet<String> kill = new HashSetFlowSet<String>();
        
        for( String s : node.getLValues() ){
            kill.add( s );
        }
        for( String s : node.getRHS().getSymbols() )
            gen.add( s );

        //System.out.println( node.getRHS().getPrettyPrinted() );

        currentOutSet.difference( kill );
        currentOutSet.union( gen );

        outFlowSets.put( node, currentOutSet.copy() );
    }

    public void caseExpr( Expr node )
    {
        currentOutSet = newInitialFlow();
        copy( currentInSet, currentOutSet );
        for( String s : node.getSymbols() )
            currentOutSet.add( s );
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