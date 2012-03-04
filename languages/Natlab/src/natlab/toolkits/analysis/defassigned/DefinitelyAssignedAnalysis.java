package natlab.toolkits.analysis.defassigned;
import analysis.*;
import analysis.natlab.*;

import ast.*;
import java.util.*;

public class DefinitelyAssignedAnalysis extends
		AbstractSimpleStructuralForwardAnalysis<AssignedFlowSet> {

    public void caseScript( Script node )
    {
        currentInSet= newInitialFlow();
        currentOutSet = currentInSet;
        node.getStmts().analyze(this);
        outFlowSets.put( node, currentOutSet.clone() );
    }

	public DefinitelyAssignedAnalysis(ASTNode tree) {
		super(tree);
	}

    @Override
    public void caseFunction( Function node )
    {
        currentInSet = newInitialFlow();
        currentOutSet = currentInSet;
        // Add input params to set
        for( Name n : node.getInputParams() ){
            currentInSet.add( n.getID());
        }
        
        // Process body
        node.getStmts().analyze(this);
        outFlowSets.put( node, currentOutSet );
        
        
        for( Function f : node.getNestedFunctions() ){
            f.analyze( this );
        }
    }


    @Override
    public void caseAssignStmt( AssignStmt node )
    {
        Expr lhs = node.getLHS();
        for( NameExpr n : lhs.getNameExpressions() ){
            currentOutSet.add( n.getName().getID() );
        }
        outFlowSets.put(node, currentOutSet);
        node.getLHS().analyze( this );
        annotateNode(node);
    }
	@Override
	public void copy(AssignedFlowSet source, AssignedFlowSet dest) {
		source.copy(dest);		
	}

	@Override
	public void merge(AssignedFlowSet in1, AssignedFlowSet in2,
			AssignedFlowSet out) {
		in1.merge(in2,out);
	}

	@Override
	public AssignedFlowSet newInitialFlow() {
		return new AssignedFlowSet();
	}
	
	public void caseStmt(Stmt node){
		annotateNode(node);
	}
    private void annotateNode(Stmt n){
    	outFlowSets.put(n, currentOutSet);
    }
}
