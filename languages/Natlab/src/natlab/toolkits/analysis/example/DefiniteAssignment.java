package natlab.toolkits.analysis.example;

import java.util.Set;

import analysis.AbstractSimpleStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Function;
import ast.Name;
import ast.Script;
import ast.Stmt;

import com.google.common.collect.Sets;

public class DefiniteAssignment extends AbstractSimpleStructuralForwardAnalysis<Set<String>>
{

    public Set<String> newInitialFlow()
    {
        return Sets.newHashSet();
    }

    public DefiniteAssignment( ASTNode<?> tree )
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
        inFlowSets.put(node, Sets.newHashSet(currentInSet));
        //This line was not in the slides, it is here to make sure that the assignStmt 
        //actually owns the outset it is modifying.
        currentOutSet = copy(currentInSet);
        for( String s: node.getLValues()){
            currentOutSet.add( s );
        }
        outFlowSets.put(node, Sets.newHashSet(currentOutSet));
    }

    //This method was not in the slides. It is there to add info for each statement.
    public void caseStmt( Stmt node )
    {
        inFlowSets.put(node,currentInSet );
        currentOutSet = currentInSet;
        caseASTNode( node );
        outFlowSets.put( node, currentInSet );
    }
    
    public Set<String> copy(Set<String> source)
    {
      return Sets.newHashSet(source);
    }
    
    public Set<String> merge(Set<String> in1, Set<String> in2)
    {
      return Sets.newHashSet(Sets.intersection(in1, in2));
    }
}