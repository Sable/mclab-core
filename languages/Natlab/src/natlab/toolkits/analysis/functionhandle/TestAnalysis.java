package natlab.toolkits.analysis.functionhandle;

import natlab.FlowAnalysisTestTool;
import natlab.ast.ASTNode;
import natlab.ast.AssignStmt;
import natlab.ast.Expr;
import natlab.toolkits.analysis.*;


public class TestAnalysis extends AbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> {

	public static void main(String[] args) throws Exception {
		String source = "test.m";
		FlowAnalysisTestTool tool = new FlowAnalysisTestTool(source,TestAnalysis.class);
		System.out.println(tool.run());
	}
	
	
	
    public TestAnalysis(ASTNode tree){
        super( tree );
    }
	
    
    @Override
    public void caseAssignStmt(AssignStmt node) {
    	/*currentOutSet = new HashSetFlowSet<String>();
        copy(currentInSet, currentOutSet);
        currentOutSet.add(node.getLHS().getPrettyPrinted());
        System.out.println( node.getPrettyPrinted() );
        //System.out.println(currentInSet);
        //System.out.println(currentOutSet);
        outFlowSets.put(node, currentOutSet);
        inFlowSets.put(node, currentInSet);
        System.out.println( inFlowSets.get( node ) );
        System.out.println( outFlowSets.get( node ) );*/
        HashSetFlowSet working = new HashSetFlowSet<String>();
        HashSetFlowSet newIn = new HashSetFlowSet<String>();
        copy(currentInSet, newIn);
        copy(currentInSet, working);
        working.add( node.getLHS().getPrettyPrinted() );
        if( DEBUG )
            System.out.println( node.getPrettyPrinted() );
        currentOutSet = working;
        //currentOutSet = working.clone();
        //working.add(node.getPrettyPrinted());
        //newIn.add(node.getPrettyPrinted());
        outFlowSets.put(node, working);
        inFlowSets.put(node, newIn);
        if( DEBUG ){
            System.out.println( inFlowSets.get( node ) );
            System.out.println( outFlowSets.get( node ) );
            System.out.println( outFlowSets.size() );
        }
    }    
    
    
	@Override
	public void caseCondition(Expr condExpr) {
            //copy(currentInSet, currentOutSet);
            currentOutSet = currentInSet;
	}

	@Override
	public void caseLoopVarAsCondition(AssignStmt loopVar) {
            //copy(currentInSet, currentOutSet);
            currentOutSet = currentInSet;
	}

	@Override
	public void caseLoopVarAsInit(AssignStmt loopVar) {
            //copy(currentInSet, currentOutSet);
            currentOutSet = currentInSet;
	}

	@Override
	public void caseLoopVarAsUpdate(AssignStmt loopVar) {
            //copy(currentInSet, currentOutSet);
            currentOutSet = currentInSet;
	}

	@Override
	public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest) {
		source.copy(dest);
	}

	@Override
	public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out) {
		HashSetFlowSet<String> out2 = new HashSetFlowSet<String>();
		copy(in1,out2);
		out2.union(in2);
                out.clear();
		copy(out2,out);
	}

	@Override
	public HashSetFlowSet<String> newInitialFlow() {
		return new HashSetFlowSet<String>();
	}

}


