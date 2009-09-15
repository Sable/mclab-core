package natlab.toolkits.analysis.functionhandle;

import natlab.FlowAnalysisTestTool;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import natlab.toolkits.analysis.*;


public class TestAnalysis extends AbstractSimpleStructuralForwardAnalysis<HashSetFlowSet<String>> {

	public static void main(String[] args) throws Exception {
            String source = "/home/2005/jdoher1/mclab/Project/languages/Natlab/test.m";

		FlowAnalysisTestTool tool = new FlowAnalysisTestTool(source,TestAnalysis.class);
		System.out.println(tool.run());
	}
	
	
	
    public TestAnalysis(ASTNode tree){
        super( tree );
    }
	
    
    @Override
    public void caseAssignStmt(AssignStmt node) {
		copy(currentInSet, currentOutSet);
		for (String s : node.getLValues()){
			currentOutSet.add(s);
		}
    }
    
    
	@Override
	public void caseCondition(Expr condExpr) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void caseLoopVarAsCondition(AssignStmt loopVar) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void caseLoopVarAsInit(AssignStmt loopVar) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void caseLoopVarAsUpdate(AssignStmt loopVar) {
		copy(currentInSet, currentOutSet);
	}

	@Override
	public void copy(HashSetFlowSet<String> source, HashSetFlowSet<String> dest) {
		currentInSet.copy(currentOutSet);
	}

	@Override
	public void merge(HashSetFlowSet<String> in1, HashSetFlowSet<String> in2, HashSetFlowSet<String> out) {
		HashSetFlowSet<String> out2 = new HashSetFlowSet<String>();
		copy(in1,out2);
		out2.union(in2);
		copy(out2,out);
	}

	@Override
	public HashSetFlowSet<String> newInitialFlow() {
		return new HashSetFlowSet<String>();
	}

}


